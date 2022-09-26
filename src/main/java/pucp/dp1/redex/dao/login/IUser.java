package pucp.dp1.redex.dao.login;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.login.User;

public interface IUser extends JpaRepository<User, Integer>{
	
	Optional<User> findByUsername(String username);
	List<User> findAll();
	
}
