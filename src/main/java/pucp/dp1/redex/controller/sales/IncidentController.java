package pucp.dp1.redex.controller.sales;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.sales.Incident;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.model.utils.FlightPlanElement;
import pucp.dp1.redex.model.utils.IncidentSummary;
import pucp.dp1.redex.model.utils.SummaryCase;
import pucp.dp1.redex.services.dao.sales.IIncidentService;

@CrossOrigin
@RestController
@RequestMapping("/api/incident")
public class IncidentController {

	@Autowired
	private IIncidentService service;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarIncidentes() {
		ResponseObject response = new ResponseObject();
		try {
			List<Incident> lista = this.service.findByActiveTrue();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/airports", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarIncidentesEnAeropuertos(@Param("simulated") Boolean simulated) {
		ResponseObject response = new ResponseObject();
		try {
			List<AirportElement> lista = this.service.findByAirports(simulated);
//			System.out.println(this.util.convertToDateViaSqlDate(this.util.convertStringToLocalDate(date)));
			if(!lista.isEmpty()) {
				response.setResultado(lista);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE PUDO RECUPERAR LA INFORMACIÓN", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/flightplans", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarIncidentesPorPlanes(@Param("simulated") Boolean simulated) {
		ResponseObject response = new ResponseObject();
		try {
			List<FlightPlanElement> lista = this.service.findByFlightPlans(simulated);
//			System.out.println(this.util.convertToDateViaSqlDate(this.util.convertStringToLocalDate(date)));
			if(!lista.isEmpty()) {
				response.setResultado(lista);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE PUDO RECUPERAR LA INFORMACIÓN", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarIncidentesResumen() {
		ResponseObject response = new ResponseObject();
		try {
			Optional<SummaryCase> sc = this.service.findSummary();
//			System.out.println(this.util.convertToDateViaSqlDate(this.util.convertStringToLocalDate(date)));
			if(sc.isPresent()) {
				response.setResultado(sc);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE PUDO RECUPERAR LA INFORMACIÓN", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/dashboards", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarIncidentesTotal(@Param("simulated") Boolean simulated /*@Param("date") String date, @Param("time") String time*/) {
		ResponseObject response = new ResponseObject();
		try {
			IncidentSummary ic = new IncidentSummary();
			List<AirportElement> byairports=this.service.findByAirports(simulated);
			List<FlightPlanElement> byflightplans=this.service.findByFlightPlans(simulated);
			List<AirportElement> top5Airport= new ArrayList<>();
			List<FlightPlanElement> top5FlightPlans = new ArrayList<>();
			
			Integer i=0;
			while(i<5 && i<byairports.size()) {
				top5Airport.add(byairports.get(i));
				i++;
			}
			
			i=0;
			while(i<5 && i<byflightplans.size()) {
				top5FlightPlans.add(byflightplans.get(i));
				i++;
			}
			
			ic.setByairports(top5Airport);
			ic.setByflightplans(top5FlightPlans);
			if(simulated) {
				ic.setSummaryCase(this.service.findSummary().get());
			}
			response.setResultado(ic);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
