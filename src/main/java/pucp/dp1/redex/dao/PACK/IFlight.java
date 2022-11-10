package pucp.dp1.redex.dao.PACK;

import java.util.List;
import java.sql.Time;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.model.utils.FlightElement;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pucp.dp1.redex.model.utils.AirportElement;

public interface IFlight extends JpaRepository<Flight, Integer> {
 
	List<Flight> findByTakeOffAirport(Airport airport);
	List<Flight> findAll();



	@Query("select new pucp.dp1.redex.model.utils.FlightElement(f.idFlight, f.arrivalAirport, f.takeOffAirport) from Flight f where f.takeOffAirport.id=:start and f.arrivalAirport.id=:objective")
	List<FlightElement> findBestFlight(@Param("start") Integer start, @Param("objective") Integer objective);

	//@Query("select new pucp.dp1.redex.model.utils.FlightElement(f.idFlight, f.arrivalAirport, f.takeOffAirport, f.takeOffTime.toLocalTime(), f.arrivalTime.toLocalTime()) from Flight f where f.takeOffAirport.id=:start and f.arrivalAirport.id=:objective")
	//List<FlightElement> findBestFlight(@Param("start") Integer start, @Param("objective") Integer objective);

	//@Query("select new pucp.dp1.redex.model.utils.FlightElement(f.takeOffTime) from Flight f where f.takeOffTime=f.takeOffTime")
	//List<FlightElement> findBestFlight(@Param("start") Integer start, @Param("objective") Integer objective);

	@Query(value = "select f.takeOffTime from Flight f where f.idFlight=:id")
	Time findBestFlightTakeOffTime(@Param("id") Integer id);

	@Query(value = "select f.arrivalTime from Flight f where f.idFlight=:id")
	Time findBestFlightArrivalTime(@Param("id") Integer id);

	//, f.takeOffTime,f.arrivalTime


}
	