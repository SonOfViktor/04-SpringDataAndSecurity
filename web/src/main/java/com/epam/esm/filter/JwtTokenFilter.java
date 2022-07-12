package com.epam.esm.filter;

import com.epam.esm.security.JpaUserDetailService;
import com.epam.esm.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final JpaUserDetailService jpaUserDetailService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest)
                .flatMap(jpaUserDetailService::loadUserByJwtToken)
                .map(userDetails ->
                        new PreAuthenticatedAuthenticationToken(userDetails, "", userDetails.getAuthorities()))
                .ifPresent(authentication -> SecurityContextHolder.getContext()
                        .setAuthentication(authentication));

        filterChain.doFilter(servletRequest, servletResponse);
    }

}

