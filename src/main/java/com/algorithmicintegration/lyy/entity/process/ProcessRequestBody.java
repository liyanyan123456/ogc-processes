package com.algorithmicintegration.lyy.entity.process;


import com.alibaba.fastjson.JSONObject;

public class ProcessRequestBody {
    private JSONObject inputs;
    /**
     * 包含transmissionMode:value/reference; format:{mediaType: ""} 在description中是可以选择的；
     * 注意，如果在request中包含了outputs，那么ogc process API只支持返回outputs中所列出的输出名字的输出
     */
    private JSONObject outputs;
    /**
     * enum: document/raw 要选择document，返回的是json格式
     */
    private String response = "document";

    private String mode = "async";
    /**
     * 订阅者 暂时不考虑
     */
    private JSONObject subscriber;

    public JSONObject getInputs() {
        return inputs;
    }

    public void setInputs(JSONObject inputs) {
        this.inputs = inputs;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public JSONObject getOutputs() {
        return outputs;
    }

    public void setOutputs(JSONObject outputs) {
        this.outputs = outputs;
    }

    public JSONObject getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(JSONObject subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "ProcessRequestBody{" +
                "inputs=" + inputs +
                ", outputs=" + outputs +
                ", response='" + response + '\'' +
                ", mode='" + mode + '\'' +
                ", subscriber=" + subscriber +
                '}';
    }
}

