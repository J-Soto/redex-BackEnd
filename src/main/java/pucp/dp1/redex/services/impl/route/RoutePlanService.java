package pucp.dp1.redex.services.impl.route;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.route.IRoutePlan;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.services.dao.route.IRoutePlanService;

@Service
public class RoutePlanService implements IRoutePlanService{

	@Autowired
	private IRoutePlan dao;
	
	@Override
	public List<RoutePlan> findAll() {
		return dao.findAll();
	}

}
