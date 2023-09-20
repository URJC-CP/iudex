package es.urjc.etsii.grafo.iudex.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class TestConfigSecurity {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = new User("user", "", List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        ));

        UserDetails judgeDetails = new User("judge", "", List.of(
                new SimpleGrantedAuthority("ROLE_JUDGE")
        ));

        UserDetails adminDetails = new User("admin", "", List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        ));

        return new InMemoryUserDetailsManager(List.of(
                userDetails, judgeDetails, adminDetails
        ));
    }
}