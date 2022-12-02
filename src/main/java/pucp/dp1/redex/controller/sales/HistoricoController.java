package pucp.dp1.redex.controller.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pucp.dp1.redex.services.dao.sales.IHistoricoService;

@CrossOrigin
@RestController
@RequestMapping("/api/order")
public class HistoricoController {
    @Autowired
    private IHistoricoService service;

}