package com.am.machinex;

public class ServiceType {
    String Checktype;
    int check_id;
    String input;
    boolean unckecked = false;
    boolean checked_boolean = false;


    public ServiceType(String checktype, String check_id, String input) {
        Checktype = checktype;
        this.check_id = Integer.parseInt(check_id);
        this.input = input;
    }
public ServiceType(){}
    public boolean isSelectedunckecked() {
        return unckecked;
    }

    public void setSelectedunckecked(boolean selected) {
        this.unckecked = selected;
    }

    public boolean isSelectedcheck() {
        return checked_boolean;
    }

    public void setSelectedcheck(boolean selected) {
        this.checked_boolean = selected;
    }
}
