package com.algorithmicintegration.lyy.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController


public class featureArea{

    private double featureArea;

    // 创建 Cookie
    private Cookie cookie = new Cookie("my_cookie", "area_cookie");


    @PostMapping(value = "/processes/area/jobs")
    public ResponseEntity<String> FeatureArea(@RequestBody JSONObject geoJson, HttpServletResponse response) {
        // 获取坐标数组
        JSONArray coordinates = geoJson.getJSONArray("coordinates").getJSONArray(0);

        // 将坐标数组转换为用于计算的数据结构
        int numVertices = coordinates.size();
        double[] x = new double[numVertices];
        double[] y = new double[numVertices];
        for (int i = 0; i < numVertices; i++) {
            JSONArray vertex = coordinates.getJSONArray(i);
            x[i] = vertex.getDoubleValue(0);
            y[i] = vertex.getDoubleValue(1);
        }

        // 计算多边形的面积
        double totalArea = 0.0;
        for (int i = 0; i < numVertices; ++i) {
            int j = (i + 1) % numVertices;
            totalArea += x[i] * y[j] - x[j] * y[i];
        }
        totalArea /= 2.0;
        featureArea =totalArea;
        String jobId = UUID.randomUUID().toString();


        cookie.setMaxAge(3600); // 设置过期时间为一小时
        // 将 Cookie 添加到响应中
        response.addCookie(cookie);



        // 返回结果
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString()); // 将 Cookie 添加到响应头部
        return new ResponseEntity<>(jobId, headers, HttpStatus.OK);
    }


    @GetMapping(value = "/processes/area/jobs/{jobId}/results")
    public ResponseEntity<String> FeatureAreaResult(@PathVariable("jobId") String jobId, HttpServletResponse response) {


        // 将 Cookie 添加到响应中
        cookie.setMaxAge(3600); // 设置过期时间为一小时
        response.addCookie(cookie);
        JSONObject resultArray = new JSONObject();
        resultArray.put("area",Double.toString(Math.abs(featureArea)));

        // 返回结果
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString()); // 将 Cookie 添加到响应头部
        return new ResponseEntity<>(resultArray.toString(), headers, HttpStatus.OK);
    }






}