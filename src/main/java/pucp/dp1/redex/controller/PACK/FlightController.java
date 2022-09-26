package pucp.dp1.redex.controller.PACK;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.services.dao.PACK.IFlightService;

@CrossOrigin
@RestController
@RequestMapping("/api/airport/flight")
public class FlightController {
	
	@Autowired
	private IFlightService service;
	
	@PostMapping(path = "/takeoffs", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarVuelosPorAeropuerto(@Valid @RequestBody Airport airport) {
		ResponseObject response = new ResponseObject();
		try {
			List<Flight> lista = this.service.findByTakeOffAirport(airport);
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodos() {
		ResponseObject response = new ResponseObject();
		try {
			List<Flight> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/update/capacity", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> actualizarCapacidad(@Param("id") Integer id, @Param("capacity") Integer capacity) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<Flight> ff = this.service.updateCapacity(id, capacity);
			if(ff.isPresent()) {
				response.setEstado(Estado.OK);
			} else {
				response.setEstado(Estado.ERROR);
				response.setError(1, "NO SE PUDO ACTUALIZAR LA CAPACIDAD DEL VUELO","");
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarVuelo(@Param("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<Flight> ff = this.service.findById(id);
			if(ff.isPresent()) {
				response.setResultado(ff.get());
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE ENCONTRO VUELO", "");
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
