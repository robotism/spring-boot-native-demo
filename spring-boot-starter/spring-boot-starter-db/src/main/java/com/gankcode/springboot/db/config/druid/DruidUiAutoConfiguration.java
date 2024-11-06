package com.gankcode.springboot.db.config.druid;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import jakarta.servlet.*;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class DruidUiAutoConfiguration {

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        final ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>();
        bean.setServlet(new StatViewServlet());
        bean.addUrlMappings("/druid/*");
        return bean;
    }

    @Bean
    public StatFilter statFilter() {
        final StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(300);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    @Bean
    public WallFilter wallFilter() {
        final WallFilter wallFilter = new WallFilter();
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        final FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("exclusions",
                "*.js,*.gif,*.jpg,*.png,*.css,*.ico,"
                        + "/druid*,"
                        + "/actuator*,"
                        + "/webjars*,"
                        + "/debug*,"
                        + "/swagger*,/v2/api-docs,");
        bean.addInitParameter("profileEnable", "true");
        return bean;
    }


    @Bean
    public FilterRegistrationBean<Filter> removeDruidAdFilter() throws IOException {
        // 获取common.js内容
        String text = Utils.readFromResource("support/http/resources/js/common.js");
        // 屏蔽 this.buildFooter(); 直接替换为空字符串,让js没机会调用
        final String newJs = text.replace("this.buildFooter();", "");
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        // 注册common.js文件的过滤器
        registration.addUrlPatterns("/druid/js/common.js");
        // 添加一个匿名的过滤器对象,并把改造过的common.js文件内容写入到浏览器
        registration.setFilter(new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                // 重置缓冲区, 响应头不会被重置
                response.resetBuffer();
                // 把改造过的common.js文件内容写入到浏览器
                response.getWriter().write(newJs);
            }

            @Override
            public void destroy() {

            }
        });
        return registration;
    }

}
