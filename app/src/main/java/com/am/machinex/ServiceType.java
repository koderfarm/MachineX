package com.am.machinex;

public class ServiceType {
    String Checktype;
    int check_id;
    String input;
    String status_ok, status_nok;
    int mainID;
    boolean unckecked = false;
    boolean checked_boolean = false;


    public ServiceType(String checktype, String check_id, String input) {
        Checktype = checktype;
        this.check_id = Integer.parseInt(check_id);
        this.input = input;
    }
public ServiceType(){}
   /* public ServiceType(String status_ok, String status_nok, int mainID) {
        this.status_ok = status_ok;
        this.status_nok = status_nok;
        this.mainID = mainID;
    }
*/
    public String getStatus_ok() {
        return status_ok;
    }

    public void setStatus_ok(String status_ok) {
        this.status_ok = status_ok;
    }

    public String getStatus_nok() {
        return status_nok;
    }

    public void setStatus_nok(String status_nok) {
        this.status_nok = status_nok;
    }

    public int getMainID() {
        return mainID;
    }

    public void setMainID(int mainID) {
        this.mainID = mainID;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getChecktype() {
        return Checktype;
    }

    public void setChecktype(String checktype) {
        Checktype = checktype;
    }

    public int getCheck_id() {
        return check_id;
    }

    public void setCheck_id(int check_id) {
        this.check_id = check_id;
    }

    public boolean isSelectedunckecked() {
        return unckecked;
    }

    public void setSelectedunckecked(boolean selected) {
        this.unckecked = selected;
    }
/*

    String checked, unchecked;
    int mainID;


    public ServiceType(String checked, String unchecked, int mainID) {
        checked = checked;
        unchecked = unchecked;
        mainID = mainID;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getUnchecked() {
        return unchecked;
    }

    public void setUnchecked(String unchecked) {
        this.unchecked = unchecked;
    }

    public int getMainID() {
        return mainID;
    }

    public void setMainID(int mainID) {
        this.mainID = mainID;
    }

    public boolean isSelected() {
        return unckecked;
    }

    public void setSelected(boolean selected) {
        this.unckecked = selected;
    }
*/

    public boolean isSelectedcheck() {
        return checked_boolean;
    }

    public void setSelectedcheck(boolean selected) {
        this.checked_boolean = selected;
    }
}
