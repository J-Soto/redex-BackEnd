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
public class AStar {
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

	public void CalculateMinimumDistance(Node evaluationNode, Double edgeWeigh, Node sourceNode, Flight flight,
			LocalDate date, LocalTime time, boolean simulated) {
		Double sourceDistance = sourceNode.getDistance();
		if (sourceDistance + sourceNode.getHeuristic() + edgeWeigh < evaluationNode.getDistance()
				+ evaluationNode.getHeuristic()) {
			evaluationNode.setDistance(sourceDistance + edgeWeigh);
			LinkedList<Pair<Node, FlightPlan>> shortestPath = new LinkedList<Pair<Node, FlightPlan>>(
					sourceNode.getShortestPath());
			Integer listSize = shortestPath.size();
			/* Date actual */
			if (listSize == 0) {
				if (!time.isBefore(flight.getTakeOffTime().toLocalTime())) {
					date = date.plusDays(1);
				}
			} else {
				date = convertToLocalDateViaInstant(shortestPath.get(listSize - 1).getValue().getArrivalDate());
				/* Determinar si tiene que ser el día siguiente */
				if (!shortestPath.get(listSize - 1).getValue().getFlight().getArrivalTime().toLocalTime()
						.isBefore(flight.getTakeOffTime().toLocalTime())) {
					date = date.plusDays(1);
				}
			}
			Optional<FlightPlan> existent = serviceFlightPlan.findByFlight_IdFlightAndTakeOffDate(flight.getIdFlight(),convertToDateViaSqlDate(date));
			FlightPlan flightPlan;
			if (existent.isPresent()) {
				flightPlan = existent.get();
				/* Consultar capacidad */
				Boolean isFull;
				if(simulated){
					isFull=flightPlan.getFullSimulated();
				}
				else {
					isFull=flightPlan.getFull();
				}
				if (!isFull) {
					/* Consultar si el aeropuerto tiene espacio */
					Optional<Warehouse> oWarehouse = serviceWarehouse
							.findByAirport_id(flight.getArrivalAirport().getId());
					if (oWarehouse.isPresent()) {
						Warehouse warehouse = oWarehouse.get();
						Date dateStorage;
						Date arrivalDate = flightPlan.getArrivalDate();
						Time arrivalTime = flightPlan.getFlight().getArrivalTime();
						dateStorage = convertDateAndTimeToDate(arrivalDate, arrivalTime);
						if (dateStorage == null) {
							System.out.println("Error en conversión");
						} else {
							/*Calendar c = Calendar.getInstance();
							c.setTime(dateStorage);
							c.add(Calendar.HOUR, -5);
							Date dateStoragePatch=c.getTime();
							System.out.println(dateStorage);*/
							List<StorageRegister> listRegisters = serviceStorage.findAllPresentOnDate(dateStorage/*Patch*/,
									warehouse.getId());
							if (listRegisters.size() < warehouse.getCapacity()) {
								/* Si hay espacio */
								Pair<Node, FlightPlan> pair = new Pair<Node, FlightPlan>(sourceNode, flightPlan);
								shortestPath.add(pair);
								evaluationNode.setShortestPath(shortestPath);
							} else {
								//System.out.println("No hay espacio en almacen");
								Incident incident = new Incident();
								incident.setAirport(warehouse.getAirport());
								incident.setRegisterDate(convertToDateViaSqlDate(convertToLocalDateViaInstant(dateStorage)));
								incident.setDescription("FULL WAREHOUSE");
								incident.setActive(true);
								incident.setSimulated(simulated);
								incident.setRegisterDate(dateStorage);
								this.daoIncident.save(incident);
							}
						}
					} else {
						System.out.println("No existe Warehouse?");
					}
				} else {
					//System.out.println("Vuelo lleno");
					Incident incident = new Incident();
					incident.setFlightPlan(flightPlan);
					incident.setRegisterDate(flightPlan.getTakeOffDate());
					incident.setDescription("FULL FLIGHT PLAN");
					incident.setActive(true);
					incident.setSimulated(simulated);
					this.daoIncident.save(incident);
				}
			} else {
				/* Crear */
				flightPlan = new FlightPlan();
				flightPlan.setFlight(flight);
				flightPlan.setTakeOffDate(convertToDateViaSqlDate(date));
				if (flight.getTakeOffTime().toLocalTime().isAfter(flight.getArrivalTime().toLocalTime())) {
					date = date.plusDays(1);
				}
				flightPlan.setArrivalDate(convertToDateViaSqlDate(date));
				flightPlan.setFull(false);
				flightPlan.setPackagesNumber(0);
				flightPlan.setPackagesNumberSimulated(0);
				flightPlan.setFullSimulated(false);
				flightPlan.setStatus(FlightPlanStatus.AGENDADO);
				/* Consultar si hay espacio */
				Optional<Warehouse> oWarehouse = serviceWarehouse.findByAirport_id(flight.getArrivalAirport().getId());
				if (oWarehouse.isPresent()) {
					Warehouse warehouse = oWarehouse.get();
					Date dateStorage;
					Date arrivalDate = flightPlan.getArrivalDate();
					Time arrivalTime = flightPlan.getFlight().getArrivalTime();
					dateStorage = convertDateAndTimeToDate(arrivalDate, arrivalTime);
					if (dateStorage == null) {
						System.out.println("Error en conversión");
					} else {
						/*Calendar c = Calendar.getInstance();
						c.setTime(dateStorage);
						c.add(Calendar.HOUR, -5);
						Date dateStoragePatch=c.getTime();
						System.out.println(dateStorage);*/
						List<StorageRegister> listRegisters = serviceStorage.findAllPresentOnDate(dateStorage/*Patch*/,
								warehouse.getId());
						if (listRegisters.size() < warehouse.getCapacity()) {
							/* Si hay espacio */
							Pair<Node, FlightPlan> pair = new Pair<Node, FlightPlan>(sourceNode, flightPlan);
							shortestPath.add(pair);
							evaluationNode.setShortestPath(shortestPath);
						} else {
							//System.out.println("No hay espacio en almacen");
							Incident incident = new Incident();
							incident.setAirport(warehouse.getAirport());
							incident.setRegisterDate(convertToDateViaSqlDate(convertToLocalDateViaInstant(dateStorage)));
							incident.setDescription("FULL WAREHOUSE");
							incident.setActive(true);
							incident.setSimulated(simulated);
							incident.setRegisterDate(dateStorage);
							this.daoIncident.save(incident);
						}
					}
				} else {
					System.out.println("¿No existe Warehouse?");
				}
			}
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

	public Graph calculateShortestPathFromSource(Graph graph, Node source, LocalDate date, LocalTime time, boolean simulated) {
		// Distancia del inicial igual a 0
		source.setDistance(0.0);

		Set<Node> settledNodes = new HashSet<>();
		Set<Node> unsettledNodes = new HashSet<>();

		unsettledNodes.add(source);
		while (unsettledNodes.size() != 0) {
			Node currentNode = getLowestDistanceNode(unsettledNodes);
			unsettledNodes.remove(currentNode);
			for (Entry<Node, Pair<Double, Flight>> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
				Node adjacentNode = adjacencyPair.getKey();
				Double edgeWeight = adjacencyPair.getValue().getKey();
				Flight f = adjacencyPair.getValue().getValue();
				// Comprobar consistencia de horas
				/* Ahora siempre será consistente */
				// Boolean consistent = true;
				Integer listSize = currentNode.getShortestPath().size();
				if (listSize > 0) {
					// consistent=currentNode.getShortestPath().get(listSize-1).getValue().getArrivalDateTime().isBefore(f.getTakeOffDateTime());
					// Agregar el tiempo de espera hasta el despegue
					edgeWeight += durationBetweenTime(currentNode.getShortestPath().get(listSize - 1).getValue()
							.getFlight().getArrivalTime().toLocalTime(), f.getTakeOffTime().toLocalTime());
				}
				if (!settledNodes.contains(adjacentNode) /* && consistent */) {
					CalculateMinimumDistance(adjacentNode, edgeWeight, currentNode, f, date, time, simulated);
					unsettledNodes.add(adjacentNode);
				}
			}
			settledNodes.add(currentNode);
		}
		return graph;
	}

	public Node getShortestPath(Integer start, Integer objective, LocalDate date, LocalTime time, boolean simulated) {
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
				/* Agregar espera al primer vuelo */
				if (airport.getId() == start) {
					duration+=durationBetweenTime(time, f.getTakeOffTime().toLocalTime());
				}
				nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()), duration, f);
			}
			// distancia = raiz((x2-x1)^2 + (y2-y1)^2)
			Double distance = Math.pow(Math.pow(airport.getLatitude() - objectiveAirport.getLatitude(), 2)
					+ Math.pow(airport.getLongitude() - objectiveAirport.getLongitude(), 2), 0.5);
			nodes.get(airport.getId()).setHeuristic(distance);
			graphNew.addNode(nodes.get(airport.getId()));
		}

		Graph graphResult = calculateShortestPathFromSource(graphNew, nodes.get(start), date, time, simulated);
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

	public RoutePlan determinRoute(Integer start, Integer objective, LocalDate date, LocalTime time, boolean simulated) {
		/*Verificar si almacen de origen tiene espacio*/
		Optional<Warehouse> oWarehouse = serviceWarehouse.findByAirport_id(start);
		if (oWarehouse.isPresent()) {
			Warehouse warehouse = oWarehouse.get();
			Date dateStorage;
			Date arrivalDate = convertToDateViaSqlDate(date);
			Time arrivalTime = Time.valueOf(time);
			dateStorage = convertDateAndTimeToDate(arrivalDate, arrivalTime);
			if (dateStorage == null) {
				System.out.println("Error en conversión");
			} else {
				/*Calendar c = Calendar.getInstance();
				c.setTime(dateStorage);
				c.add(Calendar.HOUR, -5);
				Date dateStoragePatch=c.getTime();
				System.out.println(dateStorage);*/
				List<StorageRegister> listRegisters = serviceStorage.findAllPresentOnDate(dateStorage/*Patch*/,warehouse.getId());
				if (listRegisters.size() >= warehouse.getCapacity()) {
					//System.out.println("No hay espacio en almacen");
					Incident incident = new Incident();
					incident.setAirport(warehouse.getAirport());
					incident.setRegisterDate(convertToDateViaSqlDate(convertToLocalDateViaInstant(dateStorage)));
					incident.setDescription("FULL WAREHOUSE");
					incident.setActive(true);
					incident.setSimulated(simulated);
					incident.setRegisterDate(dateStorage);
					this.daoIncident.save(incident);
					
					RoutePlan plan=new RoutePlan();
					plan.setStatus(RoutePlanStatus.CANCELADO);
					plan.setCurrentStage(0);
					plan.setEstimatedTime(0.0);
					plan.setFlightPlans(new ArrayList<FlightPlan>());
					return plan;
				}
			}
		}
		else {
			System.out.println("No existe el almacen?");
			
			RoutePlan plan=new RoutePlan();
			plan.setStatus(RoutePlanStatus.CANCELADO);
			plan.setCurrentStage(0);
			plan.setEstimatedTime(0.0);
			plan.setFlightPlans(new ArrayList<FlightPlan>());
			return plan;
		}
		
		Node result = getShortestPath(start, objective, date, time, simulated);
		RoutePlan plan = new RoutePlan();
		if (result.getId() == 0) {
			/* Sin resultado */
			return plan;
		}
		plan.setCurrentStage(0);
		plan.setStatus(RoutePlanStatus.EN_EJECUCION);
		List<FlightPlan> listFlightPlan = new ArrayList<>();
		List<Pair<Node, FlightPlan>> listPairs = result.getShortestPath();
		if (listPairs.size() == 0) {
			System.out.println("Sin resultado");
		}
		for (Pair<Node, FlightPlan> p : listPairs) {
			FlightPlan fp = p.getValue();
			listFlightPlan.add(fp);
		}
		plan.setFlightPlans(listFlightPlan);
		plan.setEstimatedTime(result.getDistance());
		return plan;
	}

	public Integer insertHistoricPackage(String originAirport, String destinationAirport, String dateS, String timeS) {
		/* Obtener id de aeropuertos */
		Optional<Airport> oDestination = serviceAirport.findByCode(destinationAirport);
		Optional<Airport> oOrigin = serviceAirport.findByCode(originAirport);
		if (oDestination.isPresent() && oOrigin.isPresent()) {
			Airport origin = oOrigin.get();
			Airport destination = oDestination.get();
			LocalDate date = convertStringToLocalDate(dateS);
			LocalTime time = convertStringToLocalTime(timeS);

			if (date == null || time == null) {
				System.out.println("Error en convertir fechas u horas");
				System.out.println(date);
				System.out.println(time);
				return 0;
			} else {
				
				/*Verificar si almacen de origen tiene espacio
				Optional<Warehouse> oWarehouse = serviceWarehouse.findByAirport_id(origin.getId());
				if (oWarehouse.isPresent()) {
					Warehouse warehouse = oWarehouse.get();
					Date dateStorage;
					Date arrivalDate = convertToDateViaSqlDate(date);
					Time arrivalTime = Time.valueOf(time);
					dateStorage = convertDateAndTimeToDate(arrivalDate, arrivalTime);
					if (dateStorage == null) {
						System.out.println("Error en conversión");
					} else {
						Calendar c = Calendar.getInstance();
						c.setTime(dateStorage);
						c.add(Calendar.HOUR, -5);
						Date dateStoragePatch=c.getTime();
						System.out.println(dateStorage);
						List<StorageRegister> listRegisters = serviceStorage.findAllPresentOnDate(dateStoragePatch,
								warehouse.getId());
						if (listRegisters.size() >= warehouse.getCapacity()) {
							//System.out.println("No hay espacio en almacen");
							Incident incident = new Incident();
							incident.setAirport(warehouse.getAirport());
							incident.setRegisterDate(convertToDateViaSqlDate(convertToLocalDateViaInstant(dateStorage)));
							incident.setDescription("FULL WAREHOUSE");
							incident.setActive(true);
							incident.setSimulated(true);
							incident.setRegisterDate(dateStorage);
							this.daoIncident.save(incident);
							return 0;
						}
					}
				}
				else {
					System.out.println("No existe el almacen?");
					return 0;
				}*/
				/*Calcular ruta*/
				RoutePlan plan = determinRoute(origin.getId(), destination.getId(), date, time,true);
				if (plan.getFlightPlans().size() > 0) {
					/* Se encontró resultado */

					// System.out.println("Crear cliente");

					/* Crear cliente */
					Client c = new Client();
					c.setCellphone("987654321");
					c.setDocument("1234568");
					c.setEmail("historia@gmail.com");
					// c.setId(0);
					c.setLastname("Historia");
					c.setName("Historia");
					c.setRegisterDate(LocalDateTime.now());

					// System.out.println("Crear paquete");

					/* Crear package */
					Package p = new Package();
					p.setFragile(false);
					p.setDescription("Paquete de registro histórico");
					p.setHigh(1.0);
					p.setLarge(1.0);
					p.setWidth(1.0);
					p.setWeight(1.0);
					p.setRoutePlan(plan);
					p.setStatus(PackageStatus.SIMULADO);
					p.setActive(true);

					/* Insertar resultados */

					// System.out.println("Crear dispatch");

					/* Crear dispatch */
					Dispatch d = new Dispatch();
					d.setDestinationAirport(destination);
					d.setOriginAirport(origin);
					d.setPack(p);
					d.setReceiveClientDocument("00000000");
					d.setReceiveClientLastname("History");
					d.setReceiveClientName("History");
					d.setRegisterDate(LocalDateTime.now());
					d.setStatus(DispatchStatus.SIMULADO);
					d.setSend_client(c);
					d.setActive(true);

					// System.out.println("insertar dispatch");
					d = serviceDispatch.save(d,true,convertDateAndTimeToDate(convertToDateViaSqlDate(date), Time.valueOf(time)));

					// System.out.println("obtener paquete dispatch");
					p = d.getPack();
					
					/*Ver si es que llego a tiempo*/
					Double max;
					if(origin.getCity().getCountry().getContinent().getId()==destination.getCity().getCountry().getContinent().getId()) {
						/*Mismo continente*/
						max=24*60.0;
					}
					else{
						/*Diferente continente*/
						max=48*60.0;
					}
					
					if(plan.getEstimatedTime()>max) {
						/*Late*/
						return 2;
					}
					else {
						/*Ok*/
						return 1;
					}
					
				} else {
					return 0;
				}
			}
		} else {
			System.out.println("No existe alguno de los aeropuertos");
			return 0;
		}
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
