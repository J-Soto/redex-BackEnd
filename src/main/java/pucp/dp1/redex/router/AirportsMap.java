package pucp.dp1.redex.router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.services.dao.PACK.IFlightService;
import pucp.dp1.redex.services.impl.sales.AirportService;

@Service
public class AirportsMap {
	private Map<Airport, List<Flight>> map;
	//rivate LocalDateTime time;
	@Autowired
	private AirportService serviceAirport;
	@Autowired
	private IFlightService serviceFlight;
	
	public AirportsMap() {
		/*Armar un grafo según la hora actual y un límite de plazo*/
		System.out.println("Constructor");
		//this.time=LocalDateTime.now();
		//System.out.println(this.time);
		//this.maxTime=2400;
		this.map=new HashMap<>();
	}

	public List<Airport> obtainAirpots() {
		System.out.println("Servicio");
		return serviceAirport.findAll();
	}

	public Map<Airport, List<Flight>> getGraph(Map <Airport, List<Flight>> listaVuelosPorAeropuerto) {
		List<Airport> airports = this.obtainAirpots();
		listaVuelosPorAeropuerto.forEach((airport, flights) -> {
			airport.setWarehouse(airports.get(airports.indexOf(airport)).getWarehouse());
			flights = flights;
		});
		return listaVuelosPorAeropuerto;
	}

	public void setGraph(Map<Airport, List<Flight>> map) {
		this.map = map;
	}
	
}
