package org.chengbing.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.chengbing.util.JwtUtils;
import org.chengbing.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author yuyongli
 * @Date 2021/5/31
 */
@Component
public class LoginFilter implements GlobalFilter, Ordered {
    @Autowired
    private ObjectMapper objectMapper;
    private static final String TOKEN="Authorization";
    //获取需要忽略的路径
    @Value("${harold.skip.urls}")
    private String skipAuthUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取来访者的ip
        // 浏览器的信息

        //token
        Result result=new Result();
        String url = exchange.getRequest().getURI().getPath();//当前的路径
        String[] split = skipAuthUrls.split(",");
        if(null != split&& Arrays.asList(split).contains(url)){
            return chain.filter(exchange);
        }else {
            ServerHttpResponse resp = exchange.getResponse();
            resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            String token = exchange.getRequest().getHeaders().getFirst(TOKEN);
            if (StringUtils.isEmpty(token)) {
                //
                //没有登录
                result = new Result(401, "没有登录", "没有登录");
            } else {
                try {
                    //验证
                    JwtUtils.checkToken(token);
                    //result = new Result(200, "认证成功", "认证成功");
                } catch (ExpiredJwtException e) {
                    System.out.println("token过期！！！！");
                    result = new Result(401, "token过期", "token过期");
                } catch (Exception e) {
                    System.out.println("认证失败！！！！");
                    result = new Result(401, "认证失败", "认证失败");
                }

            }
            String returnStr = "";
            try {
                returnStr = objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            DataBuffer buffer = resp.bufferFactory().wrap(returnStr.getBytes(StandardCharsets.UTF_8));
            return resp.writeWith(Flux.just(buffer));
        }

    }

    //优先级 值越小优先级越高
    @Override
    public int getOrder() {
        return 1;
    }

}