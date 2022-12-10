package pucp.dp1.redex.controller.sales;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.sales.Historico;
import pucp.dp1.redex.model.utils.TrackingHistory;
import pucp.dp1.redex.model.utils.TrackingList;
import pucp.dp1.redex.services.dao.sales.IDispatchService;

@CrossOrigin
@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

	@Autowired
	private IDispatchService service;

	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarEnvios() {
		ResponseObject response = new ResponseObject();
		try {
			List<Dispatch> lista = this.service.findByActiveTrue();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "/tracking", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarEnviosPorCodigoDeRastreo(@RequestParam("code") String code) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<Dispatch> dispatch = this.service.findByTrackingCode(code);
			if (dispatch.isPresent()) {
				response.setResultado(dispatch);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE HA ENCONTRADO EL ENVIO", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "/findby/oairport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarEnviosPorAeropuertoOrigen(@RequestParam("code") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			List<Dispatch> list = this.service.findByOriginAirport_idAndStatusNotOrderByRegisterDateDesc(id);
			if (!list.isEmpty()) {
				response.setEstado(Estado.OK);
				response.setResultado(list);
			} else {
				response.setError(1, "NO HAY ENVIOS REGISTRADOS", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/findby/dairport", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarEnviosPorAeropuertoDestino(@RequestParam("code") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			List<Dispatch> list = this.service.findByDestinationAirport_idAndStatusNotOrderByRegisterDateDesc(id);
			if (!list.isEmpty()) {
				response.setEstado(Estado.OK);
				response.setResultado(list);
			} else {
				response.setError(1, "NO HAY ENVIOS REGISTRADOS", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(path = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> guardarPedido(@Valid @RequestBody Dispatch dispatch) {
		ResponseObject response = new ResponseObject();
		try {
			Dispatch savedDispatch = this.service.save(dispatch, false, null);
			if (savedDispatch != null) {
				response.setEstado(Estado.OK);
				response.setResultado(savedDispatch);
			} else {
				response.setError(1, "NO SE PUDO GUARDAR EL ENVIO", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "OCURRIO UN ERROR AL GUARDAR EL ENVIO", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(path = "/update/status", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> actualizarEstadoPedido(@Valid @RequestBody Map<String, Object> payload) {
		ResponseObject response = new ResponseObject();
		try {
			String trackingCode=payload.get("tracking").toString();
			DispatchStatus status=DispatchStatus.valueOf(payload.get("status").toString());
			
			Optional<Dispatch> findDispatch = this.service.updateState(trackingCode, status);
			if(findDispatch.isPresent()) {
				response.setEstado(Estado.OK);
				response.setResultado(findDispatch.get());
			} else {
				response.setError(1, "NO SE PUDO ACTUALIZAR EL ENVIO", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "OCURRIO UN ERROR AL ACTUALIZAR EL ENVIO", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping(path = "/envioMuerte", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarEnviosPorAeropuertoDestino() {
		ResponseObject response = new ResponseObject();
		try {
			Historico muerte =this.service.envioMuerte();
			if (muerte!=null) {
				response.setEstado(Estado.OK);
				response.setResultado(muerte);
			} else {
				response.setError(1, "ERROR EN MUERTE", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping(path = "/upload/zip", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> procesamientoMasivo(MultipartHttpServletRequest request) {
	//public ResponseEntity<ResponseObject> procesamientoMasivo(MultipartHttpServletRequest request, Date date) {
		ResponseObject response = new ResponseObject();
		try {
			//MultipartHttpServletRequest request = (MultipartHttpServletRequest) params.getFile("file");
			//Date date=params.get("date");
			String check = this.service.masiveLoad(request);
			if (check.equals("OK")) {
				response.setEstado(Estado.OK);
			} else {
				if(check.equals("COLAPSO")){
					response.setError(1, "ERROR COLAPSO", "");;
					response.setEstado(Estado.COLAPSO);
				}

				else{response.setError(1, "ERROR EN EL PROCESO", "");
				response.setEstado(Estado.ERROR);}
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "ERROR EN EL PROCESO", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/outgoing", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarSalidas(@Param("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			List<Dispatch> lista = this.service.findOutgoingDispatchs(id);
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/arriving", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarLlegadas(@Param("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			List<Dispatch> lista = this.service.findArrivingDispatchs(id);
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarHistorialTracking(@Param("code") String code) {
		ResponseObject response = new ResponseObject();
		try {					
			List<TrackingHistory> history = this.service.findTrackingHistory(code);
			if(history!=null) {
				TrackingList element = new TrackingList();
				element.setHistory(history);
				element.setDispatch(history.get(0).getDispatch());
				response.setResultado(element);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE ENCONTRO HISTORIAL DEL ENVIO","");
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
