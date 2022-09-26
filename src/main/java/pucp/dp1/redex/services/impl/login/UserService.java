package pucp.dp1.redex.services.impl.login;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pucp.dp1.redex.dao.login.IUser;
import pucp.dp1.redex.model.login.User;
import pucp.dp1.redex.services.dao.login.IUserService;

@Service
public class UserService implements IUserService{

	@Autowired
	private IUser dao;
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
	public Optional<User> findByUsernameAndPassword(String username, String password) {
		Optional<User> findUser = this.dao.findByUsername(username);
		if(findUser.isPresent()) {
			boolean isPasswordMatched = passwordEncoder().matches(password, findUser.get().getPassword());
			if(!isPasswordMatched) return Optional.empty();
		}
		return findUser;
	}

	@Override
	public List<User> findAll() {
		return dao.findAll();
	}
}