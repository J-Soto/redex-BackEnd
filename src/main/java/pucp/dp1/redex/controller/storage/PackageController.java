package pucp.dp1.redex.controller.storage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.services.dao.storage.IPackageService;

@CrossOrigin
@RestController
@RequestMapping("/api/package")
public class PackageController {

	@Autowired
	private IPackageService service;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodos() {
		ResponseObject response = new ResponseObject();
		try {
			List<Package> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	@GetMapping(path = "/outgoing", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<ResponseObject> consultarSalidas(@Param("id") Integer id) {
//		ResponseObject response = new ResponseObject();
//		try {
//			List<Package> lista = this.service.findOutgoingPackages(id);
//			response.setResultado(lista);
//			response.setEstado(Estado.OK);
//			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
//		} catch(Exception e) {
//			response.setError(1, "Error", e.getMessage());
//			response.setEstado(Estado.ERROR);
//			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	@GetMapping(path = "/arriving", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<ResponseObject> consultarLlegadas(@Param("id") Integer id) {
//		ResponseObject response = new ResponseObject();
//		try {
//			List<Package> lista = this.service.findArrivingPackages(id);
//			response.setResultado(lista);
//			response.setEstado(Estado.OK);
//			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
//		} catch(Exception e) {
//			response.setError(1, "Error", e.getMessage());
//			response.setEstado(Estado.ERROR);
//			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
}
