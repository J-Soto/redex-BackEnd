package pucp.dp1.redex.services.impl.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.sales.IClient;
import pucp.dp1.redex.model.sales.Client;
import pucp.dp1.redex.services.dao.sales.IClientService;

@Service
public class ClientService implements IClientService {
	
	@Autowired
	private IClient dao;

	@Override
	public List<Client> findAll() {
		return dao.findAll();
	}

	@Override
	public Optional<Client> findByDocument(String document) {
		return dao.findByDocument(document);
	}

	@Override
	public Client editClient(Client client) {
		return this.dao.save(client);
	}

}
