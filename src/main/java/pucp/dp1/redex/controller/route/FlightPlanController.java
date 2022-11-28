package pucp.dp1.redex.controller.route;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.services.dao.route.IFlightPlanService;

@CrossOrigin
@RestController
@RequestMapping("/api/airport/flight/plan")
public class FlightPlanController {

	@Autowired
	private IFlightPlanService service;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodos() {
		ResponseObject response = new ResponseObject();
		try {
			List<FlightPlan> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping(path = "/allDay", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodosPorDia(@RequestParam("fecha") String fecha) {
		ResponseObject response = new ResponseObject();
		try {
			String datereq = fecha.substring(1, 11).replace("-", "");
			Date dateDate;
			SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
			dateDate = formatterDate.parse(datereq);
			LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
			List<FlightPlan> lista = this.service.findAll();
			List<FlightPlan> listaFiltrada = lista.stream().filter(x -> x.getTakeOffDate().equals(date1)).collect(Collectors.toList());
			response.setResultado(listaFiltrada);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
