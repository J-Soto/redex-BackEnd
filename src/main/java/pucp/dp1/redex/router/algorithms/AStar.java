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
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.print.attribute.standard.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javafx.util.Pair;
import pucp.dp1.redex.dao.sales.IIncident;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.location.Country;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.route.FlightPlanStatus;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.model.route.RoutePlanStatus;
import pucp.dp1.redex.model.sales.*;
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
import pucp.dp1.redex.services.impl.location.ContinentService;
import pucp.dp1.redex.services.impl.location.CountryService;

@Service
public class AStar {
	//region DAOs y Services
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
	private CountryService daoCountry;
	@Autowired
	private FlightService serviceFlight;
	//endregion

	//region Maps y Lists
	private Map<Airport, List<Flight>> map;
	private List<Country>countrys=null;
	private List<Airport> airports = null;
	private List<Warehouse> warehouses = null;
	private Map <Airport, List<Flight>> listaVuelosPorAeropuerto= null;
	private Map<Pair<Integer,Date>, FlightPlan> fpMap = null;
	//endregion

	//region Algoritmo
	public List<Node> calculateShortestPathFromSource( Node start,Node objective, LocalDate date, LocalTime time, Integer cantPackages) {
		Integer minComunCap=0;
		Node currentNode=null;
		List<Node>  bestWays =new LinkedList<>();
		Integer packagesProcesados,packagesProcesadosR;
		Double timeAc=0.0,tMax;
		tMax=maxTiempo(start,objective);
		fpMap=serviceFlightPlan.findAll().stream().collect(Collectors.toMap(fp->new Pair<Integer,Date>(fp.getFlight().getIdFlight(),fp.getTakeOffDate()), fp->fp));
		Pair<Integer,Date> llaves = null;
		while(true){
			if(cantPackages <= 0) break;
			minComunCap=cantPackages;
			Set<Node> settledNodes = new HashSet<>();
			PriorityQueue<Node> unsettledNodes = new PriorityQueue<>(new Comparator<Node>() {
				@Override
				public int compare(Node a, Node b) {
					if ( a.getDistance() + a.getHeuristic() > b.getDistance() + b.getHeuristic() ) return 1;
					else if (a.getDistance() + a.getHeuristic() < b.getDistance() + b.getHeuristic()) return -1;
					return 0;
				}
				/*
				@Override
				public int compare(Node a, Node b) {
					if ( a.getDistance() * a.getHeuristic() > b.getDistance() * b.getHeuristic() ) return 1;
					else if (a.getDistance() * a.getHeuristic() < b.getDistance() * b.getHeuristic()) return -1;
					return 0;
				}
				*/
			});

			//Set<Node> unsettledNodes = new HashSet<>();
			currentNode=null;
			start.setDistance(0.0);
			unsettledNodes.add(start);
			while (unsettledNodes.size() != 0) {
				//currentNode = getLowestDistanceNode(unsettledNodes);
				currentNode = unsettledNodes.poll();
				if(minComunCap > currentNode.getPackagesProcesados() && currentNode.getId()!=start.getId()) minComunCap = currentNode.getPackagesProcesados();
				unsettledNodes.remove(currentNode);
				settledNodes.add(currentNode);
				timeAc+=currentNode.getDistance();
				if(timeAc>tMax){
					System.out.println("Colapso");
					Node nodoColapso=new Node();
					nodoColapso.setColapso(true);
					List<Node> listaColapso =new LinkedList<>();
					listaColapso.add(nodoColapso);
					return listaColapso;
				}
				if(currentNode.getId() == objective.getId() ){
					//deberá retornar el current node
					break;
				}
				for (Entry<Node, Pair<Double, Flight>> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {//aqui se generan suscesores de node_n
					Node adjacentNode = adjacencyPair.getKey();
					Flight f = adjacencyPair.getValue().getValue();
					LocalTime takeOff, arrival;
					Double newDistance=0.0;
					Boolean isStart=false, flagFP=false;
					Integer takeOffUtc, arrivalUtc;
					if(f.getTakeOffAirport().getId() == start.getId()) isStart=true;
					takeOff= f.getTakeOffTime().toLocalTime();
					arrival = f.getArrivalTime().toLocalTime();
					takeOffUtc =f.getTakeOffAirport().getCity().getCountry().getUtc();
					arrivalUtc =f.getArrivalAirport().getCity().getCountry().getUtc();
					LocalDate takeOfDate = calcularTakeOfDate(isStart,date, time, takeOff, arrival);
					LocalDate arrivalDate = calcularArrivalDate(isStart,date, time, takeOff, arrival);
					packagesProcesados= hayCapacidad(f, f.getArrivalAirport().getWarehouse(), cantPackages);
					if(packagesProcesados == 0){
						//adjacentNode.setDistance(Double.MAX_VALUE);
						continue;
					}
					else{
						adjacentNode.setDistance(currentNode.getDistance() + newDistance);
						adjacentNode.setPackagesProcesados(packagesProcesados);
						adjacentNode.setArrivalFlight(f);
					}
					llaves = new Pair<Integer,Date>(f.getIdFlight(),Date.from(takeOfDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
					FlightPlan fp = fpMap.get(llaves);
					if(fp==null){
						flagFP = true;
						fp= new FlightPlan(f);
						Date takeOfDate2=Date.from(takeOfDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
						Date arrivalDate2=Date.from(arrivalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
						fp.setTakeOffDate(takeOfDate2);
						fp.setArrivalDate(arrivalDate2);
						setearArrivalTakeOffUTC(fp);
					}
					adjacentNode.setArrivalFlight(f);
					adjacentNode.setFather(currentNode);
					newDistance=durationBetweenTime(isStart,date, time, takeOff, arrival, takeOffUtc, arrivalUtc,fp);
					//fp.setPackagesNumber(packagesProcesados);
					adjacentNode.setHeuristic(heuristic(adjacentNode.getArrivalFlight().getArrivalAirport(),currentNode.getId(), objective.getId(), time,newDistance));
					//fp.setPackagesNumberSimulated(packagesProcesados);
					if(flagFP) fp.setOccupiedCapacity(packagesProcesados);
					adjacentNode.setFlightPlan(fp);
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

			//
			actualizarCapacidad(start,currentNode,minComunCap);
			cantPackages-=minComunCap;
			bestWays.add(currentNode);
			//actualizarStart(start, objective.getId());
		}
		return bestWays;
	}
	public double heuristic(Airport arrivalAirport, Integer takeOffNode, Integer objective, LocalTime time, double newDistance){return (arrivalAirport.getId()==objective)?newDistance:10000000.0;}
	public List <RoutePlan> getShortestPath(Integer start, Integer objective, LocalDate date, LocalTime time, boolean simulated, Integer cantPackages) {
		warehouses = serviceWarehouse.findAll();
		airports = serviceAirport.findAll();
		listaVuelosPorAeropuerto.forEach((airport, flights) -> {
			airport.setWarehouse(warehouses.get(airport.getId()));
			flights = flights;
		});
		Map<Airport, List<Flight>> graphOld = listaVuelosPorAeropuerto;
		Map<Integer, Node> nodes = new HashMap<>();
		Graph graphNew = new Graph();
		Integer idFlight;
		List<Integer> bestIdFlight=null;

		/*
		List<Flight> flights2 = null;
		List<Airport> auxAirports = null;
		auxAirports= new ArrayList<>(graphOld.keySet());
		nodes = auxAirports.stream().collect(Collectors.toMap(airport -> airport.getId(), airport -> new Node(airport.getId())));
		Map<Integer, Node> finalNodes = nodes;
		graphOld.values().stream().flatMap(Collection::stream).forEach(flight -> {
			finalNodes.get(flight.getTakeOffAirport().getId()).addDestination(finalNodes.get(flight.getArrivalAirport().getId()),0, flight,10000000.0);
			graphNew.addNode(finalNodes.get(flight.getTakeOffAirport().getId()));});
		*/

		// Create nodes
		for (Airport airport : graphOld.keySet()) {
			Node n = new Node(airport.getId());
			nodes.put(airport.getId(), n);
		}
		for (Airport airport : graphOld.keySet()) {
			List<Flight> flights = graphOld.get(airport);
			for (Flight f : flights) nodes.get(airport.getId()).addDestination(nodes.get(f.getArrivalAirport().getId()), 0, f,10000000.0);
			graphNew.addNode(nodes.get(airport.getId()));
		}
		List <Node> listResult = calculateShortestPathFromSource(nodes.get(start),nodes.get(objective), date, time, cantPackages);
		// Logica que pablo comento ya implementada
		List <RoutePlan> listplan = new ArrayList<>();
		//obtengo el shortestPath hasta este momento
		for(Node result: listResult){
			if(result.getColapso()==true){
				List <RoutePlan> rpColapso = null;
				return rpColapso;
			}
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
	public Integer insertHistoricPackage(List<Country>countrys,Map <Airport, List<Flight>> vuelos ,String originAirport, String destinationAirport, LocalDate dateS, LocalTime time, Integer cantPackages) {
		this.countrys=countrys;
		listaVuelosPorAeropuerto=vuelos;
		List <RoutePlan> listplan = new ArrayList<>();
		Map<String,Optional<Airport>> airports = serviceAirport.findAll().stream().collect(Collectors.toMap(Airport::getCode, (Airport a) -> Optional.of(a)));
		int resultado=0;
		/* Obtener id de aeropuertos */
		Optional<Airport> oDestination = airports.get(destinationAirport);
		Optional<Airport> oOrigin = airports.get(originAirport);
		if (oDestination.isPresent() && oOrigin.isPresent()) {
			Airport origin = oOrigin.get();
			Airport destination = oDestination.get();
			LocalDate date = dateS;
			//time.plusHours(5);
			if (date == null || time == null) {
				System.out.println("Error en convertir fechas u horas");
				System.out.println(date);
				System.out.println(time);
				return 0;
			} else {

				listplan = getShortestPath(origin.getId(), destination.getId(), date, time,true,cantPackages);
				//RoutePlan plan = determinRoute(origin.getId(), destination.getId(), date, time,true,cantPackages);
				if(listplan==null) return 0;

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
						d.setRegisterDate(LocalDateTime.of(date, time));
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

	//endregion

	//region Utils
	private void actualizarCapacidad(Node start, Node node, Integer minComunCapac) {
		while(true){
			if(node.getId()==start.getId()) break;
			Flight f=node.getArrivalFlight();
			FlightPlan fp=node.getFlightPlan();
			Warehouse w=f.getArrivalAirport().getWarehouse();
			//serviceFlight.updateOccupiedCapacity(f.getIdFlight(),f.getOccupiedCapacity()+minComunCapac);
			serviceFlightPlan.updateOccupiedCapacity(fp.getId(),fp.getOccupiedCapacity()+minComunCapac);
			serviceWarehouse.updateOccupiedCapacity(w.getId(), w.getOccupiedCapacity()+minComunCapac);
			node=node.getFather();
		}
	}
	private LocalDate calcularArrivalDate(Boolean isStart, LocalDate date,LocalTime time,LocalTime start, LocalTime end) {

		//Integer dia=0;
		LocalDate arrivalDate=date;
		//calcular tiempo hasta el vuelo
		if(time.isAfter(start))	arrivalDate.plusDays(1);

		//calcular tiempo desde el arrivo hasta la llegada
		if (start.isAfter(end)) arrivalDate.plusDays(1);

		return arrivalDate;
	}
	private Double maxTiempo(Node start, Node objective) {
		String continentSt,continentObj;
		Integer utcSt,utcObj,izqDer=-1;//izq=-1,der=1
		Country c;
		Double maxTime=0.0;
		c = countrys.stream().filter(x -> x.getId() == start.getId()).findFirst().get();
		//c=(daoCountry.findById(start.getId())).get();
		continentSt=c.getContinent().getName();
		utcSt=c.getUtc();
		c = countrys.stream().filter(x -> x.getId() == objective.getId()).findFirst().get();
		//c=(daoCountry.findById(objective.getId())).get();
		continentObj=c.getContinent().getName();
		utcObj=c.getUtc();
		if(utcSt<utcObj) izqDer=1;

		if(continentSt==continentObj) maxTime=24.0*60;
		else maxTime=48.0*60;

		if(izqDer>0) maxTime-=utcObj;
		else maxTime+=utcObj;

		return maxTime;

	}
	private LocalDate calcularTakeOfDate(Boolean isStart, LocalDate date,LocalTime time,LocalTime start, LocalTime end) {

		//Integer dia=0;
		LocalDate diaTakeOff=date;

		//calcular tiempo hasta el vuelo
		if(time.isAfter(start))	diaTakeOff.plusDays(1);

		return diaTakeOff;

	}
	private Node contiene(PriorityQueue<Node> unsettledNodes, Integer id){
		Node result=null;
		for (Node node :  unsettledNodes){
			if (node.getId()==id){
				return node;
			}
		}
		return result;
	}
	public void setearArrivalTakeOffUTC(FlightPlan fp){
		int takeOffUtc =fp.getFlight().getTakeOffAirport().getCity().getCountry().getUtc();
		int arrivalUtc =fp.getFlight().getArrivalAirport().getCity().getCountry().getUtc();

		LocalTime takeOff= fp.getFlight().getTakeOffTime().toLocalTime();
		LocalTime arrival= fp.getFlight().getArrivalTime().toLocalTime();

		if(takeOffUtc>0) takeOff=takeOff.minusHours(takeOffUtc);
		else {
			takeOffUtc*=-1;
			takeOff=takeOff.plusHours(takeOffUtc);
		}

		if(arrivalUtc>0) arrival=arrival.minusHours(arrivalUtc);
		else {
			arrivalUtc*=-1;
			arrival=arrival.plusHours(arrivalUtc);
		}

		fp.setArrivalTimeUtc(Time.valueOf(arrival));
		fp.setTakeOffTimeUtc(Time.valueOf(takeOff));


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
	public Integer hayCapacidad(Flight f,Warehouse w,Integer cantPackages){
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

		if(!isStart) acumulator+=60; //agregar una hora si es escala

		if(utcStart>0) start=start.minusHours(utcStart);
		else {
			utcStart*=-1;
			start=start.plusHours(utcStart);
		}

		if(utcEnd>0) end=end.minusHours(utcEnd);
		else {
			utcEnd*=-1;
			end=end.plusHours(utcEnd);
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
		return acumulator;
	}
	public double durationBetweenTime(LocalTime start, LocalTime end, Integer utcStart, Integer utcEnd) {
		double acumulator;
		if(utcStart>0){
			start=start.minusHours(utcStart);
		}
		else{
			utcStart*=-1;
			start=start.plusHours(utcStart);
		}

		if(utcEnd>0){
			end=end.minusHours(utcEnd);
		}
		else{
			utcEnd*=-1;
			end=end.plusHours(utcEnd);
		}

		acumulator  =durationBetweenTime( start,  end);
		return acumulator;
	}
	//endregion

	//region Converts
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
	//endregion

	//region No se usan
	public double durationBetweenTime(Boolean isStart, LocalDate date,LocalTime time,LocalTime start, LocalTime end,  FlightPlan fp)  {
		double acumulator=0;
		Integer dia=0;
		LocalDate diaFinLC=date;
		Date diaIni,diaFin;


		if(!isStart) acumulator+=60; //agregar una hora si es escala



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
	private Double getBestTime(Integer start, Integer objective) {
		return null;
	}
	private Node contiene(Set<Node> unsettledNodes, Integer id){
		Node result=null;
		for (Node node :  unsettledNodes){
			if (node.getId()==id){
				return node;
			}
		}
		return result;
	}
	public Map<Airport, List<Flight>> getMap(Map <Airport, List<Flight>> listaVuelosPorAeropuerto) {
		map = airportMap.getGraph(listaVuelosPorAeropuerto);
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
				// if(lowestDistanceNode.getId()==40){
				// 	System.out.println("holaa");
				// }
				//System.out.println(lowestDistanceNode.getFlightPlan().getArrivalTimeUtc());
			}
		}
		return lowestDistanceNode;
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
					int takeOffUtc = f.getTakeOffAirport().getCity().getCountry().getUtc();
					int arrivalUtc = f.getArrivalAirport().getCity().getCountry().getUtc();
					f.setArrivalTime(serviceFlight.findBestFlightArrivalTime(f.getIdFlight()).toLocalTime());
					f.setTakeOffTime(serviceFlight.findBestFlightTakeOffTime(f.getIdFlight()).toLocalTime());
					tEspera = durationBetweenTime(time, f.getTakeOffTime(),takeOffUtc,arrivalUtc);
					Double newTimeHeu= tEspera + durationBetweenTime(f.getTakeOffTime(),f.getArrivalTime(),takeOffUtc,arrivalUtc);
					idFlight= f.getIdFlight();
					if(timeHeu>newTimeHeu) timeHeu=newTimeHeu;
				}
			}
		}
		return timeHeu;
	}
	//endregion

}