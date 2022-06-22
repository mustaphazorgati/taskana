package pro.taskana.example.wildfly.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;

/**
 * Default basic configuration for taskana web example running on Wildfly / JBoss with Elytron or
 * JAAS Security.
 */
@EnableWebSecurity
public class WildflyWebSecurityConfigurer {
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    HttpSecurity security =
        http.authorizeHttpRequests(
            (authz) -> {
              try {
                http.addFilter(jaasApiIntegrationFilter())
                    .addFilterAfter(new ElytronToJaasFilter(), JaasApiIntegrationFilter.class)
                    .csrf()
                    .disable();
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
    return security.build();
  }

  protected JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }
}
