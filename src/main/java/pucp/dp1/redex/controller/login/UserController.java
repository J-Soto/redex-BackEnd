package pucp.dp1.redex.controller.login;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.model.login.User;
import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.services.impl.login.UserService;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService usuarioService; 
	
	@GetMapping(path = "/logueo", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarDatosUsuario(@RequestParam("username") String username, @RequestParam("password") String password) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<User> user = this.usuarioService.findByUsernameAndPassword(username, password);
			if(user.isPresent()) {
				response.setResultado(user);
				response.setEstado(Estado.OK);
			} else {
				response.setEstado(Estado.ERROR);
				response.setError(1, "ERROR EN EL LOGIN, VERIFIQUE LOS DATOS", "");
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarUsuarios() {
		ResponseObject response = new ResponseObject();
		try {
			List<User> lista = this.usuarioService.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/time", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> obtenerHora() {
		ResponseObject response = new ResponseObject();
		try {
			LocalDateTime time;
			time=LocalDateTime.now();
			response.setResultado(time);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

