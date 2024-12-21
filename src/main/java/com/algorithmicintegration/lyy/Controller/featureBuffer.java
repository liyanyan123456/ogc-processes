package com.algorithmicintegration.lyy.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.locationtech.jts.geom.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Scanner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController


public class featureBuffer {


    // 创建 Cookie
    private Cookie cookie = new Cookie("my_cookie", "buffer_cookie");
    private String featureBuffer;


    @PostMapping(value = "/processes/buffer/jobs")
    private ResponseEntity<String> FeatureBuffer(@RequestBody JSONObject request, HttpServletResponse response) {
        JSONObject geoJson = request.getJSONObject("geoJson");
        double distance = request.getDouble("distance");
        // 获取坐标数组
        JSONArray coordinates = geoJson.getJSONArray("coordinates");

// 提取点的坐标
        double x = coordinates.getBigDecimal(0).doubleValue();
        double y = coordinates.getBigDecimal(1).doubleValue();


        // 创建Point对象表示输入的点
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(x, y));

        // 计算缓冲区
        Geometry buffer = point.buffer(distance);

        // 将缓冲区转换为GeoJSON对象
        JSONObject bufferGeoJson = geometryToGeoJson(buffer);
        featureBuffer=bufferGeoJson.toString();
        String jobId = UUID.randomUUID().toString();


        cookie.setMaxAge(3600); // 设置过期时间为一小时
        // 将 Cookie 添加到响应中
        response.addCookie(cookie);



        // 返回结果
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString()); // 将 Cookie 添加到响应头部
        return new ResponseEntity<>(jobId, headers, HttpStatus.OK);

    }
    @GetMapping(value = "/processes/buffer/jobs/{jobId}/results")
    public ResponseEntity<String> FeaturebufferResult(@PathVariable("jobId") String jobId, HttpServletResponse response) {


        // 将 Cookie 添加到响应中
        cookie.setMaxAge(3600); // 设置过期时间为一小时
        response.addCookie(cookie);
        JSONObject resultArray = new JSONObject();
        resultArray.put("buffer",featureBuffer);

        // 返回结果
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString()); // 将 Cookie 添加到响应头部
        return new ResponseEntity<>(resultArray.toString(), headers, HttpStatus.OK);
    }

    // 将Geometry对象转换为GeoJSON对象
    private JSONObject geometryToGeoJson(Geometry geometry) {
        JSONObject geoJson = new JSONObject();
        JSONArray coordinates = new JSONArray();

        if (geometry instanceof Polygon) {
            Coordinate[] polygonCoordinates = geometry.getCoordinates();
            JSONArray polygonCoords = new JSONArray();
            for (Coordinate coord : polygonCoordinates) {
                JSONArray coordArray = new JSONArray();
                coordArray.add(coord.x);
                coordArray.add(coord.y);
                polygonCoords.add(coordArray);
            }
            coordinates.add(polygonCoords);
        }

        geoJson.put("type", "Polygon");
        geoJson.put("coordinates", coordinates);

        return geoJson;
    }
//
//    public static void main(String[] args) {
////        // GeoJSON 格式的点坐标字符串
////        String pointGeoJSON = "{"type":"Point","coordinates":[1.0, 1.0]}";
//        // 创建一个 Scanner 对象以接收用户输入
//        Scanner scanner = new Scanner(System.in);
//
//        // 提示用户输入 GeoJSON 字符串
//        System.out.print("请输入 GeoJSON 字符串：");
//        String pointGeoJSON = scanner.nextLine();
//        // 提示用户输入 GeoJSON 字符串
//        System.out.print("请输入缓冲区距离：");
//        Double distance = Double.valueOf(scanner.nextLine());
//
//
//        // 解析 GeoJSON 字符串并计算缓冲区
//        JSONObject geoJson = JSONObject.parseObject(pointGeoJSON);
//        JSONObject bufferGeoJson = calculateBuffer(geoJson, distance);
//
//        // 输出缓冲区的 GeoJSON 字符串
//        System.out.println("Buffer GeoJSON: " + bufferGeoJson.toJSONString());
//
//    }
}
