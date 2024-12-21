package com.algorithmicintegration.lyy.entity.process;


import java.io.Serializable;
import java.util.List;

//OGC Application Package, that defines a formal process description language encoded using JSON
public class OGCApplicationPackage implements  Serializable{
    private ProcessDesc processDesc;
//    executionUnit property
    private ExecutionUnit executionUnit; // 同样根据实际情况选择合适的数据类型

    public OGCApplicationPackage(ProcessDesc processDesc, ExecutionUnit executionUnit) {
        this.processDesc = processDesc;
        this.executionUnit = executionUnit;
    }

    public ProcessDesc  getProcessDescription() {
        return processDesc;
    }

    public void setProcessDescription(ProcessDesc processDesc) {
        this.processDesc = processDesc;
    }

    public Object getExecutionUnit() {
        return executionUnit;
    }

    public void setExecutionUnit(ExecutionUnit executionUnit) {
        this.executionUnit = executionUnit;
    }

}
