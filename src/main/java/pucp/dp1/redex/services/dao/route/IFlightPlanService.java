package pucp.dp1.redex.services.dao.route;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.route.FlightPlan;

public interface IFlightPlanService {

	public List<FlightPlan> findAll();
	public Optional<FlightPlan> findByFlight_IdFlightAndTakeOffDate(Integer id, Date date);
	public void clenaSimulated();
	public Optional<FlightPlan> updateOccupiedCapacity(Integer id, Integer capacity);

	List<FlightPlan> findByFechaHora(Date fecha, LocalTime horaI, LocalTime horaF);
}

