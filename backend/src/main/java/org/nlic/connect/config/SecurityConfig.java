
package org.nlic.connect.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configures authentication, authorization, sessions,
 * CSRF protection, and CORS for NLIC Connect.
 *
 * @author James Pinto
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines access control and security rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        // Allow communication with the React frontend.
        http.cors(cors ->
                cors.configurationSource(corsConfigurationSource())
        );

        // Protect state-changing requests against CSRF.
        http.csrf(csrf ->
                csrf.csrfTokenRepository(
                        new HttpSessionCsrfTokenRepository()
                )
        );

        // Allow Spring Security to use HTTP sessions.
        http.sessionManagement(session ->
                session.sessionCreationPolicy(
                        SessionCreationPolicy.IF_REQUIRED
                )
        );

        // Configure public and protected endpoints.
        http.authorizeHttpRequests(authorize -> authorize

                // Public authentication endpoints.
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/auth/register",
                        "/api/auth/login"
                ).permitAll()

                // CSRF token retrieval for the frontend.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/auth/csrf"
                ).permitAll()

                // Public health endpoint.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/health"
                ).permitAll()

                // Public event viewing.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/events",
                        "/api/events/**"
                ).permitAll()

                // Event creation requires leadership access.
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/events",
                        "/api/events/**"
                ).hasAnyRole("ADMIN", "LEADER")

                // Authenticated members can view public
                // prayer requests on the Community Prayer Wall.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/prayer-requests/public"
                ).authenticated()

                // All other prayer request retrieval
                // remains restricted to church leadership.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/prayer-requests",
                        "/api/prayer-requests/**"
                ).hasAnyRole("ADMIN", "LEADER")

                // Members can submit prayer requests.
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/prayer-requests"
                ).authenticated()

                // Authenticated members can view
                // their own volunteer signup history.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/volunteer/signups/me"
                ).authenticated()

                // Authenticated members can register
                // themselves for volunteer opportunities.
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/volunteer/signups"
                ).authenticated()

                // Access to another member's signup
                // history is restricted to administrators.
                .requestMatchers(
                        "/api/volunteer/signups/**"
                ).hasRole("ADMIN")

                // Authenticated members can view
                // their own New Life Path progress.
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/new-life-path/progress/me"
                ).authenticated()

                // Authenticated members can complete
                // steps in their own discipleship journey.
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/new-life-path/progress/me/*/complete"
                ).authenticated()

                // Access to another member's progress
                // remains restricted to administrators.
                .requestMatchers(
                        "/api/new-life-path/progress/**"
                ).hasRole("ADMIN")

                // All remaining requests require login.
                .anyRequest().authenticated()
        );

        // Disable HTTP Basic authentication.
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Disable Spring's default HTML login form.
        http.formLogin(AbstractHttpConfigurer::disable);

        // Configure session logout.
        http.logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                response.setStatus(
                                        HttpStatus.NO_CONTENT.value()
                                )
                )
        );

        return http.build();
    }

    /**
     * Allows the React development server to communicate
     * with Spring Boot using session cookies.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Content-Type",
                        "X-CSRF-TOKEN"
                )
        );

        // Allow browser session cookies.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/api/**",
                configuration
        );

        return source;
    }
}