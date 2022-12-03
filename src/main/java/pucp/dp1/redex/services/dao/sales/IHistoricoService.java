package pucp.dp1.redex.services.dao.sales;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import pucp.dp1.redex.model.sales.Historico;

public interface IHistoricoService {

    public List<Historico> findAll();

    Optional<Historico> findById(Integer id);

    List<Historico> findByFecha(LocalDate fecha);
}
