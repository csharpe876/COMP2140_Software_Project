package com.volunteermanagement.filter;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Character encoding filter ensuring UTF-8 for all requests and responses.
 * Enhanced with Java 21 patterns and proper initialization.
 */
public class CharacterEncodingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CharacterEncodingFilter.class);
    private static final String DEFAULT_ENCODING = "UTF-8";
    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) {
        encoding = filterConfig.getInitParameter("encoding");
        if (encoding == null || encoding.isBlank()) {
            encoding = DEFAULT_ENCODING;
        }
        logger.info("CharacterEncodingFilter initialized with encoding: {}", encoding);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Set request encoding if not already set
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
        }
        
        // Set response encoding (but not content type - let servlets decide)
        response.setCharacterEncoding(encoding);
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.debug("CharacterEncodingFilter destroyed");
    }
}
