package pucp.dp1.redex.services.dao.PACK;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;

public interface IFlightService {
	
	public List<Flight> findByTakeOffAirport(Airport airport);
	public List<Flight> findAll();
	public Optional<Flight> updateCapacity(Integer id, Integer capacity);
	Optional<Flight> findById(Integer id);

}
