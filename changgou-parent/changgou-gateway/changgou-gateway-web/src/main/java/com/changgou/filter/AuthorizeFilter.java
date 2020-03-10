package com.changgou.filter;

import com.changgou.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class AuthorizeFilter implements GlobalFilter , Ordered {

    //令牌头名字
    private static final String AUTHORIZE_TOKEN = "Authorization";
    //用户登录地址
    private static final String USER_LOGIN_URL = "http://localhost:9001/oauth/login";

    /***
     * 全局过滤器
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取Request、Response对象
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        //获取请求的URI
        String path = request.getURI().getPath();

        //如果是登录、goods等开放的微服务[这里的goods部分开放],则直接放行,这里不做完整演示，完整演示需要设计一套权限系统
/*

        if (path.equalsIgnoreCase("/api/user/login")||path.startsWith("/api/user/add")){
            //放行
            return chain.filter(exchange);
        }
*/

        if (URLFilter.hasAuthorize(path)){
            //放行
            return chain.filter(exchange);
        }


        //获取头文件中的令牌信息
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        //如果头文件中没有，则从请求参数中获取
        if (StringUtils.isEmpty(token)){
             token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        //从cookie中获取数据
        HttpCookie authorization = request.getCookies().getFirst(AUTHORIZE_TOKEN);
        if (authorization!=null){
             token = authorization.getValue();

        }
        //如果为空，则输出错误代码
        if (StringUtils.isEmpty(token)){
            //设置方法不允许被访问，405错误代码
/*
            response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            return response.setComplete();
            */

            //跳转到登录页面
            return needAuthorization(USER_LOGIN_URL+"?From="+request.getURI(),exchange);
        }
        //解析令牌数据
        try {
            //Claims claims = JwtUtil.parseJWT(token);
        //    将令牌数据添加到头文件中
        //    request.mutate().header(AUTHORIZE_TOKEN, claims.toString());
            request.mutate().header(AUTHORIZE_TOKEN, "Bearer "+token);
        } catch (Exception e) {
            //解析失败，响应401错误
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //放行
        return chain.filter(exchange);
    }

    /**
     * 响应设置
     * @param userLoginUrl
     * @param exchange
     * @return
     */

    private Mono<Void> needAuthorization(String userLoginUrl, ServerWebExchange exchange) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set("Location",userLoginUrl );
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
