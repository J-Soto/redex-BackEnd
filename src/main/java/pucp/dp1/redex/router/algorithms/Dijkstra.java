package pucp.dp1.redex.router.algorithms;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.router.AirportsMap;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Service
public class Dijkstra {
	@Autowired
	private AirportsMap airportMap;
	private Map<Airport, List<Flight>> map;

	public Map<Airport, List<Flight>> getMap() {
		map=airportMap.getGraph();
		return map;
	}

	private static void CalculateMinimumDistance(Node evaluationNode, Double edgeWeigh, Node sourceNode, Flight flight) {
		Double sourceDistance = sourceNode.getDistance();
		if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
			evaluationNode.setDistance(sourceDistance + edgeWeigh);
			LinkedList<Pair<Node, FlightPlan>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
			FlightPlan aux = new FlightPlan();
			aux.setFlight(flight);
			Pair<Node,FlightPlan> pair = new Pair<Node,FlightPlan>(sourceNode,aux);
			shortestPath.add(pair);
			evaluationNode.setShortestPath(shortestPath);
		}
	}

	private static Node getLowestDistanceNode(Set< Node > unsettledNodes) {
	    Node lowestDistanceNode = null;
	    double lowestDistance = Double.MAX_VALUE;
	    for (Node node: unsettledNodes) {
	        double nodeDistance = node.getDistance();
	        if (nodeDistance < lowestDistance) {
	            lowestDistance = nodeDistance;
	            lowestDistanceNode = node;
	        }
	    }
	    return lowestDistanceNode;
	}

	public Graph calculateShortestPathFromSource(Graph graph, Node source) {
		//Distancia del inicial igual a 0
		source.setDistance(0.0);

	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();

	    unsettledNodes.add(source);

	    while (unsettledNodes.size() != 0) {
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (Map.Entry< Node, Pair<Double, Flight>> adjacencyPair:
	          currentNode.getAdjacentNodes().entrySet()) {
	            Node adjacentNode = adjacencyPair.getKey();
	            Double edgeWeight = adjacencyPair.getValue().getKey();
	            Flight f = adjacencyPair.getValue().getValue();
	            //Comprobar consistencia de horas
	            Boolean consistent = true;
	            Integer listSize=currentNode.getShortestPath().size();
	            if(listSize>0) {
	            	consistent=currentNode.getShortestPath().get(listSize-1).getValue().getFlight().getArrivalTime().toLocalTime().isBefore(f.getTakeOffTime().toLocalTime());
	            	edgeWeight= edgeWeight + durationBetweenTime(currentNode.getShortestPath().get(listSize-1).getValue()
							.getFlight().getArrivalTime().toLocalTime(),f.getTakeOffTime().toLocalTime());
	            }
	            if (!settledNodes.contains(adjacentNode) && consistent) {
	                CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode, f);
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return graph;
	}

	public Node getShortestPath(Integer start, Integer objetive){
		//List<Flight> result=new ArrayList<>();
		Map<Airport, List<Flight>> graphOld = this.getMap();
		Map<Integer, Node> nodes = new HashMap<>();
		Graph graphNew = new Graph();
		//Create nodes
		for (Airport airport: graphOld.keySet()) {
			Node n = new Node(airport.getId());
			nodes.put(airport.getId(), n);
		}

		for(Airport airport: graphOld.keySet()) {
			List<Flight> flights = graphOld.get(airport);
			for (Flight f: flights) {
				double duration = durationBetweenTime(f.getTakeOffTime().toLocalTime(),
						f.getArrivalTime().toLocalTime());
				nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()),duration, f);
			}
			graphNew.addNode(nodes.get(airport.getId()));
		}

		Graph graphResult = calculateShortestPathFromSource(graphNew, nodes.get(start));

		Node end=new Node(0);
		//Boolean found=false;
		for (Node node: graphResult.getNodes()) {
			//end=node;
			if(node.getId().equals(objetive)) {
				//found=true;
				end=node;
				break;
			}
		}

		return end;

		/*if(found) {
			for(Pair<Node,Flight> p : end.getShortestPath()) {
				result.add(p.getValue());
			}
		}
		else {
			System.out.println("Error en obtener nodo");
		}

		return result; */
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

/*
	public Graph getShortestPathGraph(Integer start){
		Map<Airport, List<Flight>> graphOld = this.getMap();
		Map<Integer, Node> nodes = new HashMap<>();
		Graph graphNew = new Graph();
		//Create nodes
		for (Airport airport: graphOld.keySet()) {
			Node n = new Node(airport.getId());
			nodes.put(airport.getId(), n);
		}

		for(Airport airport: graphOld.keySet()) {
			List<Flight> flights = graphOld.get(airport);
			for (Flight f: flights) {
				double duration = Duration.between(f.getTakeOffDateTime(),f.getArrivalDateTime()).toMinutes();
				nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()),duration, f);
			}
			graphNew.addNode(nodes.get(airport.getId()));
		}

		Graph graphResult = calculateShortestPathFromSource(graphNew, nodes.get(start));

		return graphResult;
	}
*/

}