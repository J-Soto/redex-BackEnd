package pucp.dp1.redex.controller.storage;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.router.algorithms.AStar;
import pucp.dp1.redex.services.dao.storage.IWarehouseService;

@CrossOrigin
@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

	@Autowired
	private IWarehouseService service;
	
	@Autowired
	private AStar utils;
	
	@GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodos() {
		ResponseObject response = new ResponseObject();
		try {
			List<Warehouse> lista = this.service.findAll();
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarInfo(@Param("id") Integer id) {
		ResponseObject response = new ResponseObject();
		try {
			Optional<Warehouse> wf = this.service.findByAirport_id(id);
			if(wf.isPresent()) {
				response.setResultado(wf.get());
				response.setEstado(Estado.OK);
			} else {
				response.setEstado(Estado.ERROR);
				response.setError(1,"ERROR AL CONSULTAR INFO","");
			}
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
			Optional<Warehouse> wf = this.service.updateCapacity(id, capacity);
			if(wf.isPresent()) {
				response.setEstado(Estado.OK);
			} else {
				response.setError(1, "NO SE PUDO ACTUALIZAR LA CAPACIDAD", "");
				response.setEstado(Estado.ERROR);
			}
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/timeline", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTimeLine(@Param("date") String date, @Param("time") String time, @Param("simulated") Boolean simulated) {
		ResponseObject response = new ResponseObject();
		try {
			LocalDate x = this.utils.convertStringToLocalDate(date);
			LocalTime y = this.utils.convertStringToLocalTime(time);
			//y=y.minusHours(5);
			Date z = this.utils.convertDateAndTimeToDate(this.utils.convertToDateViaSqlDate(x), Time.valueOf(y));
			System.out.println(z);
			List<AirportElement> ls = this.service.getTimeLineWarehouse(z,simulated);
			int i=0;
			while(i<ls.size()) {
				Optional<Warehouse> warehouse = this.service.findByAirport_code(ls.get(i).getCode());
				System.out.println(warehouse.get().getId());
				System.out.println(ls.get(i).getX());
				System.out.println(warehouse.get().getCapacity());
				Double perc = (Double.valueOf((Double.valueOf(ls.get(i).getX())/Double.valueOf((warehouse.get().getCapacity())))));
				System.out.println(perc);
				AirportElement ae = new AirportElement(ls.get(i).getCode(), ls.get(i).getCity(), ls.get(i).getCountry(), ls.get(i).getX());
				/*Debido a los registros que viajan en el tiempo*/
				if(perc>1) {
					perc=1.0;
				}
				ae.setPercentage(perc*100);
				ls.set(i, ae);
				i++;
			}
			//ordenar lista
			Collections.sort(ls, Comparator.comparing(AirportElement::getPercentage));
			response.setResultado(ls);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/fails/range", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarCaidasPorRango(@Param("simulated") Boolean simulated, @Param("id") Integer id, @Param("idate") String idate, @Param("fdate") String fdate) {
		ResponseObject response = new ResponseObject();
		try {
			LocalDate date1 = this.utils.convertStringToLocalDate(idate);
			Date formatDate1 = this.utils.convertToDateViaSqlDate(date1);
			System.out.println(formatDate1);
			LocalDate date2 = this.utils.convertStringToLocalDate(fdate);
			Date formatDate2 = this.utils.convertToDateViaSqlDate(date2);
			System.out.println(formatDate2);
			List<AirportElement> ls = this.service.findByRange(simulated, id, formatDate1, formatDate2);
			int i=0;
			while(i<ls.size()) {
				AirportElement ae = new AirportElement(ls.get(i).getCode(), ls.get(i).getName(), ls.get(i).getDate(), ls.get(i).getCity(), ls.get(i).getCountry(), ls.get(i).getCount());
				ae.setLdate((this.utils.convertToLocalDateViaInstant(ls.get(i).getDate())).plusDays(1));
				ls.set(i, ae);
				i++;
			}
			//ordenar lista
			Collections.sort(ls, Comparator.comparing(AirportElement::getLdate));
			response.setResultado(ls);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
