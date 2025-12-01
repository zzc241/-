package ruiji.ruiji.filter;

import java.io.IOException;
import org.springframework.util.AntPathMatcher;

import com.alibaba.fastjson2.JSON;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ruiji.ruiji.common.BaseContext;
import ruiji.ruiji.common.R;

@Slf4j

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static final String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg",
            "/user/login"
    };
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException { 
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截请求：{}", request.getRequestURI());
        //1.获取本次请求的URI
        String requestURL = request.getRequestURI();
        //2.判断本次请求是否需要处理
        if (check(urls , requestURL)){
            //3.如果不需要处理，则直接放行
            // log.info("本次请求{}不需要处理", requestURL);
            filterChain.doFilter(request, response);
            return;
        }
        
        //4.判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            // log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            //设置当前线程的id
            Long empId = (long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }


        if(request.getSession().getAttribute("user") != null){
            // log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            //设置当前线程的id
            Long userId = (long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //5.如果未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    public boolean check(String[] urls, String requestURL){
        for (String url : urls) {
            boolean match = pathMatcher.match(url, requestURL);
            if (match) {
                return true;
            }
        }
        return false;
    }
    
}
