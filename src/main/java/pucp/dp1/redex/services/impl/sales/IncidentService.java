package pucp.dp1.redex.services.impl.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.sales.IIncident;
import pucp.dp1.redex.dao.utils.ISummaryCase;
import pucp.dp1.redex.model.sales.Incident;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.model.utils.FlightPlanElement;
import pucp.dp1.redex.model.utils.SummaryCase;
import pucp.dp1.redex.services.dao.sales.IIncidentService;

@Service
public class IncidentService implements IIncidentService {
	
	@Autowired
	private ISummaryCase daoSummary;
	
	@Autowired
	private IIncident dao;
	
	@Override
	public List<Incident> findByActiveTrue() {
		return dao.findByActiveTrue();
	}

	@Override
	public List<AirportElement> findByAirports(Boolean simulated) {
		return this.dao.findByAirports(simulated);
	}

	@Override
	public List<FlightPlanElement> findByFlightPlans(Boolean simulated) {
		return this.dao.findByFlightPlans(simulated);
	}

	@Override
	public Optional<SummaryCase> findSummary() {
		return this.daoSummary.findById(1);
	}

	@Override
	public void deleteSimulated() {
		this.dao.deleteSimulated();
	}
}
