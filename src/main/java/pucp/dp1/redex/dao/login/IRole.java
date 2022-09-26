package pucp.dp1.redex.dao.login;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.login.Role;

public interface IRole extends JpaRepository<Role, Integer>{

	List<Role> findAll();
}
