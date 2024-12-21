package com.algorithmicintegration.lyy.entity.process;

import java.io.Serializable;
import java.util.List;

public class Job implements Serializable {
    private String jobID;
    /**
     * 状态 accepted running successful failed dismissed
     */
    private String status;
    /**
     * 消息
     */
    private String message;
    /**
     * 进度
     */
    private Integer progress;
    /**
     * 创建时间
     */
    private String created;
    /**
     * 链接
     */
    private List<Link> links;

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
