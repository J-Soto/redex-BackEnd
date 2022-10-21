package pucp.dp1.redex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pucp.dp1.redex.model.utils.SummaryCase;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public  class RedexApplication {
	public static void main(String[] args) {
		SpringApplication.run(RedexApplication.class, args);
	}
}