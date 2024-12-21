package com.algorithmicintegration.lyy.entity.process;

import java.io.Serializable;
import java.util.List;

/**
 * 这个类作为 baseUrl/processes的返回的response
 */
public class ProcessesDesc implements Serializable {

    private List<ProcessDesc> processes;
    private List<Link> links;

    public List<ProcessDesc> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessDesc> processes) {
        this.processes = processes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
