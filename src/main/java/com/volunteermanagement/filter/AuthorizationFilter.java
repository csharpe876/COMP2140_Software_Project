package com.volunteermanagement.filter;

import jakarta.servlet.*;
import java.io.IOException;

public class AuthorizationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Placeholder: allow all for now
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
