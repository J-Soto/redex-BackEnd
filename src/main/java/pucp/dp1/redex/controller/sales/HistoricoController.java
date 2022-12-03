package pucp.dp1.redex.controller.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.model.sales.Historico;
import pucp.dp1.redex.services.dao.sales.IHistoricoService;
import pucp.dp1.redex.services.impl.sales.HistoricoService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @GetMapping(path = "/allByFecha", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> consultarHistoricoPorFecha(@RequestParam("fecha") String fecha) {
        ResponseObject response = new ResponseObject();
        try {
            String datereq = fecha.substring(1, 11).replace("-", "");
            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
            Date dateDate;
            dateDate = formatterDate.parse(datereq);
            LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
            List<Historico> lista = this.historicoService.findByFecha(date1);
            response.setResultado(lista);
            response.setEstado(Estado.OK);
            return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
        } catch(Exception e) {
            response.setError(1, "Error", e.getMessage());
            response.setEstado(Estado.ERROR);
            return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/allByFechaPaisSalida", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> consultarHistoricoPorFechaPorPaisSalida(@RequestParam("fecha") String fecha,@RequestParam("paisSalida") String codigo) {
        ResponseObject response = new ResponseObject();
        try {
            String datereq = fecha.substring(1, 11).replace("-", "");
            SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
            Date dateDate;
            dateDate = formatterDate.parse(datereq);
            LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
            List<Historico> lista = this.historicoService.findByFecha(date1).stream().filter(historico -> historico.getCodigoPaisSalida().equals(codigo)).collect(Collectors.toList());
            response.setResultado(lista);
            response.setEstado(Estado.OK);
            return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
        } catch(Exception e) {
            response.setError(1, "Error", e.getMessage());
            response.setEstado(Estado.ERROR);
            return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}