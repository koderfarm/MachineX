package com.am.machinex.models;

public class FaultList {

    String machine_fault,machine_fault_code;

    public FaultList(String machine_fault, String machine_fault_code) {
        this.machine_fault = machine_fault;
        this.machine_fault_code = machine_fault_code;
    }

    public String getMachine_fault() {
        return machine_fault;
    }

    public void setMachine_fault(String machine_fault) {
        this.machine_fault = machine_fault;
    }

    public String getMachine_fault_code() {
        return machine_fault_code;
    }

    public void setMachine_fault_code(String machine_fault_code) {
        this.machine_fault_code = machine_fault_code;
    }
}
