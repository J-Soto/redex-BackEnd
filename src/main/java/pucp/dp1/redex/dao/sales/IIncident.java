package pucp.dp1.redex.dao.sales;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pucp.dp1.redex.model.sales.Incident;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.model.utils.FlightPlanElement;

public interface IIncident extends JpaRepository<Incident, Integer>{

	@Query
	List<Incident> findByActiveTrue();
	
	@Query(value = "select new pucp.dp1.redex.model.utils.AirportElement(i.airport.code, i.airport.description, min(i.registerDate), max(i.registerDate), i.airport.city.name , i.airport.city.country.name, count(*)) from Incident i where i.airport.id is not null and i.active = true and i.isSimulated = :simulated group by i.airport.id order by count(*) desc")
	List<AirportElement> findByAirports(@Param("simulated") Boolean simulated);
//	List<Incident> findTop5ByAirport_idNotNullAndLastLoadOrderByCountsDesc();
	
	@Query(value = "select new pucp.dp1.redex.model.utils.FlightPlanElement(i.flightPlan.flight.id, i.flightPlan.flight.takeOffAirport.code, i.flightPlan.flight.arrivalAirport.code, count(*), i.flightPlan.flight.takeOffAirport.city.name, i.flightPlan.flight.takeOffAirport.city.country.name,  i.flightPlan.flight.arrivalAirport.city.name,  i.flightPlan.flight.arrivalAirport.city.country.name) from Incident i where i.flightPlan.id is not null and i.active = true and i.isSimulated = :simulated group by i.flightPlan.flight.id order by count(*) desc")
	List<FlightPlanElement> findByFlightPlans(@Param("simulated") Boolean simulated);
//	List<Incident> findTop5ByFlightPlan_idNotNullAndLastLoadOrderByCountsDesc();
	
	@Transactional
	@Modifying
	@Query(value = "update Incident i set i.active = false where i.isSimulated = true")
	void deleteSimulated();
	
}
