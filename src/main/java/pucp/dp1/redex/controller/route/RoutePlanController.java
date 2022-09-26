package pucp.dp1.redex.controller.route;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.router.algorithms.AStar;
import pucp.dp1.redex.services.dao.route.IRoutePlanService;

@CrossOrigin
@RestController
@RequestMapping("/api/airport/flight/route")
public class RoutePlanController {

	@Autowired
	private IRoutePlanService service;
	
	@Autowired
	private AStar serviceAStar;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodos() {
		ResponseObject response = new ResponseObject();
		try {
			List<RoutePlan> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/generateRoute", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> generarRuta(@RequestParam("idStart") Integer idStart, @RequestParam("idObjective") Integer idObjective) {
		ResponseObject response = new ResponseObject();
		try {
			RoutePlan plan = serviceAStar.determinRoute(idStart, idObjective, LocalDate.now(), LocalTime.now().minusHours(5),false);
			response.setResultado(plan);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
