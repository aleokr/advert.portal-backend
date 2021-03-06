package com.app.advert.portal.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    private final SecurityUtils securityUtils;

    public AuthorizationFilter(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/auth") || request.getServletPath().equals("/api/v1/auth/refreshToken")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(securityUtils.getSecretKey().getBytes());
                if(token.equals("null")) {
                    filterChain.doFilter(request, response);
                    return;
                }
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);

                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role ->
                        authorities.add(new SimpleGrantedAuthority(role)));

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(new UserPrincipal(decodedJWT.getSubject(), null, decodedJWT.getClaim("id").asLong(), decodedJWT.getClaim("companyId").asLong(), authorities), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                log.error("Error logging {}", exception.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                Map<String, String> errors = new HashMap<>();
                errors.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), errors);

            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
