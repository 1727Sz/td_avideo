package td.h;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Order(1)
@Component
public class SessionValidFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();

        log.error(path);
        boolean b = !path.contains("/admin") || ( !path.endsWith(".html") && path.contains("."));
        if (b) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }


        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        if (!path.contains("login")) {
            if (Objects.isNull(session) || Objects.isNull(session.getAttribute("login"))) {
                response.sendRedirect(request.getContextPath() + "/admin/login.html");//如果session为空表示用户没有登录就重定向到login.jsp页面
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
