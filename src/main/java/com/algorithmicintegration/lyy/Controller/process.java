package com.algorithmicintegration.lyy.Controller;

import com.algorithmicintegration.lyy.entity.process.*;
import com.algorithmicintegration.lyy.entity.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "OGC API - Processes ")
@RestController
@RequestMapping("/processes_api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class process {
//    final String localDataRoot = "/home/geocube/tomcat8/apache-tomcat-8.5.57/webapps/data/temp/";
//    final String httpDataRoot = "http://125.220.153.26:8093/data/temp/";
//
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private SSHClientUtil sshClientUtil;


    @Autowired
    private HttpUtil httpUtil;

    private ProcessesDesc processesDesc = new ProcessesDesc();//所有的processList
    private List<ProcessDesc> processDescList = new ArrayList<>();


    /**
     * Landing page of geocube processes API.
     */
    @ApiOperation(value = "Landing page", notes = "The landing page provides links to the:\n" +
            "\n" +
            "· The APIDefinition (no fixed path),\n" +
            "· The Conformance statements (path /conformance),\n" +
            "· The processes metadata (path /processes),\n" +
            "· The endpoint for job monitoring (path /jobs).")
    @GetMapping(value = "/")
    public Map<String, Object> getLandingPage() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "OGE processing server");
        map.put("description", "OGE server implementing the OGC API - Processes 1.0 and 2.0");

        List<Link> linkList = new ArrayList<>();
        Link self = new Link();
        self.setHref("/processes_api/");
        self.setRel("self");
        self.setType("application/json");
        self.setTitle("landing page");
        linkList.add(self);

        Link serviceDesc = new Link();
        serviceDesc.setHref("/processes_api/api");
        serviceDesc.setRel("service-desc");
        serviceDesc.setType("application/openapi+json;version=3.0");
        serviceDesc.setTitle("the API definition");
        linkList.add(serviceDesc);

        Link conformance = new Link();
        conformance.setHref("/processes_api/conformance");
        conformance.setRel("conformance");
        conformance.setType("application/json");
        conformance.setTitle("OGC API - Processes conformance classes implemented by this server");
        linkList.add(conformance);

        Link processes = new Link();
        processes.setHref("processes_api/processes");
        processes.setRel("processes");
        processes.setType("application/json");
        processes.setTitle("Metadata about the processes");
        linkList.add(processes);

        Link jobs = new Link();
        jobs.setHref("processes_api/jobs");
        jobs.setRel("job-list");
        jobs.setType("application/json");
        jobs.setTitle("The endpoint for job monitoring");
        linkList.add(jobs);

        map.put("links", linkList);
        return map;
    }

    /**
     * The OpenAPI definition as JSON.
     */
    @ApiOperation(value = "The OpenAPI definition as JSON", notes = "The API definition is metadata about the API and strictly not part of the API itself, but it MAY be hosted as a sub-resource to the base path of the API,")
    @GetMapping(value = "/api")
    public Map<String, Object> getOpenAPI() throws IOException {
//
        Resource resource = resourceLoader.getResource("classpath:static/openapi.json");
        // 使用ObjectMapper解析JSON文件
        ObjectMapper objectMapper = new ObjectMapper();
        // 读取JSON文件并转换为相应的Java对象
        Map<String, Object> myObject = objectMapper.readValue(resource.getInputStream(), Map.class);
        return myObject;
    }


    /**
     * Information about standards that this API conforms to.
     */
    @ApiOperation(value = "information about standards that this API conforms to", notes = "A list of all conformance classes, specified in a standard, that the server conforms to.")
    @GetMapping(value = "/conformance")
    public Map<String, Object> getConformanceClasses() {
        Map<String, Object> map = new HashMap<>();
        List<String> comformList = new ArrayList<>();
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/json");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/oas30");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/ogc-process-description");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/callback");
        comformList.add("http://www.opengis.net/spec/ogcapi-processes-1/1.0/conf/dismiss");
        map.put("conformsTo", comformList);
        return map;
    }


    /**
     * Lists the processes this API offers.
     */
    @ApiOperation(value = "retrieve the list of available processes", notes = "The list of processes contains a summary of each process the OGC API - Processes offers, including the link to a more detailed description of the process.")
    @GetMapping(value = "/processes")
    public ProcessesDesc getProcesses() throws IOException {
        try {
            // 定义文件夹路径
            String folderPath = "classpath:/processDescription/";


            // 获取资源解析器
            ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

            // 使用资源解析器获取匹配文件夹下的所有文件资源
            Resource[] resources = resourceResolver.getResources(folderPath + "*.json");

            // 创建 ObjectMapper 用于读取 JSON 文件
            ObjectMapper objectMapper = new ObjectMapper();

            // 遍历每个资源
            for (Resource resource : resources) {
                // 获取文件名
                String fileName = resource.getFilename();

                // 使用资源的 InputStream 读取文件内容
                try (InputStream inputStream = resource.getInputStream()) {
                    // 将 JSON 文件内容映射到 ProcessDesc 对象
                    ProcessDesc processDesc = objectMapper.readValue(inputStream, ProcessDesc.class);

                    // 将 ProcessDesc 对象添加到列表中
                    processDescList.add(processDesc);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常堆栈信息
            // 可以根据需要处理异常
        }

        processesDesc.setProcesses(processDescList);
        processesDesc.setLinks(Arrays.asList(new Link("/processes_api/processes", "self",
                "application/json", "the list of process description")));
        return processesDesc;
    }




    /**
     * Retrieve a process description.
     *
     * @param name Process name
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "retrieve a process description", notes = "The process description contains information about inputs and outputs and a link to the execution-endpoint for the process. The Core does not mandate the use of a specific process description to specify the interface of a process. ")
    @GetMapping(value = "/processes/{processId}")
    public String getProcessDescription(@PathVariable("processId") String name) throws IOException {

        Resource resource = new ClassPathResource("processDescription/" + name + "_description.json");
        InputStream is = resource.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(is);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    /**
     * Execute a process.
     *
     * @param processName Process name
     * @param processRequest 请求输入的参数
     * @param session http session
     * @return job 对象
     * @throws IOException
     * @throws InterruptedException
     */
    @ApiOperation(value = "Execute a process", notes = "Create a new job")
    @PostMapping(value = "/processes/{processId}/execution")
    public ResponseEntity<Job> execute(@PathVariable("processId") String processName,
                       @RequestBody ProcessRequestBody processRequest,
                       @ApiIgnore HttpSession session)
            throws IOException, InterruptedException{
        System.out.println(processName + " process is runing...");
        String sessionId = session.getId();
        String jobId = UUID.randomUUID().toString();
        // if process Id == buffer
        if(processName.equals("buffer")){
            // 示例 GeoJSON 字符串
            String geoJsonString = "{\"type\": \"Point\", \"coordinates\": [10, 0]}";
            JSONObject geoJson=JSONObject.parseObject(geoJsonString);
            double distance=2.0;
            JSONObject requestJson = new JSONObject();
            requestJson.put("geoJson", geoJson);
            requestJson.put("distance", distance);


            ResponseEntity<String> responseEntity = httpUtil.jsonHttp("http://localhost:8080/processes/buffer/jobs",
                    requestJson.toString(), HttpMethod.POST, null);
            List<String> cookieList = responseEntity.getHeaders().get("Set-Cookie");
            String cookies = cookieList.stream().collect(Collectors.joining(";"));
            jobId = responseEntity.getBody();
            session.setAttribute(processName + "_" + jobId + "_cookies", cookies);
            System.out.println("session: " + session);
        }
        // if process Id == area
        if(processName.equals("area")){
//            // 示例 GeoJSON 字符串
//            String geoJsonString = "{\"type\": \"Polygon\", \"coordinates\": [[[0, 0], [10, 0], [10, 10], [0, 10], [0, 0]]]}";
            JSONObject geoJson = processRequest.getInputs().getJSONObject("geojson");
            try {
                String result=sshClientUtil.DockerRun("10.101.240.67", "root", "ypfamily",22,geoJson.toString());
                System.out.println("result:"+result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ResponseEntity<String> responseEntity = httpUtil.jsonHttp("http://localhost:8080/processes/area/jobs",
                    geoJson.toString(), HttpMethod.POST, null);
            List<String> cookieList = responseEntity.getHeaders().get("Set-Cookie");
            String cookies = cookieList.stream().collect(Collectors.joining(";"));
            jobId = responseEntity.getBody();
            session.setAttribute(processName + "_" + jobId + "_cookies", cookies);
            System.out.println("session: " + session);
//            // 设置请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // 创建请求主体
//            HttpEntity<String> requestEntity = new HttpEntity<>(geoJsonString, headers);
//
//            // 发送 POST 请求
//            String url = "http://localhost:8080/processes/area/jobs";
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<Double> responseEntity = restTemplate.postForEntity(url, requestEntity, Double.class);
//
//            // 获取响应结果
//            HttpStatus statusCode = responseEntity.getStatusCode();
//            Double area = responseEntity.getBody();
//
//            // 打印响应结果
//            System.out.println("Status code: " + statusCode);
//            System.out.println("Area: " + area);
        }
        Job job = new Job();
        job.setJobID(jobId);
        job.setStatus("accepted");
        job.setMessage("Process started");
        job.setProgress(0);
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        job.setCreated(formatted);
        job.setLinks(Arrays.asList(new Link("http://localhost:8080/processes/" + processName
                + "/jobs/" + jobId,
                "self", "application/json", "area")));
        session.setAttribute(processName + "_" + jobId + "_state", job);
        System.out.println("job: " + job);
        // Create the URI for the new resource
        String uriString = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath("/processes/" + processName + "/jobs/" + jobId)
                .build()
                .toUriString();

        // Return ResponseEntity with 201 Created status and the location header pointing to the new resource URI
        return ResponseEntity.created(URI.create(uriString)).body(job);
    }


    /**
     * Retrieve the status of a job.
     *
     * @param processName processName
     * @param jobId the Id of the job
     * @param session the http session
     * @return Job 对象
     */
    @ApiOperation(value = "retrieve the status of a job", notes = "Shows the status of a job.")
    @GetMapping("/processes/{processId}/jobs/{jobId}")
    public Job getStatus(@PathVariable("processId") String processName,
                         @PathVariable("jobId") String jobId,
                         @ApiIgnore HttpSession session){
        Job job = (Job) session.getAttribute(processName + "_" + jobId + "_state");
        String cookies = (String) session.getAttribute(processName + "_" + jobId + "_cookies");
        return job;
    }

    /**
     * Retrieve the result(s) of a job/
     *
     * @param processName the name of the process
     * @param jobId the Id of the job
     * @param session the session of this process
     * @return Map<String, Object> return the map struct
     * @throws IOException IO Error
     */
    @ApiOperation(value = "retrieve the result(s) of a job", notes = "Lists available results of a job. In case of a failure, lists exceptions instead.")
    @GetMapping("/processes/{processId}/jobs/{jobId}/results")
    public JSONArray getResults(@PathVariable("processId") String processName,
                                @PathVariable("jobId") String jobId,
                                @ApiIgnore HttpSession session) throws IOException {
        JSONArray resultArray = new JSONArray();
        String cookies = (String) session.getAttribute(processName + "_" + jobId + "_cookies");
//        if(processName.equals("buffer")){
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//            String resultsTxt = httpUtil.formHttp(
//                    "http://localhost:8080/processes/buffer/jobs/"+ jobId +"/results",
//                    params, HttpMethod.GET, cookies).getBody();
////            System.out.println("resultsTxt: " + resultsTxt);
//            JSONObject resultObj = JSONObject.parseObject(resultsTxt);
//            System.out.println("resultObj: " + resultObj);
//            resultArray.add(resultObj);
//        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String resultsTxt = httpUtil.formHttp(
                "http://localhost:8080/processes/"+processName+"/jobs/"+ jobId +"/results",
                params, HttpMethod.GET, cookies).getBody();
//            System.out.println("resultsTxt: " + resultsTxt);
        JSONObject resultObj = JSONObject.parseObject(resultsTxt);
        System.out.println("resultObj: " + resultObj);
        resultArray.add(resultObj);
        return resultArray;
    }

    /**
     * Cancel a job execution, remove a finished job.
     *
     * @param name
     * @param jobId
     * @param session
     * @return
     */
    @ApiOperation(value = "cancel a job execution, remove a finished job", notes = "Cancel a job execution and remove it from the jobs list.")
    @DeleteMapping("/processes/{processId}/jobs/{jobId}")
    public Map<String, Object> dismiss(@PathVariable("processId") String name,
                                       @PathVariable("jobId") String jobId, @ApiIgnore HttpSession session){
        Map<String, Object> map = new HashMap<>();
        map.put("jobID", jobId);
        map.put("progress", 0);

        String sparkAppId = (String) session.getAttribute(name + "_" + jobId + "_sparkAppId");
        System.out.println(sparkAppId);
        Runtime run = Runtime.getRuntime();
        String cmd = "curl -X POST \"http://10.101.240.67:9090/app/kill/?id=" + sparkAppId + "&terminate=true\"";
        try{
            Process process = run.exec(cmd);
            int status = process.waitFor();
            if(status != 0){
                map.put("message", "Fail to dismiss the process");
                map.put("status", status);
            } else{
                map.put("message", "Process dismissed");
                map.put("status", "dismissed");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Link link = new Link();
        link.setHref("http://localhost:8080/processes/" + name + "/jobs/" + jobId);
        link.setRel("self");
        link.setType("application/json");
        link.setTitle(name);

        map.put("links", link);
        return map;
    }



    /**
     * deploy a new process to the API.
     *
     * @param ogcapppkg
     * @return
     */

    @ApiOperation(value = "deploy a new process to the API", notes = "The process description contains information about inputs and outputs and a link to the execution-endpoint for the process. The Core does not mandate the use of a specific process description to specify the interface of a process. ")
    @PostMapping(value = "/processes")
    public ResponseEntity<ProcessDesc> deploy(@RequestBody OGCApplicationPackage ogcapppkg,
                                              @RequestParam("CodeFile") String filepath,
                                              @RequestParam("DockerFile") String filepath2
                                              ) throws IOException, InterruptedException {
        ProcessDesc new_processDesc = ogcapppkg.getProcessDescription();
        String processName= new_processDesc.getId();
        try {
            sshClientUtil.FileUploader("10.101.240.67", "root", "ypfamily", 22,filepath,"/home/lyy/");
            System.out.println("文件已存入服务器");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 定义存储 JSON 文件的文件名
        String fileName = processName + "_description.json";
        String filePath = "src/main/resources/processDescription/" + fileName;
        Path path = Paths.get(filePath);
        // 将新数据写入新文件中
        ObjectMapper objectMapper = new ObjectMapper();
        Files.write(path, objectMapper.writeValueAsString(new_processDesc).getBytes());
        System.out.println("New file created: " + fileName);
        // 如果 new_processDesc 不等于 processDescList 中的任何一个 processDesc，将其添加到 processDescList 中
        boolean exists = false;
        for (ProcessDesc desc : processDescList) {
            if (desc.getId().equals(new_processDesc.getId())) {
                exists = true;
                System.out.println("process already exists");
                break;
            }
        }
        if (!exists) {
            processDescList.add(new_processDesc);
            System.out.println("add the new process to the processList");
            processesDesc.setProcesses(processDescList);
        }


        // Create the URI for the new resource
        String uriString = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath("/processes/" + processName )
                .build()
                .toUriString();
//        JSONObject resultObject=new JSONObject();
//        resultObject.put("id",processName);

        // Return ResponseEntity with 201 Created status and the location header pointing to the new resource URI
        return ResponseEntity.created(URI.create(uriString)).body(new_processDesc);

    }

    /**
     * update a  process to the API.
     *
     * @param processName
     * @param ogcapppkg
     * @return
     */
    @ApiOperation(value = "replacing an existing processes", notes = "upgrading a deployed process from a Processes API implementation.")
    @PutMapping(value = "/processes/{processId}")
    public ResponseEntity<Void> replace(@PathVariable("processId") String processName,
                                        @RequestBody OGCApplicationPackage ogcapppkg) throws IOException, InterruptedException {
        ProcessDesc new_processDesc = ogcapppkg.getProcessDescription();
        // 定义存储 JSON 文件的文件名
        String fileName = processName + "_ogcapppkg.json";
        String filePath = "src/main/resources/processOGCapppkg/" + fileName;
        Path path = Paths.get(filePath);
        System.out.println(filePath);

        try {
            // 检查文件是否存在以及算子是否匹配
            if (fileName.equals(new_processDesc.getId() + "_ogcapppkg.json") && Files.exists(path)) {
                System.out.println("start updating");
                ObjectMapper objectMapper = new ObjectMapper();
                String updatedContent = objectMapper.writeValueAsString(ogcapppkg);

                // 写入更新后的内容到文件中
                Files.write(path, updatedContent.getBytes(StandardCharsets.UTF_8));
                System.out.println("File updated: " + fileName);
            } else {
                // 文件不存在或者文件名不匹配，可能需要进行其他处理
                System.out.println("File does not exist or processName does not match: " + fileName);
            }

            // 返回 204 状态码
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            // 捕获并处理 IOException
            e.printStackTrace(); // 打印异常堆栈信息
            // 可以根据需要返回适当的 ResponseEntity，例如返回 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @ApiOperation(value = "removing a processes from the API ", notes = "undeploy a previously deployed process that is accessible via the Processes API endpoint.")
    @DeleteMapping(value = "/processes/{processId}")
    public ResponseEntity<Void> undeploy(@PathVariable("processId") String processName) throws IOException {
//        System.out.println("qqqq"+processesDesc.toString());

        Iterator<ProcessDesc> iterator = processDescList.iterator();
        while (iterator.hasNext()) {
            ProcessDesc desc = iterator.next();
            if (desc.getId().equals(processName) && desc.getMutable()) {
                iterator.remove(); // 使用迭代器安全删除元素
                System.out.println("Successfully find the process and remove it: " + processName);
                //删除文件。。。。。
            }
        }
        processesDesc.setProcesses(processDescList);
        return ResponseEntity.noContent().build();

    }


}
