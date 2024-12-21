package com.algorithmicintegration.lyy.entity.process;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

public class ProcessDesc implements Serializable {
    private String id; //name
    private String title;
    private String version;
    private Boolean mutable;//whether a process is a static or built-in process that is immutable or a dynamically added process that is mutable.
    private List<String> jobControlOptions;
    private List<String> outputTransmission;
    private JSONObject inputs;
    private JSONObject outputs;
    List<Link> links;

    // 默认构造函数
    public ProcessDesc() {
    }

    public ProcessDesc(String id, String title, String version,Boolean mutable,
                       List<String> jobControlOptions, List<String> outputTransmission, List<Link> links){
        this.id = id;
        this.title = title;
        this.version = version;
        this.mutable = mutable;
        this.jobControlOptions = jobControlOptions;
        this.outputTransmission = outputTransmission;
        this.links = links;
    }


    public JSONObject getInputs() {
        return inputs;
    }

    public void setInputs(JSONObject inputs) {
        this.inputs = inputs;
    }

    public JSONObject getOutputs() {
        return outputs;
    }

    public void setOutputs(JSONObject outputs) {
        this.outputs = outputs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getMutable() {
        return mutable;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getJobControlOptions() {
        return jobControlOptions;
    }

    public void setJobControlOptions(List<String> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    public List<String> getOutputTransmission() {
        return outputTransmission;
    }

    public void setOutputTransmission(List<String> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
