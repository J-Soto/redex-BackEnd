package pucp.dp1.redex;
import antlr.ASTNULLType;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.core.io.ClassPathResource;
import pucp.dp1.redex.dao.sales.IHistorico;
import pucp.dp1.redex.model.sales.Historico;
import pucp.dp1.redex.services.dao.sales.IHistoricoService;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public  class RedexApplication {


	/*
        List<Historico> tutorials = new ArrayList<>();
        String fecha = "\"2022-08-01\"";
        String datereq = fecha.substring(1, 11).replace("-", "");
        System.out.println(datereq);
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
        Date dateDate;
        dateDate = formatterDate.parse(datereq);
        tutorials =  IHistorico.findByFecha(dateDate);
	 */
	public static void main(String[] args)  {
		SpringApplication.run(RedexApplication.class, args);

	}
	@PostConstruct
	public void init() throws ParseException {
		log.info("Post Construct: Executes after beans are instantiated");
	}
}