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
					Double newDistance=0.0;
					Boolean isStart=false;				
					Date dia;
					dia=Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
					Double horas;
					if(f.getTakeOffAirport().getId() == start.getId()) isStart=true;
					takeOff= f.getTakeOffTime().toLocalTime();
					arrival = f.getArrivalTime().toLocalTime();
					takeOffUtc =f.getTakeOffAirport().getCity().getCountry().getUtc();
					arrivalUtc =f.getArrivalAirport().getCity().getCountry().getUtc();
					LocalDate takeOfDate = calcularTakeOfDate(isStart,date, time, takeOff, arrival, takeOffUtc, arrivalUtc);
					FlightPlan fp = buscarFP(f,takeOfDate);
					adjacentNode.setArrivalFlight(f);
					adjacentNode.setFather(currentNode);
					adjacentNode.setHeuristic(heuristic(adjacentNode.getArrivalFlight().getArrivalAirport(),currentNode.getId(), objective.getId(), time));
					newDistance=durationBetweenTime(isStart,date, time, takeOff, arrival, takeOffUtc, arrivalUtc,fp);//actualiza el fp
					packagesProcesados= hayCapacidad(f, f.getArrivalAirport().getWarehouse(), cantPackages,fp);
					fp.setPackagesNumber(packagesProcesados);
					fp.setPackagesNumberSimulated(packagesProcesados);
					adjacentNode.setFlightPlan(fp);
					if(packagesProcesados > 0){						
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
								packagesProcesadosR= hayCapacidad(adjacentNode.getArrivalFlight(), adjacentNode.getArrivalFlight().getArrivalAirport().getWarehouse(), minComunCap,fp);
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
	private LocalDate calcularTakeOfDate(Boolean isStart, LocalDate date,LocalTime time,LocalTime start, LocalTime end, Integer utcStart, Integer utcEnd) {

		double acumulator=0;
		Integer dia=0;
		LocalDate diaTakeOff=date;
		LocalDate diaIni,diaFin;
		

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
		if(time.isBefore(start));
		else 	dia++;

		diaTakeOff.plusDays(dia);	
		return diaTakeOff;
		
	}
	private FlightPlan buscarFP(Flight f, LocalDate date) {
		FlightPlan fpResult=null;
		List<FlightPlan> listFP;
		listFP = serviceFlightPlan.findAll();
		for(FlightPlan fp:listFP){
			if(fp.getFlight().getIdFlight()==f.getIdFlight() && fp.getTakeOffDate().equals(date)){
				fpResult=fp;
			}
		}
		return fpResult;
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
			FlightPlan fp=node.getFlightPlan();
			Warehouse w=f.getArrivalAirport().getWarehouse();
			//serviceFlight.updateOccupiedCapacity(f.getIdFlight(),f.getOccupiedCapacity()+minComunCapac);
			serviceFlightPlan.updateOccupiedCapacity(fp.getId(),fp.getPackagesNumberSimulated()+minComunCapac);
			serviceWarehouse.updateOccupiedCapacity(w.getId(), w.getOccupiedCapacity()+minComunCapac);
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
				
			}


			graphNew.addNode(nodes.get(airport.getId()));
		}

		
		List <Node> listResult = calculateShortestPathFromSource( nodes.get(start),nodes.get(objective), date, time, cantPackages);

		// Logica que pablo comento ya implementada
		List <RoutePlan> listplan = new ArrayList<>();
		//obtengo el shortestPath hasta este momento
		for(Node result: listResult){
			LinkedList<Pair<Node, FlightPlan>> shortestPath = new LinkedList<Pair<Node, FlightPlan>>(result.getShortestPath());
			RoutePlan rPlan = new RoutePlan();
			List<FlightPlan> listFlightPlan = new ArrayList<>();
			while(true){
				FlightPlan fp= result.getFlightPlan();
				rPlan.setCurrentStage(0);
				rPlan.setStatus(RoutePlanStatus.EN_EJECUCION);
				listFlightPlan.add(fp);
				if(fp!=null){
					Pair<Node, FlightPlan> pair = new Pair<Node, FlightPlan>(result,fp);
					//agrego el par al shortestPath
					shortestPath.add(pair);
					//obtengo el nodo padre anterior
					
				}
				result = result.getFather();
				if (result.getFather()==null) break;

				
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
	public Integer insertHistoricPackage(String originAirport, String destinationAirport, String dateS, LocalTime time, Integer cantPackages) {
		List <RoutePlan> listplan = new ArrayList<>();
		int resultado=0;
		/* Obtener id de aeropuertos */
		Optional<Airport> oDestination = serviceAirport.findByCode(destinationAirport);
		Optional<Airport> oOrigin = serviceAirport.findByCode(originAirport);
		if (oDestination.isPresent() && oOrigin.isPresent()) {
			Airport origin = oOrigin.get();
			Airport destination = oDestination.get();
			LocalDate date = convertStringToLocalDate(dateS);
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

						/* Crear package */
						Package p = new Package();
						p.setDescription("Paquete de registro histórico");
						p.setRoutePlan(plan);
						p.setStatus(PackageStatus.SIMULADO);
						p.setActive(true);

						/* Crear dispatch */
						Dispatch d = new Dispatch();
						d.setDestinationAirport(destination);
						d.setOriginAirport(origin);
						d.setPack(p);
						d.setRegisterDate(LocalDateTime.now());
						d.setStatus(DispatchStatus.SIMULADO);
						d.setActive(true);
						d = serviceDispatch.save(d,true,convertDateAndTimeToDate(convertToDateViaSqlDate(date), Time.valueOf(time)));
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
	public Integer hayCapacidad(Flight f,Warehouse w,Integer cantPackages, FlightPlan fp){
		Integer res=0;

		Integer cantOcupadaAntF=0,cantOcupadaAntA=0,cantMaxF,cantMaxA,cantDisponible,cantDisponibleA,cantDisponibleF,cantPorOcupar,packagesPorProcesar;
		cantMaxF=f.getCapacity();
		cantMaxA=w.getCapacity();
		//cantOcupadaAntF=fp.getOccupiedCapacity();
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
	public double durationBetweenTime(Boolean isStart, LocalDate date,LocalTime time,LocalTime start, LocalTime end, Integer utcStart, Integer utcEnd, FlightPlan fp)  {
		double acumulator=0;
		Integer dia=0;
		LocalDate diaFinLC=date;
		Date diaIni,diaFin;
		

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
			dia++;
			acumulator += Duration.between(time, LocalTime.parse("23:59:59")).toMinutes();
			acumulator += Duration.between( LocalTime.parse("00:00:00"),start).toMinutes();
		}
		
		//calcular tiempo desde el arrivo hasta la llegada
		if (start.isBefore(end)) {
			acumulator += Duration.between(start, end).toMinutes();
		} else {
			dia++;
			acumulator += Duration.between(start, LocalTime.parse("23:59:59")).toMinutes();
			acumulator += Duration.between(LocalTime.parse("00:00:00"), end).toMinutes();
		}

		diaIni=Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
		diaFinLC.plusDays(dia);
		diaFin=Date.from(diaFinLC.atStartOfDay(ZoneId.systemDefault()).toInstant());	
		//fp.setTakeOffDate(diaIni);
		//fp.setArrivalDate(diaFin);
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