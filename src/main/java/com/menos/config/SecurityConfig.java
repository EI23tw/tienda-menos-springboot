package com.menos.config;

import com.menos.model.Usuario;
import com.menos.repo.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(UsuarioRepository r) {
        // Implementación tradicional con clase anónima y condicionales clásicos
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                Optional<Usuario> opt = r.findByEmail(email);
                if (opt.isPresent()) {
                    Usuario u = opt.get();
                    return User.withUsername(u.getEmail())
                            .password(u.getPassword())
                            .roles(u.getRol())
                            .build();
                } else {
                    throw new UsernameNotFoundException(email);
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/pago/**", "/mis-compras").authenticated()
                .anyRequest().permitAll()
        );

        http.formLogin(f -> f
                .loginPage("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException, ServletException {
                        boolean esAdmin = false;
                        
                        // Bucle for tradicional en lugar de auth.getAuthorities().stream().anyMatch()
                        for (GrantedAuthority autoridad : auth.getAuthorities()) {
                            if (autoridad.getAuthority().equals("ROLE_ADMIN")) {
                                esAdmin = true;
                                break;
                            }
                        }

                        if (esAdmin) {
                            res.sendRedirect("/admin");
                        } else {
                            SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
                            handler.onAuthenticationSuccess(req, res, auth);
                        }
                    }
                })
        );

        http.logout(l -> l.logoutSuccessUrl("/"));

        return http.build();
    }
}