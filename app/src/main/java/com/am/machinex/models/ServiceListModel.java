package com.am.machinex.models;

public class ServiceListModel {
    String service_type,service_type_code;

    public ServiceListModel(String service_type, String service_type_code) {
        this.service_type = service_type;
        this.service_type_code = service_type_code;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_type_code() {
        return service_type_code;
    }

    public void setService_type_code(String service_type_code) {
        this.service_type_code = service_type_code;
    }
}
