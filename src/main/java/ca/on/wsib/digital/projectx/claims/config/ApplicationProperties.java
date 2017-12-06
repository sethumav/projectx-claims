package ca.on.wsib.digital.projectx.claims.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Claims.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String disableSecurity;

    public String getDisableSecurity() {
        return disableSecurity;
    }

    public void setDisableSecurity(String disableSecurity) {
        this.disableSecurity = disableSecurity;
    }
}
