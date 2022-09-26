package pucp.dp1.redex.dao.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.location.Country;

public interface ICountry extends JpaRepository<Country, Integer> {

	List<Country> findAllByOrderByName();
	
}
