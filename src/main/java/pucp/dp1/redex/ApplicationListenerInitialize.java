package pucp.dp1.redex;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationListenerInitialize implements ApplicationListener<ApplicationReadyEvent> {

    @EventListener(ApplicationReadyEvent.class)
    public void onReadyEvent() {
        log.info("onReadyEvent: The application is ready");
    }

    public void onApplicationEvent( ApplicationReadyEvent event) {
        log.info("onApplicationEvent: Beans are instantiated");
    }

}
