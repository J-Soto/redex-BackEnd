package pucp.dp1.redex.services.impl.sales;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.sales.IHistorico;
import pucp.dp1.redex.model.sales.Historico;
import pucp.dp1.redex.services.dao.sales.IHistoricoService;

@Service
public class HistoricoService implements IHistoricoService {

    @Autowired
    private IHistorico dao;

    @Override
    public List<Historico> findAll() {
        System.out.println("Find All");
        return dao.findAll();
    }

    @Override
    public Optional<Historico> findById(Integer id){
        return dao.findById(id);
    }

    @Override
    public List<Historico> findByFecha(LocalDate fecha) {
        return dao.findByFecha(fecha);
    }
}
