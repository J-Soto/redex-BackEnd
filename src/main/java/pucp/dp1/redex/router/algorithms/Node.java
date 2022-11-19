package pucp.dp1.redex.router.algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.route.FlightPlan;

public class Node {
	private Integer id;
	private Double distance=Double.MAX_VALUE;
	private List<Pair<Node,FlightPlan>> shortestPath = new LinkedList<>();
	private Double heuristic=10000000.0;
	private Integer key=0; 
	private Integer packagesProcesados=0;
	private Flight arrivalFlight=null;
	private List<List<Pair<Node,FlightPlan>>> listShortestPath= new LinkedList<>();
	private FlightPlan flightPlan=null; 

	public FlightPlan getFlightPlan() {
		return flightPlan;
	}

	public void setFlightPlan(FlightPlan flightPlan) {
		this.flightPlan = flightPlan;
	}

	Map<Node, Pair<Double, Flight>> adjacentNodes = new HashMap<>();

	public void addListShortestPath( List<Pair<Node,FlightPlan>> shortestPath){
		this.listShortestPath.add(shortestPath);
	}
	
	

	public Flight getArrivalFlight() {
		return arrivalFlight;
	}

	public void setArrivalFlight(Flight arrivalFlight) {
		this.arrivalFlight = arrivalFlight;
	}

	public Integer getPackagesProcesados() {
		return packagesProcesados;
	}

	public void setPackagesProcesados(Integer packagesProcesados) {
		this.packagesProcesados = packagesProcesados;
	}

	public Integer getKey() {
		return key;
	}

	
	public Node() {
	}

	private Node father=null;
	private Flight fatherFlight;
	private Double DistancePlusHeu;

	
	public Flight getFatherFlight() {
		return fatherFlight;
	}

	public void setFatherFlight(Flight fatherFlight) {
		this.fatherFlight = fatherFlight;
	}

	
	
	public Double getDistancePlusHeu() {
		return DistancePlusHeu;
	}

	public void setDistancePlusHeu(Double distancePlusHeu) {
		DistancePlusHeu = distancePlusHeu;
	}

	public Node getFather() {
		return father;
	}

	public void setFather(Node father) {
		this.father = father;
	}

	public void addDestination(Node destination, double cost, Flight flight, Double heu) {
		//rev, malogra el return final porque busca por id
		Pair<Double, Flight> pair = new Pair<Double, Flight>(cost, flight);
		Node newNode= new Node();
		newNode.setId(destination.getId());
		newNode.setAdjacentNodes(destination.getAdjacentNodes());
		newNode.setDistancePlusHeu(destination.getDistancePlusHeu());
		newNode.setDistance(destination.getDistance());
		newNode.setFather(destination.getFather());
		newNode.setFatherFlight(destination.getFatherFlight());
		newNode.setHeuristic(heu);
		newNode.setShortestPath(destination.getShortestPath());
		key++;
		//this.adjacentNodes.put(destination, pair);	
		this.adjacentNodes.put(newNode, pair);
		
	}
	
	public void addDestination(Node destination, double cost, Flight flight) {
		//rev, malogra el return final porque busca por id
		Pair<Double, Flight> pair = new Pair<Double, Flight>(cost, flight);
		
		this.adjacentNodes.put(destination, pair);	

		
	}
	public Node(Integer id) {
		this.id=id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Pair<Node, FlightPlan>> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<Pair<Node, FlightPlan>> shortestPath) {
		this.shortestPath = shortestPath;
		key =1;
	}

	public Map<Node, Pair<Double, Flight>> getAdjacentNodes() {
		return adjacentNodes;
	}

	public void setAdjacentNodes(Map<Node, Pair<Double, Flight>> adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(Double heuristic) {
		this.heuristic = heuristic;
	}
		
}
