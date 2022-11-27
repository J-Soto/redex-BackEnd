package pucp.dp1.redex.services.dao.location;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.location.Country;

public interface ICountryService {
	
	public List<Country> findAll();
	Optional<Country> findById(Integer id);

}
