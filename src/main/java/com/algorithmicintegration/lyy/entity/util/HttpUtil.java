package com.algorithmicintegration.lyy.entity.util;

import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class HttpUtil {
    public String formHttp(String uri, MultiValueMap<String, String> params, HttpMethod method) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, method, entity, String.class);
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        return response.getBody();
    }

    public ResponseEntity<String> formHttp(String uri, MultiValueMap<String, String> params, HttpMethod method, String cookies) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if(cookies != null){
            headers.set("Cookie", cookies);
        }
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        return  restTemplate.exchange(uri, method, entity, String.class);
    }

    public String jsonHttp(String uri, String requestBody, HttpMethod method){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, method, entity, String.class);;
        return response.getBody();
    }

    public ResponseEntity<String> jsonHttp(String uri, String requestBody, HttpMethod method, String cookies){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(cookies != null){
            headers.set("Cookie", cookies);
        }
        //将请求头部和参数合成一个请求
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, method, entity, String.class);
        return response;
    }

    public String postXML(String url, String xmlString){
//        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><AvailReq><hotelid>123</hotelid></AvailReq>";

        RestTemplate restTemplate =  new RestTemplate();
        //Create a list for the message converters
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        //Add the String Message converter
        messageConverters.add(new StringHttpMessageConverter());
        //Add the message converters to the restTemplate
        restTemplate.setMessageConverters(messageConverters);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody() ;
    }
}
