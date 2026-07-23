package education.scheme.welfare;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "umis.api")
public class UmisConfig {

    private String url;
    private String authToken;
}