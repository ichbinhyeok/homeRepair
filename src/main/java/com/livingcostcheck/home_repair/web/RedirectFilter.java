package com.livingcostcheck.home_repair.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // 1. .html.html 패턴 영구 리다이렉션 (301)
        if (uri != null && uri.contains(".html.html")) {
            String newUri = uri.replaceAll("(\\.html)+$", ".html");

            // 쿼리 파라미터 보존
            if (req.getQueryString() != null) {
                newUri += "?" + req.getQueryString();
            }

            res.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            res.setHeader("Location", newUri);
            return; // 필터 체인 중단
        }

        // 2. 다른 필터나 서블릿으로 넘김
        chain.doFilter(request, response);
    }
}
