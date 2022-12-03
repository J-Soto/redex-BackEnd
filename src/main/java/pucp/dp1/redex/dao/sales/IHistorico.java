package pucp.dp1.redex.dao.sales;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.sales.Historico;

public interface IHistorico extends JpaRepository<Historico, Integer> {

    List<Historico> findAll();

    Optional<Historico> findById(Integer id);

    //pattern "dd/MM/yyyy"
     List<Historico> findAllByFecha(Date fecha);
}

