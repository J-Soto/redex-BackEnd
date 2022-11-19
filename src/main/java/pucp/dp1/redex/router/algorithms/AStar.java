package pucp.dp1.redex.router.algorithms;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
import java.util.TimeZone;
import java.util.Map.Entry;
import javax.print.attribute.standard.Destination;
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
import pucp.dp1.redex.model.utils.FlightElement;
import pucp.dp1.redex.router.AirportsMap;
import pucp.dp1.redex.services.impl.route.FlightPlanService;
import pucp.dp1.redex.services.impl.sales.AirportService;
import pucp.dp1.redex.services.impl.sales.DispatchService;
import pucp.dp1.redex.services.impl.storage.StorageRegisterService;
import pucp.dp1.redex.services.impl.storage.WarehouseService;
import pucp.dp1.redex.services.impl.PACK.FlightService;
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
	@Autowired
	private FlightService serviceFlight;
	private Map<Airport, List<Flight>> map;
	public Map<Airport, List<Flight>> getMap() {
		map = airportMap.getGraph();
		return map;
	}
	public Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		Node lowestDistanceNode = null;
		double lowestDistance = Double.MAX_VALUE;
		for (Node node : unsettledNodes) {
			double nodeDistance = node.getDistance() + node.getHeuristic();
			if (nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestDistanceNode = node;
			}
		}
		return lowestDistanceNode;
	}
	public List<Node> calculateShortestPathFromSource( Node start,Node objective, LocalDate date, LocalTime time, Integer cantPackages) {
		Integer minComunCap=0;
		Node currentNode=null;
		List<Node>  bestWays =new LinkedList<>();
		Integer packagesProcesados,packagesProcesadosR;	

		while(true){
			if(cantPackages <= 0) break;
			minComunCap=cantPackages;				
			Set<Node> settledNodes = new HashSet<>();
			Set<Node> unsettledNodes = new HashSet<>();
			currentNode=null;
			start.setDistance(0.0);
			unsettledNodes.add(start);
			
			while (unsettledNodes.size() != 0) {
				currentNode = getLowestDistanceNode(unsettledNodes);
				if(minComunCap > currentNode.getPackagesProcesados() && currentNode.getId()!=start.getId()) minComunCap = currentNode.getPackagesProcesados();
				unsettledNodes.remove(currentNode);
				settledNodes.add(currentNode);

				if(currentNode.getId() == objective.getId() ){
					//deberá retornar el current node
					break;
				}
	
				for (Entry<Node, Pair<Double, Flight>> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {//aqui se generan suscesores de node_n
					
					Node adjacentNode = adjacencyPair.getKey();
					Flight f = adjacencyPair.getValue().getValue();
					LocalTime takeOff, arrival;
					Integer takeOffUtc, arrivalUtc;
					Double newDistance;
					Boolean isStart=false;
					Date dia;
					dia=Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
					Double horas;
					// horas=durationBetweenTime(takeOff, arrival, takeOffUtc, arrivalUtc);
					// time.plusMinutes(horas.longValue());
					// if(time.isBefore(LocalTime.parse("23:59:59")) ){
					// }

					FlightPlan fp = new FlightPlan(dia, dia, f);
					//fp = actualizarDiasFP(fp);
					if(f.getTakeOffAirport().getId() == start.getId()) isStart=true;
					takeOff= f.getTakeOffTime().toLocalTime();
					arrival = f.getArrivalTime().toLocalTime();
					takeOffUtc =f.getTakeOffAirport().getCity().getCountry().getUtc();
					arrivalUtc =f.getArrivalAirport().getCity().getCountry().getUtc();
					adjacentNode.setArrivalFlight(f);
					adjacentNode.setFlightPlan(fp);
					adjacentNode.setFather(currentNode);
					adjacentNode.setHeuristic(heuristic(adjacentNode.getArrivalFlight().getArrivalAirport(),currentNode.getId(), objective.getId(), time));
					packagesProcesados= hayCapacidad(f, f.getArrivalAirport().getWarehouse(), cantPackages);
					if(packagesProcesados > 0){
						//if(minComunCap > packagesProcesados) minComunCap = packagesProcesados;
						newDistance=durationBetweenTime(isStart,date, time, takeOff, arrival, takeOffUtc, arrivalUtc);
						adjacentNode.setDistance(currentNode.getDistance() + newDistance);
						adjacentNode.setPackagesProcesados(packagesProcesados);
						adjacentNode.setArrivalFlight(f);
					}
					else{
						Double n=Double.MAX_VALUE;
						adjacentNode.setDistance(n);
					}
	
					if (!settledNodes.contains(adjacentNode) && !unsettledNodes.contains(adjacentNode)) {
						unsettledNodes.add(adjacentNode);
					}
					else{
						//if unsettlet tiene un nodo que es el mismo pais
						Node oldCurrentNode;
						oldCurrentNode = contiene(unsettledNodes,adjacentNode.getId());
						if(oldCurrentNode!=null){						
							if(adjacentNode.getDistance() < oldCurrentNode.getDistance()){
								//actualizar el nodo anterior con un nuevo papá, nueva distancia y demás
								//debe actualizarse a en el espacio de memoria, para que se actualice en la cola
								
								//ver disponibilidad
								packagesProcesadosR= hayCapacidad(adjacentNode.getArrivalFlight(), adjacentNode.getArrivalFlight().getArrivalAirport().getWarehouse(), minComunCap);
								if(packagesProcesadosR>=minComunCap){
									//solo se actualiza si tenía mayor o igual capacidad que la otra opcion
									oldCurrentNode.setPackagesProcesados(adjacentNode.getPackagesProcesados());
									oldCurrentNode.setDistance(adjacentNode.getDistance());
									oldCurrentNode.setArrivalFlight(adjacentNode.getArrivalFlight());
									oldCurrentNode.setFather(adjacentNode.getFather());
								}	
							}
						}
					}
	
				}
				
			}
			actualizarCapacidad(start,currentNode,minComunCap);
			cantPackages-=minComunCap;
			bestWays.add(currentNode);
			actualizarStart(start, objective.getId());
		}
		
		return bestWays;
	}
	Node contiene(Set<Node> unsettledNodes, Integer id){
		Node result=null;
		for (Node node :  unsettledNodes){
			if (node.getId()==id){
				return node;
			}
		}
		return result;
	}
	private void actualizarCapacidad(Node start, Node node, Integer minComunCapac) {
		
		while(true){
			if(node.getId()==start.getId()) break;
			Flight f=node.getArrivalFlight();
			Warehouse w=f.getArrivalAirport().getWarehouse();			
			//f.setOccupiedCapacity(f.getOccupiedCapacity()+minComunCapac);
			//w.setOccupiedCapacity(w.getOccupiedCapacity()+minComunCapac);
			
			
			serviceFlight.updateOccupiedCapacity(f.getIdFlight(),f.getOccupiedCapacity()+minComunCapac);		
			serviceWarehouse.updateOccupiedCapacity(w.getId(), w.getOccupiedCapacity()+minComunCapac);

			//serviceFlight.updateCapacity(f.getIdFlight(),f.getOccupiedCapacity()+minComunCapac);		
			//serviceWarehouse.updateCapacity(w.getId(), w.getOccupiedCapacity()+minComunCapac);

			node=node.getFather();

		}	
		
	}
	public double heuristic(Airport arrivalAirport, Integer takeOffNode, Integer objective, LocalTime time){
		double  timeHeu= 10000000.0;
		Double tEspera=0.0;
		Integer idFlight;
		List<FlightElement> listBestFlights = serviceFlight.findBestFlight(takeOffNode, objective);

		if(arrivalAirport.getId()==objective){
			
			if(listBestFlights.size()>0 ) {
				//tomar el menor tiempo de los vuelos directos que existan
				for (FlightElement f : listBestFlights) {
					f.setArrivalTime(serviceFlight.findBestFlightArrivalTime(f.getIdFlight()).toLocalTime());
					f.setTakeOffTime(serviceFlight.findBestFlightTakeOffTime(f.getIdFlight()).toLocalTime());
					tEspera = durationBetweenTime(time, f.getTakeOffTime());
					Double newTimeHeu= tEspera + durationBetweenTime(f.getTakeOffTime(),f.getArrivalTime(),
					  f.getTakeOffAirport().getCity().getCountry().getUtc(),   f.getArrivalAirport().getCity().getCountry().getUtc());
						idFlight= f.getIdFlight();
						if(timeHeu>newTimeHeu) timeHeu=newTimeHeu;
				}
			}
	
		}

		
		return timeHeu;
	}
	public void  actualizarStart(Node start,Integer objective){
		Map<Airport, List<Flight>> graphOld = this.getMap();
		Map<Integer, Node> nodes = new HashMap<>();
		Airport objectiveAirport = null;
		Graph graphNew = new Graph();

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
				nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()), 0, f,10000000.0);		
			}
			graphNew.addNode(nodes.get(airport.getId()));
		}
		start =nodes.get(start.getId());

	}
	public List <RoutePlan> getShortestPath(Integer start, Integer objective, LocalDate date, LocalTime time, boolean simulated, Integer cantPackages) {
		// List<Flight> result=new ArrayList<>();
		Double tEspera=0.0;
		Double timeHeu= 10000000.0;
		List<Double> bestHeuristics=null;
		Map<Airport, List<Flight>> graphOld = this.getMap();
		Map<Integer, Node> nodes = new HashMap<>();
		Graph graphNew = new Graph();
		Airport objectiveAirport = null;
		Integer idFlight;
		List<Integer> bestIdFlight=null;
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
				nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()), 0, f,timeHeu);
/*
				double duration = durationBetweenTime(f.getTakeOffTime().toLocalTime(),f.getArrivalTime().toLocalTime(),
				f.getArrivalAirport().getCity().getCountry().getUtc(),f.getTakeOffAirport().getCity().getCountry().getUtc());
				
				if (airport.getId() == start) {
					//Agregar espera al primer vuelo, no usa utc
					duration+=durationBetweenTime(time, f.getTakeOffTime().toLocalTime());
				}

				if(f.getIdFlight()==4755 ||f.getIdFlight()==4757 ||f.getIdFlight()==4759 ){
					Integer i;
					i=f.getIdFlight();
				}

 */			//nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()), duration, f,timeHeu);
				
			}
			// distancia = raiz((x2-x1)^2 + (y2-y1)^2), ya no es asi
		
			

			graphNew.addNode(nodes.get(airport.getId()));
		}

		//Node result = calculateShortestPathFromSource( nodes.get(start),nodes.get(objective), date, time, cantPackages);
		List <Node> listResult = calculateShortestPathFromSource( nodes.get(start),nodes.get(objective), date, time, cantPackages);

		// Logica que pablo comento ya implementada
		List <RoutePlan> listplan = new ArrayList<>();
		//obtengo el shortestPath hasta este momento
		for(Node result: listResult){
			LinkedList<Pair<Node, FlightPlan>> shortestPath = new LinkedList<Pair<Node, FlightPlan>>(result.getShortestPath());
			RoutePlan rPlan = new RoutePlan();
			List<FlightPlan> listFlightPlan = new ArrayList<>();
			while(true){
				//if (result.equals(null))break;
				
				//dentro del result habia un flight y lo asigne
				//Flight f= result.getArrivalFlight();
				FlightPlan fp= result.getFlightPlan();
				rPlan.setCurrentStage(0);
				rPlan.setStatus(RoutePlanStatus.EN_EJECUCION);
				listFlightPlan.add(fp);
				//creo un optional con el flight y el date que se envia como parametro
				//Optional<FlightPlan> existent = serviceFlightPlan.findByFlight_IdFlightAndTakeOffDate(f.getIdFlight(),convertToDateViaSqlDate(date));
				//creo un flightPlan
				//FlightPlan flightPlan;
				if(fp!=null){
					Pair<Node, FlightPlan> pair = new Pair<Node, FlightPlan>(result,fp);
					//agrego el par al shortestPath
					shortestPath.add(pair);
					//obtengo el nodo padre anterior
					
				}
				result = result.getFather();
				if (result.getFather()==null) break;
				//si el optional no esta vacio
				// if (existent.isPresent()) {
				// 	//asigno el optional a el flightPlan
				// 	flightPlan = existent.get();
				// 	//creo el par necesario para el shortestPath, con el nodo result y el flightPlan
				// 	Pair<Node, FlightPlan> pair = new Pair<Node, FlightPlan>(result,flightPlan);
				// 	//agrego el par al shortestPath
				// 	shortestPath.add(pair);
				// 	//obtengo el nodo padre anterior
				// 	result = result.getFather();
				// }
				//si el optional esta vacio
				//else System.out.println("No se encontro el vuelo");
				
			}
			//result.setShortestPath(shortestPath);
			rPlan.setFlightPlans(listFlightPlan);
			listplan.add(rPlan);
			//result.addListShortestPath(shortestPath);
		}
		return listplan;
	}
	private Double getBestTime(Integer start, Integer objective) {
		return null;
	}
	// public List <RoutePlan> determinRoute(Integer start, Integer objective, LocalDate date, LocalTime time, boolean simulated, Integer cantPackages) {
	// 	List <RoutePlan> listplan = new ArrayList<>();
	// 	/*Verificar si almacen de origen tiene espacio*/
	// 	Optional<Warehouse> oWarehouse = serviceWarehouse.findByAirport_id(start);
	// 	if (oWarehouse.isPresent()) {
	// 		Warehouse warehouse = oWarehouse.get();
	// 		Date dateStorage;
	// 		Date arrivalDate = convertToDateViaSqlDate(date);
	// 		Time arrivalTime = Time.valueOf(time);
	// 		dateStorage = convertDateAndTimeToDate(arrivalDate, arrivalTime);
	// 		if (dateStorage == null) {
	// 			System.out.println("Error en conversión");
	// 		} else {
	// 			/*Calendar c = Calendar.getInstance();
	// 			c.setTime(dateStorage);
	// 			c.add(Calendar.HOUR, -5);
	// 			Date dateStoragePatch=c.getTime();
	// 			System.out.println(dateStorage);*/
	// 			List<StorageRegister> listRegisters = serviceStorage.findAllPresentOnDate(dateStorage/*Patch*/,warehouse.getId());
	// 			if (listRegisters.size() >= warehouse.getCapacity()) {
	// 				//System.out.println("No hay espacio en almacen");
	// 				Incident incident = new Incident();
	// 				incident.setAirport(warehouse.getAirport());
	// 				incident.setRegisterDate(convertToDateViaSqlDate(convertToLocalDateViaInstant(dateStorage)));
	// 				incident.setDescription("FULL WAREHOUSE");
	// 				incident.setActive(true);
	// 				incident.setSimulated(simulated);
	// 				incident.setRegisterDate(dateStorage);
	// 				this.daoIncident.save(incident);
					
	// 				RoutePlan plan=new RoutePlan();
	// 				plan.setStatus(RoutePlanStatus.CANCELADO);
	// 				plan.setCurrentStage(0);
	// 				plan.setEstimatedTime(0.0);
	// 				plan.setFlightPlans(new ArrayList<FlightPlan>());
	// 				listplan.add(plan);
	// 				return listplan;
	// 			}
	// 		}
	// 	}
	// 	else {
	// 		System.out.println("No existe el almacen?");
			
	// 		RoutePlan plan=new RoutePlan();
	// 		plan.setStatus(RoutePlanStatus.CANCELADO);
	// 		plan.setCurrentStage(0);
	// 		plan.setEstimatedTime(0.0);
	// 		plan.setFlightPlans(new ArrayList<FlightPlan>());
	// 		listplan.add(plan);
	// 		return listplan;
	// 	}

	// 	List <Node> listResult = getShortestPath(start, objective, date, time, simulated,cantPackages);
	// 	//obtengo el shortestPath hasta este momento
	// 	for(Node result: listResult){
	// 			RoutePlan plan = new RoutePlan();
	// 			if (result.getId() == 0) {
	// 				/* Sin resultado */
	// 				return listplan;
	// 			}
	// 			plan.setCurrentStage(0);
	// 			plan.setStatus(RoutePlanStatus.EN_EJECUCION);
	// 			List<FlightPlan> listFlightPlan = new ArrayList<>();
	// 			List<Pair<Node, FlightPlan>> listPairs = result.getShortestPath();
	// 			if (listPairs.size() == 0) {
	// 				System.out.println("Sin resultado");
	// 			}
	// 			for (Pair<Node, FlightPlan> p : listPairs) {
	// 				FlightPlan fp = p.getValue();
	// 				listFlightPlan.add(fp);
	// 			}
	// 			plan.setFlightPlans(listFlightPlan);
	// 			plan.setEstimatedTime(result.getDistance());
	// 			listplan.add(plan);
	// 	}
	// 	return listplan;
	// }
	public Integer insertHistoricPackage(String originAirport, String destinationAirport, String dateS, String timeS, Integer cantPackages) {
		List <RoutePlan> listplan = new ArrayList<>();
		int resultado=0;
		/* Obtener id de aeropuertos */
		Optional<Airport> oDestination = serviceAirport.findByCode(destinationAirport);
		Optional<Airport> oOrigin = serviceAirport.findByCode(originAirport);
		if (oDestination.isPresent() && oOrigin.isPresent()) {
			Airport origin = oOrigin.get();
			Airport destination = oDestination.get();
			LocalDate date = convertStringToLocalDate(dateS);
			LocalTime time = convertStringToLocalTime(timeS);
			time.plusHours(5);
			if (date == null || time == null) {
				System.out.println("Error en convertir fechas u horas");
				System.out.println(date);
				System.out.println(time);
				return 0;
			} else {

				listplan = getShortestPath(origin.getId(), destination.getId(), date, time,true,cantPackages);
				//RoutePlan plan = determinRoute(origin.getId(), destination.getId(), date, time,true,cantPackages);

				for(RoutePlan plan: listplan){
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
						resultado= 1;
					}
				}
			}
		} else {
			System.out.println("No existe alguno de los aeropuertos");
			resultado=0;}
		return resultado;
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
	public double durationBetweenTime(LocalTime start, LocalTime end, Integer utcStart, Integer utcEnd) {
		double acumulator;
		if(utcStart>0){
			start.minusHours(utcStart);
		}
		else{
			utcStart*=-1;
			start.plusHours(utcStart);
		}

		if(utcEnd>0){
			end.minusHours(utcEnd);
		}
		else{
			utcEnd*=-1;
			end.plusHours(utcEnd);
		}
	
		acumulator  =durationBetweenTime( start,  end);
		return acumulator;
	}
	public Integer hayCapacidad(Flight f,Warehouse w,Integer cantPackages){
		Integer res=0;

		Integer cantOcupadaAntF=0,cantOcupadaAntA=0,cantMaxF,cantMaxA,cantDisponible,cantDisponibleA,cantDisponibleF,cantPorOcupar,packagesPorProcesar;
		cantMaxF=f.getCapacity();
		cantMaxA=w.getCapacity();
		cantOcupadaAntF=f.getOccupiedCapacity();
		cantOcupadaAntA=w.getOccupiedCapacity();
		cantDisponibleA=cantMaxA-cantOcupadaAntA;
		cantDisponibleF=cantMaxF-cantOcupadaAntF;
		cantDisponible = Math.min(cantDisponibleF,cantDisponibleA);
		if(cantDisponible<=0){
			//no hay capacidad
			res=0;
		}
		else{
			cantPorOcupar=cantDisponible-cantPackages;
			if(cantPorOcupar>0){
				//entra todo
				res=cantPackages;
			}else{
				//entra parcial
				res=cantDisponible;

			}
		}		
		return res;
	}
	public double durationBetweenTime(Boolean isStart, LocalDate date,LocalTime time,LocalTime start, LocalTime end, Integer utcStart, Integer utcEnd)  {
		double acumulator=0;

		if(!isStart) acumulator+=60; //agregar una hora si es escala

		if(utcStart>0) start.minusHours(utcStart);		
		else {
			utcStart*=-1;
			start.plusHours(utcStart);
		}

		if(utcEnd>0) end.minusHours(utcEnd);		
		else {
			utcEnd*=-1;
			end.plusHours(utcEnd);
		}
		
		//calcular tiempo hasta el vuelo
		if(time.isBefore(start)){
			acumulator += Duration.between(time, start).toMinutes();
		}
		else {
			acumulator += Duration.between(time, LocalTime.parse("23:59:59")).toMinutes();
			acumulator += Duration.between( LocalTime.parse("00:00:00"),start).toMinutes();
		}
		
		//calcular tiempo desde el arrivo hasta la llegada
		if (start.isBefore(end)) {
			acumulator += Duration.between(start, end).toMinutes();
		} else {
			acumulator += Duration.between(start, LocalTime.parse("23:59:59")).toMinutes();
			acumulator += Duration.between(LocalTime.parse("00:00:00"), end).toMinutes();
		}
		return acumulator;
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