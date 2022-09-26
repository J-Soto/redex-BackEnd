package pucp.dp1.redex.dao.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.sales.Client;

public interface IClient extends JpaRepository<Client, Integer>{

	List<Client> findAll();
	
	Optional<Client> findByDocument(String document);
}
