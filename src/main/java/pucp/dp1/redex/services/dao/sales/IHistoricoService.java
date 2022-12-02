package pucp.dp1.redex.services.dao.sales;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.sales.Historico;

public interface IHistoricoService {

    public List<Historico> findAll();

    Optional<Historico> findById(Integer id);
}
