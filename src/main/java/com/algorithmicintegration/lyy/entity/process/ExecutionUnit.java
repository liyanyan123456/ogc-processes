package com.algorithmicintegration.lyy.entity.process;

import java.io.Serializable;
//Resource containing an executable or runtime information for executing the process.
/*example:
        type: docker
        image: mydocker/ndvi:latest
        deployment: local
        config:
          cpuMin: 2
          cpuMax: 5
          memoryMin: 1
          memoryMax: 3
        */

public class ExecutionUnit implements Serializable {
//8.2.1.1.  Overview
    private String type;  //"docker" or "oci"
    private String image;
    private String deployment;//Deployment information for the execution unit:"local"
    private ExecutionUnitConfig config;

    public ExecutionUnit(String type, String image, String deployment, ExecutionUnitConfig config) {
        this.type = type;
        this.image = image;
        this.deployment = deployment;
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDeployment() {
        return deployment;
    }

    public void setDeployment(String deployment) {
        this.deployment = deployment;
    }

    public ExecutionUnitConfig getConfig() {
        return config;
    }

    public void setConfig(ExecutionUnitConfig config) {
        this.config = config;
    }


}
