package pucp.dp1.redex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Component
public class Runner implements ApplicationRunner {@Autowired
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Executed runner");

    }
}