package pucp.dp1.redex.services.impl.location;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.location.ICity;
import pucp.dp1.redex.model.location.City;
import pucp.dp1.redex.services.dao.location.ICityService;

@Service
public class CityService implements ICityService{

	@Autowired
	private ICity dao;
	
	@Override
	public List<City> findAll() {
		return dao.findAll();
	}

	@Override
	public Optional<City> findByCountry_id(Integer id) {
		return dao.findByCountry_id(id);
	}

	
}
