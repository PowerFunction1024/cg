package com.changgou.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class FeignInterceptor implements RequestInterceptor{
    @Override
    public void apply(RequestTemplate requestTemplate) {

        try {
            //    获取到request
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (requestAttributes!=null){
                HttpServletRequest request = requestAttributes.getRequest();
                //    获取到所有的头key
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames!=null){
                    while (headerNames.hasMoreElements()){
                        String name = headerNames.nextElement();
                        //    获取所有的头的值
                        String value = request.getHeader(name);
                        requestTemplate.header(name,value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
