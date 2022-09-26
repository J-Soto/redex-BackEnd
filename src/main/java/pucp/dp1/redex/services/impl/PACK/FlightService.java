package pucp.dp1.redex.services.impl.PACK;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.PACK.IFlight;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.services.dao.PACK.IFlightService;

@Service
public class FlightService implements IFlightService{

	@Autowired
	private IFlight dao;

	@Override
	public List<Flight> findByTakeOffAirport(Airport airport) {
		return dao.findByTakeOffAirport(airport);
	}

	@Override
	public List<Flight> findAll() {
		return dao.findAll();
	}

	@Override
	public Optional<Flight> updateCapacity(Integer id, Integer capacity) {
		Optional<Flight> ff = this.dao.findById(id);
		if(ff.isPresent()) {
			ff.get().setCapacity(capacity);
			this.dao.save(ff.get());
			return ff;
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Flight> findById(Integer id) {
		return this.dao.findById(id);
	}

}
