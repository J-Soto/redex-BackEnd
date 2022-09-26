package pucp.dp1.redex.router.algorithms;

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
	private Double heuristic=0.0; 
	Map<Node, Pair<Double, Flight>> adjacentNodes = new HashMap<>();
	
	public void addDestination(Node destination, double cost, Flight flight) {
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
