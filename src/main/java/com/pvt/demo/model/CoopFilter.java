package com.pvt.demo.model;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CoopFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException {
        HttpServletResponse httpResp = (HttpServletResponse) response;
        httpResp.setHeader("Cross-Origin-Opener-Policy", "same-origin-allow-popups");
        httpResp.setHeader("Cross-Origin-Embedder-Policy", "unsafe-none");
        chain.doFilter(request, response);
    }
}
