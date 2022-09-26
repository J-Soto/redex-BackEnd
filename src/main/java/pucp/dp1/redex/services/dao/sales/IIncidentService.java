package pucp.dp1.redex.services.dao.sales;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.sales.Incident;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.model.utils.FlightPlanElement;
import pucp.dp1.redex.model.utils.SummaryCase;

public interface IIncidentService {

	public List<Incident> findByActiveTrue();
	
	public List<AirportElement> findByAirports(Boolean simulated);
	
	public List<FlightPlanElement> findByFlightPlans(Boolean simulated);
	
	public Optional<SummaryCase> findSummary();
	
	public void deleteSimulated();
}
