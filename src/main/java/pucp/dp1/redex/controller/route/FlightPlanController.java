package pucp.dp1.redex.controller.route;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.services.dao.route.IFlightPlanService;
import pucp.dp1.redex.services.impl.route.FlightPlanService;
import pucp.dp1.redex.services.impl.storage.WarehouseService;

@CrossOrigin
@RestController
@RequestMapping("/api/airport/flight/plan")
public class FlightPlanController {

	@Autowired
	private FlightPlanService serviceFlightPlan;
	@Autowired
	private IFlightPlanService service;
	@Autowired
	private WarehouseService serviceWarehouse;
	
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
	public LocalTime convertStringToLocalTime(String time) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return LocalTime.parse(time, formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@GetMapping(path = "/allDay", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarTodosPorDia(@Param("fecha") String fecha,@Param("horaI") String horaI,@Param("horaF") String horaF) {
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		ResponseObject response = new ResponseObject();
		try {
			String datereq = fecha.substring(1, 11).replace("-", "");
			Date dateDate;
			SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
			dateDate = formatterDate.parse(datereq);
			LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
			Time time1 = Time.valueOf(convertStringToLocalTime(horaI));
			Time time2 = Time.valueOf(convertStringToLocalTime(horaF));
			List<FlightPlan> lista = this.service.findByFechaHora(dateDate,time1,time2);
			//List<FlightPlan> listaFiltrada = lista.stream().filter(x -> x.getTakeOffDate().equals(dateDate)).collect(Collectors.toList());
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			//e.printStackTrace();
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "/recibirHora", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> recibirHora(@Param("hora") String hora) {
		ResponseObject response = new ResponseObject();
		//"2022-08-03T07:00:00.000Z"
		try {
			String hour = hora;
			String date = hora;
			String datereq = null;
			//Fecha
			date = date.substring(1, 11).replace("-", "");
			//date: 20220803
			//Hora
			hour = hour.substring(12, 17);
			//hour: 07:00
			datereq = date + hour;
			//datereq: 2022-08-0307:00
			SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
			Date dateDate;
			dateDate = formatterDate.parse(date);
			LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
			SimpleDateFormat formatterHour= new SimpleDateFormat("HH:mm");
			Date dateTime;
			dateTime = formatterHour.parse(hour);
			LocalTime hour1 = LocalTime.parse(new SimpleDateFormat("HH:mm").format(dateTime));
			FlightPlan fpResult=null;
			List<FlightPlan> listFP;
			Date dia;
			listFP = serviceFlightPlan.findAll();
			for(FlightPlan fp:listFP){
				dia=Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant());
				if(fp.getArrivalDate().equals(dia) && fp.getArrivalTimeUtc().toLocalTime().isBefore(hour1)){
					actualizarWarehouse(fp);
				}
			}
			response.setResultado(0);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch(Exception e) {
			//e.printStackTrace();
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void actualizarWarehouse(FlightPlan fp) {
		Integer cant=null;
		Warehouse start = null;
		Warehouse end = null;
		cant=fp.getOccupiedCapacity();
		start = fp.getFlight().getArrivalAirport().getWarehouse();
		end = fp.getFlight().getTakeOffAirport().getWarehouse();

		if(fp.getRevisado()==0){
			serviceWarehouse.updateOccupiedCapacity(start.getId(), start.getOccupiedCapacity()-cant);
			serviceWarehouse.updateOccupiedCapacity(end.getId(), end.getOccupiedCapacity()-cant);
			serviceFlightPlan.updateRevisado(fp.getId(),1);
			
		}

	}
}
