package pucp.dp1.redex.services.impl.location;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.location.ICountry;
import pucp.dp1.redex.model.location.Country;
import pucp.dp1.redex.services.dao.location.ICountryService;

@Service
public class CountryService implements ICountryService{

	@Autowired
	private ICountry dao;
	
	@Override
	public List<Country> findAll() {
		return dao.findAllByOrderByName();
	}

	
}
