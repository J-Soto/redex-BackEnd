package pucp.dp1.redex.services.dao.PACK;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.model.utils.FlightElement;

public interface IFlightService {
	
	public List<Flight> findByTakeOffAirport(Airport airport);
	public List<Flight> findAll();
	public Optional<Flight> updateCapacity(Integer id, Integer capacity);
	public Optional<Flight> updateOccupiedCapacity(Integer id, Integer capacity);	
	Optional<Flight> findById(Integer id);
	public List<FlightElement> findBestFlight(Integer start,Integer objective);
	public Time findBestFlightTakeOffTime(Integer id);
	public Time findBestFlightArrivalTime(Integer id);

}
