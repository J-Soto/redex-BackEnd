package pucp.dp1.redex.dao.PACK;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;

public interface IFlight extends JpaRepository<Flight, Integer> {
 
	List<Flight> findByTakeOffAirport(Airport airport);
	List<Flight> findAll();
}
