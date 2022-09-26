package pucp.dp1.redex.controller.sales;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.sales.Client;
import pucp.dp1.redex.services.dao.sales.IClientService;

@CrossOrigin
@RestController
@RequestMapping("/api/client")
public class ClientController {
	
	@Autowired
	private IClientService service;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarClientes() {
		ResponseObject response = new ResponseObject();
		try {
			List<Client> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/findby/document", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarClientesPorDocumento(@RequestParam("document") String document) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<Client> client = this.service.findByDocument(document);
			if(client.isPresent()) {
				response.setResultado(client);
				response.setEstado(Estado.OK);
			} else {
				response.setEstado(Estado.ERROR);
				response.setError(1, "NO SE HA ENCONTRADO EL CLIENTE", "");
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(path = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> editarCliente(@Valid @RequestBody Client client) {
		ResponseObject response = new ResponseObject();
		try {
			Client edit_client = this.service.editClient(client);
			if(edit_client!=null) {
				response.setResultado(edit_client);
				response.setEstado(Estado.OK);
			} else {
				response.setError(1,"NO SE PUDO GUARDAR EL CLIENTE","");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "OCURRIO UN ERROR AL GUARDAR EL CLIENTE", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
