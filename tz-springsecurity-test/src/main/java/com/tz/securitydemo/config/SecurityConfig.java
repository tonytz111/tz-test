package com.tz.securitydemo.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tz.securitydemo.bean.RespBean;
import com.tz.securitydemo.entity.Users;
import com.tz.securitydemo.jwtsecurity.JwtAuthorizationTokenFilter;
import com.tz.securitydemo.service.UserService;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author tianzheng
 * @date 2020/2/20 12:57
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Autowired
    UserService jwtUserDetailsService;

    @Autowired
    JwtAuthorizationTokenFilter authenticationTokenFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//
//        //swagger附带的界面资源需要被忽略拦截
//        web.ignoring().antMatchers("/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
//                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                                        HttpServletResponse httpServletResponse,
                                                        Authentication authentication) throws IOException, ServletException {

                        httpServletResponse.setContentType("application/json;charset=utf-8");
                        Users u = (Users) authentication.getPrincipal();
                        Map<String,String> tokenResp =new HashMap<>();
                        tokenResp.put("token",u.getToken());
                        RespBean respBean = RespBean.ok("登录成功",tokenResp);
                        httpServletResponse.setStatus(200);
                        ObjectMapper om = new ObjectMapper();
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write(om.writeValueAsString(respBean));
                        out.flush();
                        out.close();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req,
                                                        HttpServletResponse resp,
                                                        AuthenticationException e) throws IOException {
                        resp.setContentType("application/json;charset=utf-8");
                        RespBean respBean = null;
                        if (e instanceof BadCredentialsException ||
                                e instanceof UsernameNotFoundException) {
                            respBean = RespBean.error("账户名或者密码输入错误!");
                        } else if (e instanceof LockedException) {
                            respBean = RespBean.error("账户被锁定，请联系管理员!");
                        } else if (e instanceof CredentialsExpiredException) {
                            respBean = RespBean.error("密码过期，请联系管理员!");
                        } else if (e instanceof AccountExpiredException) {
                            respBean = RespBean.error("账户过期，请联系管理员!");
                        } else if (e instanceof DisabledException) {
                            respBean = RespBean.error("账户被禁用，请联系管理员!");
                        } else {
                            respBean = RespBean.error("登录失败!");
                        }
                        resp.setStatus(401);
                        ObjectMapper om = new ObjectMapper();
                        PrintWriter out = resp.getWriter();
                        out.write(om.writeValueAsString(respBean));
                        out.flush();
                        out.close();
                    }
                })
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {

                    @Override
                    public void commence(HttpServletRequest httpServletRequest,
                                         HttpServletResponse resp,
                                         AuthenticationException e) throws IOException, ServletException {

                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        RespBean respBean = RespBean.error("访问失败!");
                        if (e instanceof InsufficientAuthenticationException) {
                            respBean.setMsg("请求失败，请联系管理员!");
                        }
                        out.write(new ObjectMapper().writeValueAsString(respBean));
                        out.flush();
                        out.close();
                    }
                })
                .accessDeniedHandler(
                        new AccessDeniedHandler(){

                            @Override
                            public void handle(HttpServletRequest httpServletRequest,
                                               HttpServletResponse resp,
                                               AccessDeniedException e) throws IOException, ServletException {
                                resp.setContentType("application/json;charset=utf-8");
                                PrintWriter out = resp.getWriter();
                                RespBean respBean = RespBean.error("访问失败!");
                                if (e instanceof AccessDeniedException) {
                                    respBean.setMsg("权限不足请联系管理员授权！");
                                    respBean.setStatus(403);
                                }
                                out.write(new ObjectMapper().writeValueAsString(respBean));
                                out.flush();
                                out.close();

                            }
                        })
                .and()
                .logout().logoutSuccessHandler(new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {

                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter out = httpServletResponse.getWriter();
                RespBean respBean = RespBean.ok("用户登出成功");

                out.write(new ObjectMapper().writeValueAsString(respBean));
                out.flush();
                out.close();
            }
        })
                .permitAll();

        http.csrf()
                //关闭csrf
                .disable()
                .sessionManagement()
                //定制自己的 session 策略：调整为让 Spring Security 不创建和使用 session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(
                        new AuthenticationEntryPoint() {

                            @Override
                            public void commence(HttpServletRequest req,
                                                 HttpServletResponse resp,
                                                 AuthenticationException e) throws IOException, ServletException {
                                resp.setContentType("application/json;charset=utf-8");
                                PrintWriter out = resp.getWriter();
                                RespBean respBean = RespBean.error("访问失败!");
                                if (e instanceof InsufficientAuthenticationException) {
                                    respBean.setMsg("用户尚未登录或者Token失效请重新登录");
                                }
                                out.write(new ObjectMapper().writeValueAsString(respBean));
                                out.flush();
                                out.close();
                            }
                        }
                )
        ;
    }


}
