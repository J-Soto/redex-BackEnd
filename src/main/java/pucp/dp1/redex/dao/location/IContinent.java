package pucp.dp1.redex.dao.location;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.location.Continent;

public interface IContinent extends JpaRepository<Continent, Integer>{
	
	List<Continent> findAll();
	
}
