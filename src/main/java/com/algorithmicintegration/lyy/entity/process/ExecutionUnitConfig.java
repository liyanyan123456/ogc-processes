package com.algorithmicintegration.lyy.entity.process;

import java.io.Serializable;

public class ExecutionUnitConfig implements Serializable {
    private Double cpuMin;
    private Double cpuMax;
    private Double memoryMin;
    private Double memoryMax;
//    private Double storageTempMin;
//    private Double storageOutputsMin;
//    private Integer jobTimeout;

    public Double getCpuMin() {
        return cpuMin;
    }

    public void setCpuMin(Double cpuMin) {
        this.cpuMin = cpuMin;
    }

    public Double getCpuMax() {
        return cpuMax;
    }

    public void setCpuMax(Double cpuMax) {
        this.cpuMax = cpuMax;
    }

    public Double getMemoryMin() {
        return memoryMin;
    }

    public void setMemoryMin(Double memoryMin) {
        this.memoryMin = memoryMin;
    }

    public Double getMemoryMax() {
        return memoryMax;
    }

    public void setMemoryMax(Double memoryMax) {
        this.memoryMax = memoryMax;
    }
//
//    public Double getStorageTempMin() {
//        return storageTempMin;
//    }
//
//    public void setStorageTempMin(Double storageTempMin) {
//        this.storageTempMin = storageTempMin;
//    }
//
//    public Double getStorageOutputsMin() {
//        return storageOutputsMin;
//    }
//
//    public void setStorageOutputsMin(Double storageOutputsMin) {
//        this.storageOutputsMin = storageOutputsMin;
//    }
//
//    public Integer getJobTimeout() {
//        return jobTimeout;
//    }
//
//    public void setJobTimeout(Integer jobTimeout) {
//        this.jobTimeout = jobTimeout;
//    }

}
