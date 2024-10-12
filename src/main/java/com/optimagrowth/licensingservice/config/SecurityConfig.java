package com.optimagrowth.licensingservice.config;

import org.keycloak.adapters.springsecurity.client.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Scope;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.springframework.core.convert.converter.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.http.*;
import org.springframework.security.oauth2.jose.jws.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.*;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Autowired
    public KeycloakClientRequestFactory keycloakClientRequestFactory;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/auth").permitAll()
                                .requestMatchers("/api/all").hasRole("admin")
                                .anyRequest().authenticated()
                ).sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).oauth2ResourceServer(
                        resourceServer -> resourceServer.jwt(
                                jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                        keycloakAuthConverter()
                                )
                        )
                )
                .build();
    }

    private Converter<Jwt,? extends AbstractAuthenticationToken> keycloakAuthConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
                new AuthoritiesConverter()
        );
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(issuerUri).jwsAlgorithm(SignatureAlgorithm.RS512).build();

        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                System.out.println("String token : "+token);
                var t = jwtDecoder.decode(token);
                System.out.println(t.toString());
                return t;
            }
        };
    }


}
