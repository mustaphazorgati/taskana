package pro.taskana.example.boot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.ldap.LdapPasswordComparisonAuthenticationManagerFactory;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import pro.taskana.common.rest.SpringSecurityToJaasFilter;

/** Default basic configuration for taskana web example. */
@Configuration
public class BootWebSecurityConfigurer {

  private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  private final String ldapServerUrl;
  private final String ldapBaseDn;
  private final String ldapUserDnPatterns;

  private final boolean devMode;
  private final boolean enableCsrf;

  public BootWebSecurityConfigurer(
      @Value("${taskana.ldap.serverUrl:ldap://localhost:10389}") String ldapServerUrl,
      @Value("${taskana.ldap.baseDn:OU=Test,O=TASKANA}") String ldapBaseDn,
      @Value("${taskana.ldap.userDnPatterns:uid={0},cn=users}") String ldapUserDnPatterns,
      @Value("${enableCsrf:false}") boolean enableCsrf,
      GrantedAuthoritiesMapper grantedAuthoritiesMapper,
      @Value("${devMode:false}") boolean devMode) {
    this.enableCsrf = enableCsrf;
    this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    this.ldapServerUrl = ldapServerUrl;
    this.ldapBaseDn = ldapBaseDn;
    this.ldapUserDnPatterns = ldapUserDnPatterns;
    this.devMode = devMode;
  }

  @Bean
  public DefaultSpringSecurityContextSource defaultSpringSecurityContextSource() {
    return new DefaultSpringSecurityContextSource(ldapServerUrl + "/" + ldapBaseDn);
  }

  @Bean
  AuthenticationManager ldapAuthenticationManager(
      BaseLdapPathContextSource contextSource, LdapAuthoritiesPopulator authorities) {
    @SuppressWarnings("deprecation")
    LdapPasswordComparisonAuthenticationManagerFactory factory =
        new LdapPasswordComparisonAuthenticationManagerFactory(
            contextSource, NoOpPasswordEncoder.getInstance());
    factory.setUserDnPatterns(ldapUserDnPatterns);
    factory.setLdapAuthoritiesPopulator(authorities);
    factory.setAuthoritiesMapper(grantedAuthoritiesMapper);
    factory.setPasswordAttribute("userPassword");
    return factory.createAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    HttpSecurity httpSecurity =
        http.authorizeRequests()
            .antMatchers("/css/**", "/img/**")
            .permitAll()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/docs/**")
            .permitAll()
            .and()
            .addFilter(jaasApiIntegrationFilter())
            .addFilterAfter(new SpringSecurityToJaasFilter(), JaasApiIntegrationFilter.class);

    if (enableCsrf) {
      CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
      csrfTokenRepository.setCookiePath("/");
      httpSecurity.csrf().csrfTokenRepository(csrfTokenRepository);
    } else {
      httpSecurity.csrf().disable().httpBasic();
    }

    if (devMode) {
      http.headers()
          .frameOptions()
          .sameOrigin()
          .and()
          .authorizeRequests()
          .antMatchers("/h2-console/**")
          .permitAll();
    } else {
      http.authorizeRequests()
          .anyRequest()
          .fullyAuthenticated()
          .and()
          .formLogin()
          .loginPage("/login")
          .failureUrl("/login?error")
          .defaultSuccessUrl("/")
          .permitAll()
          .and()
          .logout()
          .invalidateHttpSession(true)
          .clearAuthentication(true)
          .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
          .logoutSuccessUrl("/login?logout")
          .deleteCookies("JSESSIONID")
          .permitAll();
    }
    return http.build();
  }

  protected JaasApiIntegrationFilter jaasApiIntegrationFilter() {
    JaasApiIntegrationFilter filter = new JaasApiIntegrationFilter();
    filter.setCreateEmptySubject(true);
    return filter;
  }
}
