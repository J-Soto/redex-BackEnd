package pucp.dp1.redex.dao.route;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.route.RoutePlan;

public interface IRoutePlan extends JpaRepository<RoutePlan, Integer> {

	List<RoutePlan> findAll();
}
