package pucp.dp1.redex.services.impl.PACK;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.PACK.IFlight;
import pucp.dp1.redex.dao.storage.IWarehouse;
import pucp.dp1.redex.model.PACK.Flight;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.model.utils.FlightElement;
import pucp.dp1.redex.services.dao.PACK.IFlightService;

@Service
public class FlightService implements IFlightService{

	@Autowired
	private IFlight dao;
	@Autowired
	private  IWarehouse daoA;

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


	@Override
	public List<FlightElement> findBestFlight(Integer start, Integer objective) {
			return this.dao.findBestFlight(start, objective);
	}

	@Override
	public Time findBestFlightTakeOffTime(Integer id) {
			return this.dao.findBestFlightTakeOffTime(id);
	}

	@Override
	public Time findBestFlightArrivalTime(Integer id) {
			return this.dao.findBestFlightArrivalTime(id);
	}

}
