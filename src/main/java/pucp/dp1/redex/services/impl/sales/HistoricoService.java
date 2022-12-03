package pucp.dp1.redex.services.impl.sales;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT h FROM Historico h WHERE h.fecha = :fecha")
    @Override
    public List<Historico> findByFecha(LocalDate fecha) {
        return dao.findByFecha(fecha);
    }

    @Query("SELECT h FROM Historico h WHERE h.fecha = :fecha AND h.hora BETWEEN :horaI AND  :horaF order by h.hora")
    @Override
    public List<Historico> findByFechaHora(LocalDate fecha, LocalTime horaI, LocalTime horaF) {
        return dao.findByFechaHora(fecha,horaI,horaF);
    }


}
