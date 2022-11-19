package pucp.dp1.redex.controller.sales;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javafx.util.Pair;
import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.model.route.RoutePlanStatus;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.router.algorithms.AStar;
import pucp.dp1.redex.router.algorithms.Node;
import pucp.dp1.redex.services.dao.sales.IAirportService;

@CrossOrigin
@RestController
@RequestMapping("/api/airport")
public class AirportController {
	
	@Autowired
	private IAirportService service;
	/*@Autowired
	private Dijkstra servicioD;*/
	@Autowired
	private AStar servicioA;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarAeropuertos() {
		ResponseObject response = new ResponseObject();
		try {
			List<Airport> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// @GetMapping(path = "/getRouteA/{inicio}/{fin}/{cantPackages}", produces = MediaType.APPLICATION_JSON_VALUE)
	// public ResponseEntity<ResponseObject> consultarRutaA(@PathVariable("inicio") Integer start, @PathVariable("fin") Integer objective, @PathVariable("cantPackages") Integer cantPackages){
	// 	ResponseObject response = new ResponseObject();
	// 	NumberFormat formatter = new DecimalFormat("#0.00000"); 
	// 	try {
	// 		Long startTime = System.currentTimeMillis();
	// 		List<Node> nodes = this.servicioA.getShortestPath(start, objective, LocalDate.now(), LocalTime.now().minusHours(5),false,cantPackages);
	// 		Long endTime = System.currentTimeMillis();
	// 		List<List<FlightPlan>> listaFlightPlans = new ArrayList<>();
	// 		for(Node node: nodes){
	// 			List<FlightPlan> lista = new ArrayList<>();
	// 			for(Pair<Node,FlightPlan> p : node.getShortestPath()) {
	// 				lista.add(p.getValue());
	// 			}
	// 			if (node.getId()!=0 && lista.size()>0) {
	// 				System.out.println("Consiguió resultado");
	// 				System.out.println("Tiempo en horas: "+formatter.format(node.getDistance()/60));
	// 			}
	// 			else {
	// 				System.out.println("No consiguió resultado");
	// 			}
	// 			System.out.println("A* in " +  (endTime-startTime) + " milliseconds");
	// 			listaFlightPlans.add(lista);
	// 		}
	// 		response.setResultado(listaFlightPlans);
	// 		response.setEstado(Estado.OK);
	// 		return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
	// 	} catch(Exception e) {
	// 		response.setError(1, "Error", e.getMessage());
	// 		response.setEstado(Estado.ERROR);
	// 		return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	// 	}
	// }
	
	@GetMapping(path = "/findby/city", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarAeropuertoPorCiudad(@RequestParam("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<Airport> airport = this.service.findByCity_id(id);
			if(airport.isPresent()) {
				response.setEstado(Estado.OK);
				response.setResultado(airport);
			} else {
				response.setError(1, "CIUDAD SIN AEROPUERTO REGISTRADO","");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
