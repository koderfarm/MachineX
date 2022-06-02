package com.am.machinex;

public class ServiceType {
    String Checktype;
    int check_id;
    String input;
    boolean selected = false;
    private boolean isTextEmpty;

    private String textData;

    public void setTextEmpty(boolean isTrue){
        isTextEmpty = isTrue;
    }

    public boolean isTextEmpty(){
        return isTextEmpty;
    }

    public void setTextData(String data){
        textData = data;
    }

    public String getData(){
        return textData;
    }

    public ServiceType(String checktype, String check_id,String input) {
        Checktype = checktype;
        this.check_id = Integer.parseInt(check_id);
        this.input = input;
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
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
