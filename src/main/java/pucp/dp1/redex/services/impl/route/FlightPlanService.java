package pucp.dp1.redex.services.impl.route;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.route.IFlightPlan;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.services.dao.route.IFlightPlanService;

@Service
public class FlightPlanService implements IFlightPlanService{
	@Autowired
	private IFlightPlan dao;
	@Override
	public List<FlightPlan> findAll() {
		return dao.findAll();
	}
	@Override
	public Optional<FlightPlan> findByFlight_IdFlightAndTakeOffDate(Integer id, Date date) {
		return dao.findByFlight_IdFlightAndTakeOffDate(id, date);
	}
	@Override
	public void clenaSimulated() {
		dao.cleanSimulated();
	}
	@Override
	public Optional<FlightPlan> updateOccupiedCapacity(Integer id, Integer capacity) {
		Optional<FlightPlan> ff = this.dao.findById(id);
		if(ff.isPresent()) {
			ff.get().setOccupiedCapacity(capacity);
			this.dao.save(ff.get());
			return ff;
		} else {
			return Optional.empty();
		}
	}
}
