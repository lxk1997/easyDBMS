package com.clxk.h.easydbms.bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class User {

    private String username;
    private String password;
    private int user_type;//1 超级管理员 2 普通用户


    public User() {
        super();
    }

    public User(String username, String password, int user_type) {
        this.username = username;
        this.password = password;
        this.user_type = user_type;
    }

    //setter
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    //getter
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }
    public int getUser_type() {
        return this.user_type;
    }

    public boolean is_have(String name) throws IOException {
        FileReader fr = new FileReader("/sdcard/edbms/user/user.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(name.trim())) {
                br.close();
                fr.close();
                return true;
            }
        }
        br.close();
        fr.close();
        return false;
    }

    public void insertIntoUser(String username, String password) throws IOException{
        FileWriter fw = new FileWriter("/sdcard/edbms/user/user.txt",true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(username+" "+password+"\n");
        bw.close();
        fw.close();
    }
    public void insertIntoPro(String username,int power) throws IOException{
        if(power == 1) {
            FileWriter fw = new FileWriter("/sdcard/edbms/user/simple.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(username+"\n");
            bw.close();
            fw.close();
        } else {
            FileWriter fw = new FileWriter("/sdcard/edbms/user/super.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(username+"\n");
            bw.close();
            fw.close();
        }
    }

    public boolean check(String s) {
        if(s.length() < 3 || s.length() > 15) return false;
        for(int i = 0; i < s.length(); i++) {
            if(!Character.isLetterOrDigit(s.charAt(i))) return false;
        }
        return true;
    }
}
