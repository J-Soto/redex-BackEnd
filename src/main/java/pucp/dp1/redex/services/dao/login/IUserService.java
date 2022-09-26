package pucp.dp1.redex.services.dao.login;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.login.User;

public interface IUserService {
	
	public Optional<User> findByUsernameAndPassword(String username, String password);
	public List<User> findAll();
}
