package pucp.dp1.redex.services.impl.storage;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.storage.IWarehouse;
import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.services.dao.storage.IWarehouseService;

@Service
public class WarehouseService implements IWarehouseService {
	
	@Autowired
	private IWarehouse dao;

	@Override
	public List<Warehouse> findAll() {
		return dao.findAll();
	}

	@Override
	public Optional<Warehouse> findByAirport_id(Integer id) {
		return dao.findByAirport_id(id);
	}

	@Override
	public Optional<Warehouse> updateCapacity(Integer id, Integer capacity) {
		Optional<Warehouse> wf = this.dao.findByAirport_id(id);
		if(wf.isPresent()) {
			wf.get().setCapacity(capacity);
			this.dao.save(wf.get());
			return wf;
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Warehouse> updateOccupiedCapacity(Integer id, Integer capacity) {
		Optional<Warehouse> wf = this.dao.findByAirport_id(id);
		if(wf.isPresent()) {
			wf.get().setOccupiedCapacity(capacity);
			this.dao.save(wf.get());
			return wf;
		} else {
			return Optional.empty();
		}
	}

	@Override
	public List<AirportElement> getTimeLineWarehouse(Date date, Boolean simulated) {
		return this.dao.getTimeLineWarehouse(date, simulated);
	}

	@Override
	public Optional<Warehouse> findByAirport_code(String code) {
		return this.dao.findByAirport_code(code);
	}

	@Override
	public List<AirportElement> findByRange(Boolean simulated, Integer id, Date idate, Date fdate) {
		return this.dao.findByRange(simulated, id, idate, fdate);
	}

}
