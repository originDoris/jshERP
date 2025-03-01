package com.jsh.erp.filter;

import com.jsh.erp.service.redis.RedisService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(filterName = "LogCostFilter", urlPatterns = {"/*"},
        initParams = {@WebInitParam(name = "ignoredUrl", value = ".ico"),
                      @WebInitParam(name = "filterPath",
                              value = "/jshERP-boot/user/login#/jshERP-boot/user/registerUser#/jshERP-boot/user/randomImage#" +
                                      "/jshERP-boot/platformConfig/getPlatform#/jshERP-boot/v2/api-docs#/jshERP-boot/webjars#" +
                                      "/jshERP-boot/systemConfig/static#/jshERP-boot/shopping/wx/login"),
                      @WebInitParam(name = "wxUrl", value = "/jshERP-boot/shopping/query#")})
public class LogCostFilter implements Filter {

    private static final String FILTER_PATH = "filterPath";
    private static final String IGNORED_PATH = "ignoredUrl";
    private static final String WX_URL = "wxUrl";

    private static final List<String> ignoredList = new ArrayList<>();
    private String[] allowUrls;
    private String[] ignoredUrls;
    private static final List<String> wxUrlList = new ArrayList<>();
    @Resource
    private RedisService redisService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String filterPath = filterConfig.getInitParameter(FILTER_PATH);
        if (!StringUtils.isEmpty(filterPath)) {
            allowUrls = filterPath.contains("#") ? filterPath.split("#") : new String[]{filterPath};
        }

        String wxUrl = filterConfig.getInitParameter(WX_URL);
        if (!StringUtils.isEmpty(wxUrl)) {
            String[] urls = wxUrl.contains("#") ? wxUrl.split("#") : new String[]{wxUrl};
            wxUrlList.addAll(Arrays.asList(urls));
        }


        String ignoredPath = filterConfig.getInitParameter(IGNORED_PATH);
        if (!StringUtils.isEmpty(ignoredPath)) {
            ignoredUrls = ignoredPath.contains("#") ? ignoredPath.split("#") : new String[]{ignoredPath};
            ignoredList.addAll(Arrays.asList(ignoredUrls));
        }
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String requestUrl = servletRequest.getRequestURI();

        if (wxUrlList.contains(requestUrl)) {
            Object openId = redisService.getObjectFromSessionByKey(servletRequest, "openId");
            if (openId != null) {
                chain.doFilter(request, response);
                return;
            }else{
                servletResponse.setStatus(500);
                if(requestUrl != null && !requestUrl.contains("/user/logout") && !requestUrl.contains("/function/findMenuByPNumber")) {
                    servletResponse.getWriter().write("loginOut");
                }
                return;
            }
        }

        //具体，比如：处理若用户未登录，则跳转到登录页
        Object userId = redisService.getObjectFromSessionByKey(servletRequest,"userId");
        if(userId!=null) { //如果已登录，不阻止
            chain.doFilter(request, response);
            return;
        }
        if (requestUrl != null && (requestUrl.contains("/doc.html") ||
            requestUrl.contains("/user/login") || requestUrl.contains("/user/register"))) {
            chain.doFilter(request, response);
            return;
        }
        if (verify(ignoredList, requestUrl)) {
            chain.doFilter(servletRequest, response);
            return;
        }
        if (null != allowUrls && allowUrls.length > 0) {
            for (String url : allowUrls) {
                if (requestUrl.startsWith(url)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        servletResponse.setStatus(500);
        if(requestUrl != null && !requestUrl.contains("/user/logout") && !requestUrl.contains("/function/findMenuByPNumber")) {
            servletResponse.getWriter().write("loginOut");
        }
    }

    private static String regexPrefix = "^.*";
    private static String regexSuffix = ".*$";

    private static boolean verify(List<String> ignoredList, String url) {
        for (String regex : ignoredList) {
            Pattern pattern = Pattern.compile(regexPrefix + regex + regexSuffix);
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void destroy() {

    }
}