package pucp.dp1.redex.services.dao.route;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.route.FlightPlan;

public interface IFlightPlanService {

	public List<FlightPlan> findAll();
	public Optional<FlightPlan> findByFlight_IdFlightAndTakeOffDate(Integer id, Date date);
	public void clenaSimulated();
}
