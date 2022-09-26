package pucp.dp1.redex.services.dao.sales;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.sales.Client;

public interface IClientService {

	public List<Client> findAll();
	
	public Optional<Client> findByDocument(String document);
	
	public Client editClient(Client client);
	
}
