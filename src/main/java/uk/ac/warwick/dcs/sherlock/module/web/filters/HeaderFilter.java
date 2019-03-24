package uk.ac.warwick.dcs.sherlock.module.web.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filters all requests to add the current URL to the http response headers
 */
@WebFilter("/*")
public class HeaderFilter implements Filter {

    /**
     * Adds the URI of the request as a new "sherlock-url" header variable
     * in the http response.
     *
     * This is used by the JavaScript requests to detect if the response of
     * a form was a redirect (e.g. if you add a workspace, the response is
     * not a page, but a redirect to the new workspace page) so the JavaScript
     * knows to redirect the user.
     *
     * @param request
     * @param response
     * @param chain
     *
     * @throws IOException
     * @throws ServletException
     *
     */
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        //TODO: Add the "msg" parameter to the URI if set
        httpServletResponse.setHeader("sherlock-url", ((HttpServletRequest)request).getRequestURI());

        chain.doFilter(request, response);
    }

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException { }

//    @Override
//    public void destroy() { }
}