package pucp.dp1.redex.router.algorithms;

import org.springframework.stereotype.Service;

@Service
public class Dijkstra {
	/*@Autowired
	private AirportsMap airportMap;

	private Map<Airport, List<Flight>> map;
	
	public Map<Airport, List<Flight>> getMap() {
		map=airportMap.getGraph();
		return map;
	}
	
	private static void CalculateMinimumDistance(Node evaluationNode,
			Double edgeWeigh, Node sourceNode, Flight flight) {
		Double sourceDistance = sourceNode.getDistance();
		if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
			evaluationNode.setDistance(sourceDistance + edgeWeigh);
			LinkedList<Pair<Node,Flight>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
			Pair<Node,Flight> pair = new Pair<Node,Flight>(sourceNode,flight);
			shortestPath.add(pair);
			evaluationNode.setShortestPath(shortestPath);
		}
	}
	
	private static Node getLowestDistanceNode(Set < Node > unsettledNodes) {
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
	        for (Entry < Node, Pair<Double, Flight>> adjacencyPair: 
	          currentNode.getAdjacentNodes().entrySet()) {
	            Node adjacentNode = adjacencyPair.getKey();
	            Double edgeWeight = adjacencyPair.getValue().getKey();
	            Flight f = adjacencyPair.getValue().getValue();
	            //Comprobar consistencia de horas
	            Boolean consistent = true;
	            Integer listSize=currentNode.getShortestPath().size();
	            if(listSize>0) {
	            	consistent=currentNode.getShortestPath().get(listSize-1).getValue().getArrivalDateTime().isBefore(f.getTakeOffDateTime());
	            	edgeWeight= edgeWeight + Duration.between(currentNode.getShortestPath().get(listSize-1).getValue().getArrivalDateTime(),f.getTakeOffDateTime()).toHours();
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
				double duration = Duration.between(f.getTakeOffDateTime(),f.getArrivalDateTime()).toMinutes();
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
		
		return result; /
	}
	
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
