package com.clxk.h.easydbms.bean;

public class UserPower {

    private String username;
    private String tablename;
    private String power[];
    private int cntPower;

    public UserPower() {
        power = new String[6];
        cntPower = 0;
    }

    //setter
    public void setTablename(String tablename) {
        this.tablename = tablename;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void addPower(String power) {
        this.power[cntPower++] = power;
    }

    //getter
    public String getUsername() {
        return this.username;
    }
    public String getTablename() {
        return this.tablename;
    }
    public boolean have_insert() {
        for(int i = 0; i < this.cntPower; i++) {
            if(this.power[i].equals("insert")) {
                return true;
            }
        }
        return false;
    }
    public boolean have_delete() {
        for(int i = 0; i < this.cntPower; i++) {
            if(this.power[i].equals("delete")) {
                return true;
            }
        }
        return false;
    }
    public boolean have_update() {
        for(int i = 0; i < this.cntPower; i++) {
            if(this.power[i].equals("update")) {
                return true;
            }
        }
        return false;
    }
    public boolean have_select() {
        for(int i = 0; i < this.cntPower; i++) {
            if(this.power[i].equals("select")) {
                return true;
            }
        }
        return false;
    }

}
