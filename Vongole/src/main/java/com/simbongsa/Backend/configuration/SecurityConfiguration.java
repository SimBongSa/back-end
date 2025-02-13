package com.simbongsa.Backend.configuration;


import com.simbongsa.Backend.exception.jwt.AccessDeniedHandlerException;
import com.simbongsa.Backend.exception.jwt.AuthenticationEntryPointException;
import com.simbongsa.Backend.exception.jwt.JwtExceptionFilter;
import com.simbongsa.Backend.exception.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;
    private final JwtExceptionFilter jwtExceptionFilter;
    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();

        // xss
        http
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'");

        http.csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/members/**").permitAll()
                .antMatchers("/boards").permitAll()
                .antMatchers("/boards/**").permitAll() // boards 이후의 모든 엔드포인트에 대해 허용
//                .antMatchers("/comments/*").permitAll()
                .antMatchers("/user/kakao/callback").permitAll()
                .antMatchers("/companypage/**").permitAll()
                .antMatchers("/mypage/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/app.js").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/webjars").permitAll()
                .antMatchers("/main.css").permitAll()
                .antMatchers("/topic/**").permitAll()
                .antMatchers("/gs-guide-websocket/**").permitAll()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfiguration(tokenProvider, jwtExceptionFilter));

        return http.build();
    }
}
