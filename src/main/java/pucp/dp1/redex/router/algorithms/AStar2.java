package pucp.dp1.redex.router.algorithms;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javafx.util.Pair;
import pucp.dp1.redex.dao.sales.IIncident;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.route.FlightPlanStatus;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.model.route.RoutePlanStatus;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.model.sales.Client;
import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.sales.Incident;
import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.PackageStatus;
import pucp.dp1.redex.model.storage.StorageRegister;
import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.router.AirportsMap;
import pucp.dp1.redex.services.impl.route.FlightPlanService;
import pucp.dp1.redex.services.impl.sales.AirportService;
import pucp.dp1.redex.services.impl.sales.DispatchService;
import pucp.dp1.redex.services.impl.storage.StorageRegisterService;
import pucp.dp1.redex.services.impl.storage.WarehouseService;

@Service
public class AStar2 {
    @Autowired
    private AirportsMap airportMap;
    @Autowired
    private FlightPlanService serviceFlightPlan;
    @Autowired
    private WarehouseService serviceWarehouse;
    @Autowired
    private StorageRegisterService serviceStorage;
    @Autowired
    private AirportService serviceAirport;
    @Autowired
    private DispatchService serviceDispatch;
    @Autowired
    private IIncident daoIncident;

    private Map<Airport, List<Flight>> map;

    public Map<Airport, List<Flight>> getMap() {
        map = airportMap.getGraph();
        return map;
    }

    public void CalculateMinimumDistance(Node evaluationNode, Double edgeWeigh, Node sourceNode, Flight flight) {
        Double sourceDistance = sourceNode.getDistance();
        if (sourceDistance + sourceNode.getHeuristic() + edgeWeigh < evaluationNode.getDistance()
                + evaluationNode.getHeuristic()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Pair<Node, FlightPlan>> shortestPath = new LinkedList<Pair<Node, FlightPlan>>(sourceNode.getShortestPath());
            Integer listSize = shortestPath.size();
            FlightPlan flightPlan = new FlightPlan();
            flightPlan.setFlight(flight);
            Pair<Node, FlightPlan> pair = new Pair<Node, FlightPlan>(sourceNode, flightPlan);
            shortestPath.add(pair);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    public Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;
        for (Node node : unsettledNodes) {
            double nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    public Graph calculateShortestPathFromSource(Graph graph, Node source) {
        // Distancia del inicial igual a 0
        source.setDistance(0.0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);
        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Entry<Node, Pair<Double, Flight>> adjacencyPair :
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue().getKey();
                Flight f = adjacencyPair.getValue().getValue();
                // Comprobar consistencia de horas
                /* Ahora siempre será consistente */
                Boolean consistent = true;
                Integer listSize = currentNode.getShortestPath().size();
                if (listSize > 0) {
                    consistent=currentNode.getShortestPath().get(listSize-1).getValue().getFlight().getArrivalTime().toLocalTime().isBefore(f.getTakeOffTime().toLocalTime());
                    // Agregar el tiempo de espera hasta el despegue
                    edgeWeight += durationBetweenTime(currentNode.getShortestPath().get(listSize - 1).getValue()
                            .getFlight().getArrivalTime().toLocalTime(), f.getTakeOffTime().toLocalTime());
                }
                if (!settledNodes.contains(adjacentNode) && consistent ) {
                    CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode, f);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    public Node getShortestPath(Integer start, Integer objective) {
        // List<Flight> result=new ArrayList<>();
        Map<Airport, List<Flight>> graphOld = this.getMap();
        Map<Integer, Node> nodes = new HashMap<>();
        Graph graphNew = new Graph();
        Airport objectiveAirport = null;
        //LocalTime timeActual = LocalTime.now();
        // Create nodes
        for (Airport airport : graphOld.keySet()) {
            if (objective.equals(airport.getId())) {
                objectiveAirport = airport;
            }
            Node n = new Node(airport.getId());
            nodes.put(airport.getId(), n);
        }

        if (objectiveAirport == null) {
            System.out.println("No se esta encontrando el aeropuerto objetivo");
        }

        for (Airport airport : graphOld.keySet()) {
            List<Flight> flights = graphOld.get(airport);
            for (Flight f : flights) {
                double duration = durationBetweenTime(f.getTakeOffTime().toLocalTime(),
                        f.getArrivalTime().toLocalTime());
                nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()), duration, f);
            }
            // distancia = raiz((x2-x1)^2 + (y2-y1)^2)
            Double distance = Math.pow(Math.pow(airport.getLatitude() - objectiveAirport.getLatitude(), 2)
                    + Math.pow(airport.getLongitude() - objectiveAirport.getLongitude(), 2), 0.5);
            nodes.get(airport.getId()).setHeuristic(distance);
            graphNew.addNode(nodes.get(airport.getId()));
        }

        Graph graphResult = calculateShortestPathFromSource(graphNew, nodes.get(start));
        Node end = new Node(0);
        // Boolean found=false;
        for (Node node : graphResult.getNodes()) {
            // end=node;
            if (node.getId().equals(objective)) {
                // found=true;
                end = node;
                break;
            }
        }

        return end;
    }

    public LocalDate convertStringToLocalDate(String date) {
        try {
            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
            Date dateDate;
            dateDate = formatterDate.parse(date);
            return convertToLocalDateViaInstant(dateDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public LocalTime convertStringToLocalTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(time, formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double durationBetweenTime(LocalTime start, LocalTime end) {
        if (end.isAfter(start)) {
            return Duration.between(start, end).toMinutes();
        } else {
            double acumulator;
            acumulator = Duration.between(start, LocalTime.parse("23:59:59")).toMinutes();
            acumulator += Duration.between(LocalTime.parse("00:00:00"), end).toMinutes();
            return acumulator;
        }
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateToConvert));
    }

    public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    public Date convertDateAndTimeToDate(Date arrivalDate, Time arrivalTime) {
        try {
            String time = arrivalTime.toString();
            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatterDate.format(arrivalDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String concatenado;
            concatenado = date.concat(" ").concat(time);
            // System.out.println(date.concat(" ").concat(time).concat("
            // ").concat(concatenado));
            return formatter.parse(concatenado);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
