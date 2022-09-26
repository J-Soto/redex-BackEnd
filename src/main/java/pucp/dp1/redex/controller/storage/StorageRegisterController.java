package pucp.dp1.redex.controller.storage;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.dao.utils.ISummaryCase;
import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.storage.PackageStatus;
import pucp.dp1.redex.model.storage.StorageRegister;
import pucp.dp1.redex.model.utils.SummaryCase;
import pucp.dp1.redex.services.dao.storage.IStorageRegisterService;
import pucp.dp1.redex.services.impl.route.FlightPlanService;
import pucp.dp1.redex.services.impl.sales.DispatchService;
import pucp.dp1.redex.services.impl.sales.IncidentService;
import pucp.dp1.redex.services.impl.storage.PackageService;

@CrossOrigin
@RestController
@RequestMapping("/api/storage")
public class StorageRegisterController {

	@Autowired
	private IStorageRegisterService service;
	
	@Autowired
	private IncidentService serviceIncident;
	
	@Autowired
	private PackageService servicePackage;
	
	@Autowired
	private DispatchService serviceDispatch;
	
	@Autowired
	private FlightPlanService serviceFlightPlan;
	
	
	@Autowired
	private ISummaryCase daoSummary;
	
	@GetMapping(path = "/warehouse", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodosPorAlmacen(@RequestParam("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			List<StorageRegister> lista = this.service.findByWarehouse_id(id);
			if(lista!=null){
				response.setResultado(lista);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO HAY REGISTROS EN EL ALMACEN", "");
				response.setEstado(Estado.ERROR);
			}
			
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "OCURRIO UN ERROR EN LA BUSQUEDA", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> guardarRegistro(@Valid @RequestBody StorageRegister register) {
		ResponseObject response = new ResponseObject();
		try {
			StorageRegister reg = this.service.save(register);
			if(reg!=null) {
				response.setResultado(reg);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "ERROR AL GUARDAR REGISTRO", "");
				response.setEstado(Estado.OK);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "ERROR AL GUARDAR REGISTRO", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/getRegistersByDate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarRegistrosEnUnaFecha(@RequestParam("date") String Sdate, @RequestParam("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date = formatter.parse(Sdate);
			List<StorageRegister> lista = this.service.findAllPresentOnDate(date, id);
			if(lista!=null){
				response.setResultado(lista);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO HAY REGISTROS EN EL ALMACEN", "");
				response.setEstado(Estado.ERROR);
			}
			
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "OCURRIO UN ERROR EN LA BUSQUEDA", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/deleteSimulated", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> desactivarSimulados() {
		ResponseObject response = new ResponseObject();
		try {
			this.service.deleteSimulated();
			this.serviceIncident.deleteSimulated();
			this.servicePackage.deleteSimulated(PackageStatus.SIMULADO);
			this.serviceDispatch.deleteSimulated(DispatchStatus.SIMULADO);
			this.serviceFlightPlan.clenaSimulated();
			Optional<SummaryCase> sc = this.daoSummary.findById(1);
			if(sc.isPresent()) {
				sc.get().setFails(0);
				sc.get().setOk(0);
				sc.get().setLate(0);
				this.daoSummary.save(sc.get());
			}
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "OCURRIO UN ERROR EN LA ACTUALIZACION", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/update/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> actualizarRecepcion(@Param("idpack") Integer idPack, @Param("idWarehouse") Integer idWarehouse) {
		ResponseObject response = new ResponseObject();
		try {
			String msg = this.service.updateCheckIn(idPack, idWarehouse);
			if(msg.equals("OK")) {
				response.setResultado(msg);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "ERROR AL ACTUALIZAR", "");
				response.setEstado(Estado.OK);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "ERROR AL ACTUALIZAR", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/update/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> actualizarSalida(@Param("idpack") Integer idPack, @Param("idWarehouse") Integer idWarehouse) {
		ResponseObject response = new ResponseObject();
		try {
			String msg = this.service.updateCheckOut(idPack, idWarehouse);
			if(msg.equals("OK")) {
				response.setResultado(msg);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "ERROR AL ACTUALIZAR", "");
				response.setEstado(Estado.OK);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "ERROR AL ACTUALIZAR", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
