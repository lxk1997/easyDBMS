package com.clxk.h.easydbms.controller;

import android.util.Log;

import com.clxk.h.easydbms.EDBMSConf;
import com.clxk.h.easydbms.bean.UserPower;
import com.clxk.h.easydbms.operator.FileIOOperator;
import com.clxk.h.easydbms.utils.InexSortComparator;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CheckSqlGram {

    private String TYPE[] = {"varchar", "int", "char", "datatime"};
    private String strnull = "TYPE_NULL";
    private FileIOOperator fileIOOperator;
    private UserPower userPower = new UserPower();
    public static int posFiled[] = new int[1000];

    public CheckSqlGram() throws IOException {
        fileIOOperator = new FileIOOperator();
    }

    public boolean enable(String s) {
        String sql = s.trim();
        String arrSql[] = sql.split("\\s");
        if (arrSql.length != 1) {
            return false;
        } else {
            if (!arrSql[0].toLowerCase().trim().equals("enable")) {
                return false;
            } else {
                return true;
            }
        }
    }

    //CHECH NAME IN a~z
    public boolean checkName(String s) {
        s = s.toLowerCase();
        char str[] = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (str[i] < 'a' || str[i] > 'z') {
                return false;
            }
        }
        return true;
    }

    /*
     * -1 false
     * 1 create database
     * 2 user database
     * 3 create table
     * 4 insert to table
     * 5 delete from table
     * 6 help database
     * 7 help table
     * */
    public int checkSql(String s) {
        String sql = s.trim();
        String arrSql[] = sql.split("\\s");
        if (arrSql[0].trim().toLowerCase().equals("enable")) {
            Log.i("qaq","-------------enable---------------");
            return 1000;
        }
        if (arrSql[0].trim().toLowerCase().equals("create") && arrSql[1].trim().toLowerCase().equals("database")) {
            Log.i("qaq","-------------create database---------------");
            return 1;
        }
        if (arrSql[0].trim().toLowerCase().equals("use")) {
            Log.i("qaq","-------------use database---------------");
            return 2;
        }
        if (arrSql[0].trim().toLowerCase().equals("create") && arrSql[1].trim().toLowerCase().equals("table")) {
            Log.i("qaq","-------------create table---------------");
            return 3;
        }
        if (arrSql[0].trim().toLowerCase().equals("insert")) {
            Log.i("qaq","-------------insert---------------");
            return 4;
        }
        if (arrSql[0].trim().toLowerCase().equals("delete")) {
            Log.i("qaq","-------------delete---------------");
            return 5;
        }
        if (arrSql.length >= 2 && arrSql[0].trim().toLowerCase().equals("help") && arrSql[1].trim().toLowerCase().equals("database")) {
            Log.i("qaq","-------------help database---------------");
            return 6;
        }
        if (arrSql.length >= 2 && arrSql[0].trim().toLowerCase().equals("help") && arrSql[1].trim().toLowerCase().equals("table")) {
            Log.i("qaq","-------------help table---------------");
            return 7;
        }
        if(arrSql.length >= 2 && arrSql[0].trim().toLowerCase().equals("help") && arrSql[1].trim().toLowerCase().equals("index")) {
            Log.i("qaq","-------------help index---------------");
            return 11;
        }
        if(arrSql[0].trim().toLowerCase().equals("update")) {
            Log.i("qaq","-------------update---------------");
            return 8;
        }
        if(arrSql[0].trim().toLowerCase().equals("select")) {
            Log.i("qaq","-------------select---------------");
            return 9;
        }
        if(arrSql[0].trim().toLowerCase().equals("create") && arrSql[1].trim().toLowerCase().equals("index")) {
            Log.i("qaq","-------------create index---------------");
            return 10;
        }
        if(arrSql[0].trim().toLowerCase().equals("grant")) {
            Log.i("qaq","-------------grant---------------");
            return 12;
        }
        if(arrSql[0].trim().toLowerCase().equals("revoke")) {
            Log.i("qaq","-------------revoke---------------");
            return 13;
        }
        return -1;
    }

    /*
     * 0 语法错误
     * 1 数据库存在
     * 2 命名错误
     * 3 正确
     * */
    public int CheckDBCreate(String s) throws IOException {
        String sql = s.trim();
        String arrSql[] = sql.split("\\s");
        if (arrSql.length != 3) {
            Log.i("qaq","-------------创建数据库语法不是三个字符串---------------");
            return 0;
        } else if (!checkName(arrSql[2])) {
            Log.i("qaq","-------------创建数据库命名含非法字符---------------");
            return 2;
        } else {
            if (!arrSql[0].trim().toLowerCase().equals("create") || !arrSql[1].trim().toLowerCase().equals("database")) {
                return 0;
            } else if (fileIOOperator.is_exists("/db/" + arrSql[2].trim())) {
                return 1;
            } else {
                File file = new File("/sdcard/edbms/db/" + arrSql[2].trim() + "/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File("/sdcard/edbms/db/" + arrSql[2].trim() + "/dbindex.txt");
                file.createNewFile();
                file = new File("/sdcard/edbms/db/"+arrSql[2].trim()+"/retirindex.txt");
                file.createNewFile();
                FileWriter fw = new FileWriter("/sdcard/edbms/user/dbcreate.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(EDBMSConf.getInstance().username + " " + arrSql[2].trim() + "\n");
                bw.close();
                fw.close();
                Log.i("qaq","-------------数据库创建完成---------------");
                return 3;
            }
        }
    }

    /*
     * 0 语法错误
     * 2 数据库不存在
     * 3 正确
     * */
    public int CheckDBUse(String s) {
        String sql = s.trim();
        String arrSql[] = sql.split("\\s");
        if (arrSql.length != 2) {
            return 0;
        } else {
            if (!fileIOOperator.is_exists("/db/" + arrSql[1].trim())) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    /*
     * 1 命名不合法
     * 2 表存在
     * 3 语法错误
     * 5 创建完成
     * 6 主键不唯一
     * 7 外键表不存在
     * 8 外键表对应字段不存在
     * 9 外键表对应字段非主键
     * create table qwe (,,,)
     * */
    public int CheckTableCreate(String s) throws IOException {
        String sql = s.trim();
        String arrSql[] = sql.split("\\s");
        if (!checkName(arrSql[2])) {
            Log.i("qaq","-------------table命名不合法---------------");
            return 1;
        } else if (fileIOOperator.tableExistsInDB(arrSql[2])) {
            Log.i("qaq","-------------table已经存在---------------");
            return 2;
        } else {
            if (arrSql.length < 4) {
                Log.i("qaq","-------------创建表命令小于四个字符串---------------");
                return 3;
            } else if (arrSql[3].charAt(0) != '(' || arrSql[arrSql.length - 1].charAt(arrSql[arrSql.length - 1].length() - 1) != ')') {
                Log.i("qaq","-------------创建表命令第四个字符串起始字符不是‘（’或者最后一个字符串结束字符不是‘）’---------------");
                return 3;
            } else {
                int sindex = -1, eindex = s.length() - 1;
                for (int i = 0; i < s.length(); i++) {
                    if (sindex == -1 && s.charAt(i) == '(') {
                        sindex = i;
                    }
                }
                String substr = s.substring(sindex + 1, eindex);
                String arrIndex[] = substr.split(",");
                int is_wrong = 0;
                int cnt_primary = 0;
                for (int i = 0; i < arrIndex.length; i++) {
                    String spIndex[] = arrIndex[i].trim().split("\\s");
                    if (spIndex.length < 2) {
                        Log.i("qaq","-------------创建表字符串字段小于两个字符串---------------");
                        return 3;
                    }
                    String type = spIndex[1].trim();
                    if ((type.length() == 3 && type.substring(0, 3).equals("int")) ||
                            (type.length() >= 8 && (type.substring(0, 8).toLowerCase().equals("varchar(") && type.charAt(type.length() - 1) == ')' && isNumeric(type.substring(8, type.length() - 1)))) ||
                            (type.length() >= 5 && (type.substring(0, 5).toLowerCase().equals("char(") && type.charAt(type.length() - 1) == ')' && isNumeric(type.substring(5, type.length() - 1))))) {
                    } else {
                        is_wrong = 1;
                    }
                    if(arrIndex[i].trim().toLowerCase().indexOf("primary key") != -1) {
                        Log.i("qaq","-------------测试创建表字符串字段的主键约束---------------");
                        if(spIndex.length != 4) {
                            Log.i("qaq","-------------测试创建表字符串字段的主键约束---字符串长度不为4---------------");
                            return 3;
                        }
                        cnt_primary++;
                        if(cnt_primary > 1) {
                            Log.i("qaq","-------------测试创建表字符串字段的主键约束---主键不止一个---------------");
                            return 6;
                        }

                    }
                    else if(arrIndex[i].trim().toLowerCase().indexOf("unique") != -1) {
                        Log.i("qaq","-------------测试创建表字符串字段的唯一约束----------------");
                        if(spIndex.length != 3) {
                            Log.i("qaq","-------------测试创建表字符串字段的唯一约束-------字符串长度不为3---------");
                            return 3;
                        }
                    }
                    else if(arrIndex[i].trim().toLowerCase().indexOf("not null") != -1) {
                        Log.i("qaq","-------------测试创建表字符串字段的非空约束----------------");
                        if(spIndex.length != 4) {
                            Log.i("qaq","-------------测试创建表字符串字段的非空约束------字符串长度不为4----------");
                            return 3;
                        }
                    }
                    else if(arrIndex[i].trim().toLowerCase().indexOf("foreign key references") != -1) {
                        Log.i("qaq","-------------测试创建表字符串字段的外键约束----------------");
                        if(spIndex.length != 6) {
                            Log.i("qaq","-------------测试创建表字符串字段的外键约束------字符串长度不为6----------");
                            return 3;
                        }
                        int stcur;
                        stcur = arrIndex[i].trim().toLowerCase().trim().indexOf("references");
                        String sub;
                        sub = arrIndex[i].trim().substring(stcur + 11);
                        int subcur = 0;
                        for(int j = sub.length()-1; j >= 0; j--) {
                            if(sub.charAt(j) == '(') {
                                subcur = j;
                                break;
                            }
                        }
                        if(subcur == 0) {
                            Log.i("qaq","-------------分割创建表字符串字段的外键约束的外键表内容-----找不到'('-----------");
                            return 3;
                        }
                        String table;
                        //create table qwertt ( sname int foreign key references test(sname) )
                        table = sub.trim().substring(0, subcur).trim();
                        String field = sub.trim().substring(subcur+1,sub.trim().length()-1).trim();
                        if(!fileIOOperator.tableExistsInDB(table.trim())) {
                            Log.i("qaq","-------------分割创建表字符串字段的外键约束的外键表内容-----外键表不存在-----------");
                            return 7;
                        }
                        FileReader fr = new FileReader("/sdcard/edbms/table/"+table.trim()+"/index.txt");
                        BufferedReader bf = new BufferedReader(fr);
                        bf.readLine();
                        String fid;
                        int have = 0;
                        while((fid = bf.readLine()) != null) {
                            String spFid[] = fid.trim().split("\\s");
                            if(spFid[0].equals(field)) {
                                have++;
                                if(fid.toLowerCase().indexOf("primary") != -1) {
                                    have++;
                                }
                                break;
                            }
                        }
                        bf.close();
                        fr.close();
                        if(have == 0) {
                            Log.i("qaq","-------------分割创建表字符串字段的外键约束的外键表内容-----外键表不包含字段-----------");
                            return 8;
                        }
                        if(have == 1) {
                            Log.i("qaq","-------------分割创建表字符串字段的外键约束的外键表内容-----外键表该字段不为主键-----------");
                            return 9;
                        }
                        FileWriter fw = new FileWriter("/sdcard/edbms/user/map.txt",true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(table+" "+field+" "+ arrSql[2].trim()+" "+spIndex[0].trim()+"\n");
                        bw.close();
                        fw.close();
                    }
                    else if(arrIndex[i].toLowerCase().indexOf("null") != -1) {
                        Log.i("qaq","-------------检查创建表字符串字段的null约束----------------");
                        if(spIndex.length != 3) {
                            Log.i("qaq","-------------检查创建表字符串字段的null约束----字符串长度不为3------------");
                            return 3;
                        }
                    }
                    else if(spIndex.length != 2) {
                        Log.i("qaq","-------------检查创建表字符串字段长度不为二的非法串----------------");
                        return 3;
                    }
                }
                if(is_wrong == 1) {
                    Log.i("qaq","-------------创建表字符串字段类型不合法---------------");
                    return 3;
                }
                fileIOOperator.tableInDB(arrSql[2]);
                FileWriter fw = new FileWriter("/sdcard/edbms/table/" + arrSql[2] + "/index.txt", true);
                BufferedWriter bf = new BufferedWriter(fw);
                bf.write(arrIndex.length + "\n");
                for (int i = 0; i < arrIndex.length; i++) {
                    bf.write(arrIndex[i].trim() + "\n");
                }
                bf.close();
                fw.close();
                fw = new FileWriter("/sdcard/edbms/user/grant.txt",true);
                bf = new BufferedWriter(fw);
                bf.write(EDBMSConf.getInstance().username+" " + arrSql[2].trim()+"\n");
                bf.close();
                fw.close();
                Log.i("qaq","-------------table创建完成---------------");
                return 5;
            }
        }
    }

    /*
     * 1 语法错误
     * 2 表不存在
     * 3 输入值有误
     * 4 值已存在
     * 5 插入成功
     * 6 没有权限
     * 7 违反非空约束，插入失败！
     * 8 违反唯一约束，插入失败！
     * 9 违反参照约束，插入失败！
     * insert into table values ('','','')
     */
    public int CheckTableInsert(String s) throws IOException {
        String arrSql[] = s.trim().split("\\s");
        String arrValue[] = arrSql[4].trim().substring(1, arrSql[4].length() - 1).split(",");
        if (arrSql.length < 5) {
            Log.i("qaq","-------------insert into-----插入字符串长度小于5----------");
            return 1;
        }
        if (!arrSql[1].toLowerCase().trim().equals("into")) {
            Log.i("qaq","-------------insert into-----插入字符串第二个字符串不是'into'----------");
            return 1;
        }
        if (!arrSql[3].toLowerCase().trim().equals("values")) {
            Log.i("qaq","-------------insert into-----插入字符串第四个字符串不是'values'----------");
            return 1;
        }
        if (!fileIOOperator.tableExistsInDB(arrSql[2].trim())) {
            Log.i("qaq","-------------insert into-----需要插入的表不存在----------");
            return 2;
        }
        String db = EDBMSConf.getInstance().dbName;
        FileReader fr = new FileReader("/sdcard/edbms/table/" + arrSql[2].trim() + "/index.txt");
        BufferedReader bf = new BufferedReader(fr);
        if (Integer.valueOf(bf.readLine()) != arrValue.length) {
            Log.i("qaq","-------------insert into-----需要插入的字段和对应字段数不匹配----------");
            return 3;
        }
        String type;
        int is_wrong = 0;
        int cur = 0;
        while ((type = bf.readLine()) != null) {
            String typeArr[] = type.trim().split("\\s");
            //sname int
            type = typeArr[1];
            if (!checkType(type, arrValue[cur].substring(1, arrValue[cur].length() - 1))) {
                is_wrong = 1;
                break;
            }
            cur++;
        }
        if (is_wrong == 1) {
            Log.i("qaq","-------------insert into-----插入字段和属性类型不符----------");
            return 3;
        }
        bf.close();
        fr.close();
        if(!have_power(arrSql[2].trim(), "insert")) {
            Log.i("qaq","-------------insert into-----当前用户没有插入权限----------");
            return 6;
        }
        fr = new FileReader("/sdcard/edbms/table/" + arrSql[2].trim() + "/content.txt");
        bf = new BufferedReader(fr);
        String value;
        while ((value = bf.readLine()) != null) {
            String val[] = value.trim().split("\\s");
            int is_have = 1;
            for (int i = 0; i < arrValue.length; i++) {
                if (!arrValue[i].trim().substring(1, arrValue[i].trim().length() - 1).equals(val[i])) {
                    is_have = 0;
                    break;
                }
            }
            if (is_have == 1) {
                Log.i("qaq","-------------insert into-----待插入的值所有值都重复----------");
                return 4;
            }
        }
        bf.close();
        fr.close();
        //非空检查
        Log.i("qaq","-------------insert into---------------字段长度： "+arrValue.length);
        for(int i = 0; i < arrValue.length; i++) {
            Log.i("qaq",arrValue[i]);
            if(arrValue[i].trim().equals("''") && !can_null(arrSql[2].trim(), find_field(arrSql[2].trim(),i))) {
                Log.i("qaq","-------------insert into-------字段值不能为空且当前字段为空--------"+arrValue.length);
                return 7;
            }
        }
        //唯一约束
        for(int i = 0; i < arrValue.length; i++) {
            if(have_unique(arrSql[2].trim(), find_field(arrSql[2].trim(),i)) && !is_unique(arrSql[2].trim(),i,arrValue[i].trim().substring(1, arrValue[i].trim().length() - 1))) {
                Log.i("qaq","-------------insert into-----字段值唯一且当前字段重复----------");
                return 8;
            }
        }
        //参照被参照检查
        for(int i = 0; i < arrValue.length; i++) {
            if(!is_reference(arrSql[2].trim(),find_field(arrSql[2].trim(),i),arrValue[i].trim().substring(1, arrValue[i].trim().length() - 1))) {
                Log.i("qaq","-------------insert into-----被参照表中没有参照表参照字段的内容----------");
                return 9;
            }
        }
        FileWriter fw = new FileWriter("/sdcard/edbms/table/" + arrSql[2] + "/content.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < arrValue.length; i++) {
            if(arrValue[i].trim().equals("''")) {
                arrValue[i] = "'"+strnull+"'";
            }
            if (i == 0) {
                bw.write(arrValue[i].trim().substring(1, arrValue[i].trim().length() - 1));
            } else if (i != arrValue.length - 1) {
                bw.write(" " + arrValue[i].trim().substring(1, arrValue[i].trim().length() - 1));
            } else {
                bw.write(" " + arrValue[i].trim().substring(1, arrValue[i].trim().length() - 1) + "\n");
            }
        }
        bw.close();
        fw.close();
        refreshIndex(arrSql[2].trim());
        Log.i("qaq","-------------insert into 完成---------------");
        return 5;
    }

    /*
     * 1 语法错误
     * 2 表不存在
     * 3 值不存在
     * 4 删除成功
     * 5 没有权限
     * 6 被参照表，删除失败
     * delete from tablename where id=nio
     */
    public int deleteFromTable(String str) throws IOException {
        String[] arrSql = str.split("\\s");
        //delete from tablename
        if(arrSql.length < 3) {
            Log.i("qaq","-------------delete from-----删除字符串长度小于3----------");
            return 1;
        }
        if (arrSql.length == 3 && arrSql[1].trim().equals("from")) {
            if (!fileIOOperator.tableExistsInDB(arrSql[2].trim())) {
                Log.i("qaq","-------------delete from-----删除的表不存在----------");
                return 2;
            }
            else {
                deleteTableContext(arrSql[2].trim());
                Log.i("qaq","-------------delete from-----直接删除表全部信息，删除成功----------");
                return 4;
            }
        } else if (arrSql.length == 3) {
            Log.i("qaq","-------------delete from-----删除字符串长度为3且非法----------");
            return 1;
        } else if (arrSql.length == 4 && arrSql[1].trim().equals("*") && arrSql[2].trim().equals("from")) {
            if (!fileIOOperator.tableExistsInDB(arrSql[3].trim())) {
                Log.i("qaq","-------------delete * from-----删除表不存在----------");
                return 2;
            }
            else {
                deleteTableContext(arrSql[2].trim());
                Log.i("qaq","-------------delete * from-----删除成功----------");
                return 4;
            }
        } else if (arrSql.length == 4) {
            Log.i("qaq","-------------delete * from-----字符串长度为4且非法----------");
            return 1;
        } else {
            if(!arrSql[3].trim().toLowerCase().equals("where")) {
                Log.i("qaq","-------------delete from-----删除语句没有‘where’----------");
                return 1;
            }
            int st = 0;
            for(int i = 0; i < str.length(); i++) {
                if(str.substring(i,i+5).toLowerCase().equals("where")) {
                    st = i+6;
                    break;
                }
            }
            FileReader fr = new FileReader("/sdcard/edbms/table/"+arrSql[2].trim()+"/index.txt");
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String substr = str.substring(st, str.length());
            String arrSub[] = substr.trim().split("=");
            if(arrSub.length != 2) {
                Log.i("qaq","-------------delete from-----删除表对应字段不为2----------");
                return 1;
            }
            String curstr;
            int cur = 0, is_have = 0;
            while((curstr = br.readLine()) != null) {
                String arrCur[] = curstr.split("\\s");
                if(arrCur[0].trim().equals(arrSub[0].trim())) {
                    is_have = 1;
                    break;
                }
                cur++;
            }
            br.close();
            fr.close();
            if(is_have == 0) {
                Log.i("qaq","-------------delete from-----删除表对应字段不存在----------");
                return 3;
            }
            if(!have_power(arrSql[2].trim(), "delete")) {
                Log.i("qaq","-------------delete from-----没有删除权限----------");
                return 5;
            }
            if(!can_delete(arrSql[2].trim())) {
                Log.i("qaq","-------------delete from-----为被参照表不能删除----------");
                return 6;
            }
            if(arrSub[1].trim().equals("''")) arrSub[1] = "'"+strnull+"'";
            ArrayList<String> arrayList = new ArrayList<String>();
            fr = new FileReader("/sdcard/edbms/table/"+arrSql[2].trim()+"/content.txt");
            br = new BufferedReader(fr);
            while((curstr = br.readLine()) != null) {
                String arrCur[] = curstr.split("\\s");
                if(arrCur[cur].trim().equals(arrSub[1].trim().substring(1,arrSub[1].trim().length()-1))) {
                    continue;
                } else {
                    arrayList.add(curstr);
                }
            }
            br.close();
            fr.close();
            FileWriter fw = new FileWriter("/sdcard/edbms/table/"+arrSql[2].trim()+"/content.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for(String s : arrayList) {
                bw.write(s+"\n");
            }
            bw.close();
            fw.close();
            refreshIndex(arrSql[2].trim());
            Log.i("qaq","-------------delete from-----删除成功----------");
            return 4;
        }
    }

    /*
     * 1 语法错误
     * 2 表不存在
     * 3 值不存在
     * 4 字段不存在
     * 5 更新成功
     * 6 没有权限
     * 7 违反非空约束，更新失败！
     * 8 违反唯一约束，更新失败！
     * 9 违反参照约束，更新失败！
     * update tablename set id='dd',dsa='das' where ss='dsad'
     * */
    public int updateInTable(String str) throws IOException {
        String arrSql[] = str.trim().split("\\s");
        if(arrSql.length < 6) {
            Log.i("qaq","-------------update-----更新语句字符串长度小于6----------");
            return 1;
        }
        if(!arrSql[2].trim().toLowerCase().equals("set")) {
            Log.i("qaq","-------------update-----更新语句第三个字符串不是'set'----------");
            return 1;
        }
        if(!fileIOOperator.tableExistsInDB(arrSql[1].trim())) {
            Log.i("qaq","-------------update-----更新表不存在----------");
            return 2;
        }
        int cur_set = 0, cur_where = 0;
        for(int i = 0; i < str.length(); i++) {
            if(str.substring(i, i+3).toLowerCase().equals("set")) {
                cur_set = i;
            } else if(str.substring(i, i+5).toLowerCase().equals("where")) {
                cur_where = i;
                break;
            }
        }
        if(cur_set == 0 || cur_where == 0) {
            Log.i("qaq","-------------update-----更新语句'set'或‘where’字符串不存在----------");
            return 1;
        }
        String substr = str.trim().substring(cur_set+4,cur_where-1);
        String fields[] = substr.split(",");
        int cntFields = fields.length;
        int curField[] = new int[1000];
        String map[] = new String[1000];
        String val[] = new String[1000];
        for(int i = 0; i < cntFields; i++) {
            String spField[] = fields[i].trim().split("=");
            if(spField.length != 2) {
                Log.i("qaq","-------------update-----更新语句字段不为2----------");
                return 1;
            }
            map[i] = spField[0].trim();
            if(spField[1].trim().equals("''")) {
                val[i] = strnull;
                continue;
            }
            val[i] = spField[1].trim().substring(1,spField[1].trim().length()-1);
        }
        //可修改检查
        for(int i = 0; i < cntFields; i++) {
            if(!can_change(arrSql[1].trim(),map[i].trim())) {
                Log.i("qaq","-------------update-----被参照表不能更新----------");
                return 9;
            }
        }
        //非空检查
        for(int i = 0; i < cntFields; i++) {
            if(val[i].equals(strnull) && !can_null(arrSql[1].trim(), map[i].trim())) {
                Log.i("qaq","-------------update-----更新语句字符串对应字段不能为空且为空----------"+i);
                return 7;
            }
        }
        //唯一约束
        for(int i = 0; i < cntFields; i++) {
            if(have_unique(arrSql[1].trim(), map[i].trim()) && !is_unique(arrSql[1].trim(),i,val[i].trim())) {
                Log.i("qaq","-------------update-----更新语句字符串对应字段有唯一性约束且不唯一----------");
                return 8;
            }
        }
        //参照被参照检查
        for(int i = 0; i < cntFields; i++) {
            if(!is_reference(arrSql[1].trim(),map[i].trim(),val[i].trim())) {
                Log.i("qaq","-------------update-----更新语句字符串对应字段内容不在被参照表中----------");
                return 9;
            }
        }
        substr = str.substring(cur_where+6,str.trim().length());
        String arrStr[] = substr.trim().split("=");
        int posField = -1;
        FileReader fr = new FileReader("/sdcard/edbms/table/"+arrSql[1].trim() + "/index.txt");
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        int cur = 0;
        int is_all = 0;
        while((curStr = br.readLine()) != null) {
            String arrCur[] = curStr.trim().split("\\s");
            if(arrCur[0].trim().equals(arrStr[0].trim())) {
                posField = cur;
            }
            for(int i = 0; i < cntFields; i++) {
                if(map[i].trim().equals(arrCur[0].trim())) {
                    curField[i] = cur;
                    is_all++;
                }
            }
            cur++;
        }
        if(is_all != cntFields || posField == -1) {
            Log.i("qaq","-------------update-----更新语句字符串对应字段不存在于表中----------");
            return 4;
        }
        br.close();
        fr.close();
        if(arrStr[1].trim().equals("''")) arrStr[1] = "'"+strnull+"'";
        if(!have_power(arrSql[2].trim(), "update")) {
            Log.i("qaq","-------------update-----没有更新权限----------");
            return 6;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        fr = new FileReader("/sdcard/edbms/table/"+arrSql[1].trim()+"/content.txt");
        br = new BufferedReader(fr);
        while((curStr = br.readLine()) != null) {
            String arrCur[] = curStr.trim().split("\\s");
            if(arrCur[posField].trim().equals(arrStr[1].trim().substring(1,arrStr[1].trim().length()-1))) {
                String arrs[] = curStr.trim().split(" ");
                String curs="";
                for(int i = 0; i < arrs.length; i++) {
                    int is_have = 0;
                    for(int j = 0; j < cntFields; j++) {
                        if(curField[j] == i) {
                            is_have = 1;
                            if(i == 0) {
                                curs += val[j].trim();
                            } else {
                                curs += " " + val[j].trim();
                            }
                            break;
                        }
                    }
                    if(is_have == 0) {
                        if(i == 0) {
                            curs += arrs[i].trim();
                        } else {
                            curs += " " + arrs[i].trim();
                        }
                    }
                }
                arrayList.add(curs);
            } else {
                arrayList.add(curStr);
            }
        }
        br.close();
        fr.close();
        FileWriter fw = new FileWriter("/sdcard/edbms/table/"+arrSql[1].trim() + "/content.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        for(String s : arrayList) {
            bw.write(s+"\n");
        }
        bw.close();
        fw.close();
        refreshIndex(arrSql[1].trim());
        Log.i("qaq","-------------update-----更新成功----------");
        return 5;
    }

    /*
     * 1 语法错误
     * 2 表不存在
     * 3 字段不存在
     * string 查询成功
     * 4 没有权限
     * select * from tablename √
     * select * from tablename where dasd = 'dasd'
     * select dsasd,dsasa from tablename √
     * select dsad,dsad from dasd where dsad = 'dsada'
     */
    public String selectOnTable(String str) throws IOException {
        String ans = "";
        FileReader fr = null;
        BufferedReader br = null;
        FileWriter fw = null;
        BufferedWriter bw = null;

        String arrSql[] = str.trim().split("\\s");
        if(arrSql.length < 4) {
            Log.i("qaq","-------------select-----查找语句长度小于4----------");
            return "1";
        }
        //select * from dasd
        if(arrSql.length == 4 && arrSql[1].trim().equals("*") && arrSql[2].trim().toLowerCase().equals("from")) {
            if(!fileIOOperator.tableExistsInDB(arrSql[3].trim())) {
                Log.i("qaq","-------------select-----查找表不存在----------");
                return "2";
            } else {
                if(!have_power(arrSql[3].trim(),"select")) {
                    Log.i("qaq","-------------select-----没有查找权限----------");
                    return "4";
                }
                fr = new FileReader("/sdcard/edbms/table/"+arrSql[3].trim() + "/index.txt");
                br = new BufferedReader(fr);
                br.readLine();
                String curStr;
                while((curStr = br.readLine()) != null) {
                    String spCur[] = curStr.trim().split("\\s");
                    ans += spCur[0].trim() + "      ";
                }
                ans += "\n";
                br.close();
                fr.close();
                fr = new FileReader("/sdcard/edbms/table/" + arrSql[3].trim() + "/content.txt");
                br = new BufferedReader(fr);
                while((curStr = br.readLine()) != null) {
                    String spCur[] = curStr.trim().split("\\s");
                    for(int i = 0; i < spCur.length; i++) {
                        if(i == 0) {
                            ans += spCur[i].trim();
                        } else {
                            ans += "      " + spCur[i].trim();
                        }
                    }
                    ans +="\n";
                }
                br.close();
                fr.close();
                Log.i("qaq","-------------select-----查找成功----------");
                return ans;
            }
        } else if(arrSql.length == 4 && arrSql[1].trim().equals("*")) {
            Log.i("qaq","-------------select-----查找语句长度为4且没有'from'字符串----------");
            return "1";
            //select da,ds,dsa from tablename
        } else if(arrSql[arrSql.length-2].trim().toLowerCase().equals("from")){
            if(!fileIOOperator.tableExistsInDB(arrSql[arrSql.length-1].trim())) {
                Log.i("qaq","-------------select-----查找表不存在----------");
                return "2";
            } else {
                int st = str.trim().toLowerCase().indexOf("select"), et = str.trim().toLowerCase().indexOf("from");
                if(st == -1 || et == -1) {
                    Log.i("qaq","-------------select-----查找语句没有‘select’或‘from’----------");
                    return "1";
                } else {
                    st += 6;
                    et -= 1;
                    String substr = str.trim().substring(st, et);
                    String spSub[] = substr.trim().split(",");
                    if(spSub.length == 0) {
                        Log.i("qaq","-------------select-----查找语句需要查找的字段为空----------");
                        return "1";
                    } else {
                        fr = new FileReader("/sdcard/edbms/table/"+arrSql[arrSql.length-1].trim() + "/index.txt");
                        br = new BufferedReader(fr);
                        br.readLine();
                        String curStr;
                        int cur = 0;
                        int cnt = 0;
                        int len = spSub.length;
                        int posField[] = new int[1000];
                        while((curStr = br.readLine()) != null) {
                            String spCur[] = curStr.trim().split("\\s");
                            for(int i = 0; i < len; i++) {
                                if(spSub[i].trim().equals(spCur[0].trim())) {
                                    posField[i] = cur;
                                    cnt++;
                                }
                            }
                            cur++;
                        }
                        if(cnt != len) {
                            Log.i("qaq","-------------select-----查找语句对应字段与表中字段不符----------");
                            return "3";
                        } else {
                            for(int i = 0; i < spSub.length; i++) {
                                ans += spSub[i].trim() + "      ";
                            }
                            ans += "\n";
                            if(!have_power(arrSql[arrSql.length-1].trim(),"select")) {
                                Log.i("qaq","-------------select-----没有查找权限----------");
                                return "4";
                            }
                            fr = new FileReader("/sdcard/edbms/table/"+arrSql[arrSql.length-1].trim()+"/content.txt");
                            br = new BufferedReader(fr);
                            while((curStr = br.readLine()) != null) {
                                String spCur[] = curStr.trim().split("\\s");
                                for(int i = 0; i < len; i++) {
                                    if(i == 0) {
                                        ans += spCur[posField[i]].trim();
                                    } else {
                                        ans += "      " + spCur[posField[i]].trim();
                                    }
                                }
                                ans += "\n";
                            }
                            Log.i("qaq","-------------select-----查找成功----------");
                            return ans;
                        }
                    }
                }
            }
        } else {
            int st = str.trim().toLowerCase().indexOf("from"), et = str.trim().toLowerCase().indexOf("where");
            if(st == -1 || et == -1) {
                Log.i("qaq","-------------select-----查找语句没有‘where’字符串----------");
                return "1";
            }
            String subStr = str.trim().substring(st+5, et-1).trim();
            String spTable[] = subStr.trim().split(",");
            //select dsa, das from dsad where id='dasd'
            if(spTable.length == 1 && str.indexOf("*") == -1) {
                if(!fileIOOperator.tableExistsInDB(spTable[0].trim())) {
                    Log.i("qaq","-------------select-----查找表不存在----------");
                    return "2";
                } else {
                    int stField = str.trim().toLowerCase().indexOf("select"), etField = st;
                    String subField = str.trim().substring(stField+7, etField-1).trim();
                    String spField[] = subField.trim().split(",");
                    if(spField.length == 0) {
                        Log.i("qaq","-------------select-----需要查找的字段为空----------");
                        return "1";
                    }
                    int posWhere = str.trim().toLowerCase().indexOf("where");
                    if(posWhere == -1) {
                        Log.i("qaq","-------------select-----查找语句没有‘where’字符串----------");
                        return "1";
                    }
                    String subWhere = str.trim().substring(posWhere+6).trim();
                    String spWhere[] = subWhere.split("=");
                    if(spWhere.length != 2) {
                        Log.i("qaq","-------------select-----查找语句'where'之后语句长度不为2----------");
                        return "1";
                    }
                    int cur = 0, cnt = 0, len = spField.length;
                    int posField[] = new int[1000];
                    int posWhereId = -1;
                    String curStr;
                    fr = new FileReader("/sdcard/edbms/table/"+spTable[0].trim()+"/index.txt");
                    br = new BufferedReader(fr);
                    br.readLine();
                    while((curStr = br.readLine()) != null) {
                        String spCur[] = curStr.trim().split("\\s");
                        for(int i = 0; i < len; i++) {
                            if(spCur[0].trim().equals(spField[i].trim())) {
                                posField[i] = cur;
                                cnt++;
                            } else if(spCur[0].trim().equals(spWhere[0].trim())) {
                                posWhereId = cur;
                            }
                        }
                        cur++;
                    }
                    if(cnt != len || posWhereId == -1) {
                        Log.i("qaq","-------------select-----查找语句对应字段与表中字段不符----------");
                        return "3";
                    }
                    br.close();
                    fr.close();
                    if(!have_power(spTable[0].trim(),"select")) {
                        Log.i("qaq","-------------select-----没有查找权限----------");
                        return "4";
                    }
                    fr = new FileReader("/sdcard/edbms/table/"+spTable[0].trim()+"/content.txt");
                    br = new BufferedReader(fr);
                    for(int i = 0; i < len; i++) {
                        if(i == 0) {
                            ans += spField[i].trim();
                        } else {
                            ans += "      " + spField[i].trim();
                        }
                    }
                    ans += "\n";
                    while((curStr = br.readLine()) != null) {
                        String spCur[] = curStr.trim().split("\\s");
                        if(spCur[posWhereId].trim().equals(spWhere[1].trim().substring(1,spWhere[1].trim().length()-1))) {
                            for(int i = 0; i < len; i++) {
                                if(i == 0) {
                                    ans += spCur[posField[i]].trim();
                                } else {
                                    ans += "      " + spCur[posField[i]];
                                }
                            }
                            ans += "\n";
                        }
                    }
                    br.close();
                    fr.close();
                    Log.i("qaq","-------------select-----查找成功----------");
                    return ans;
                }
            } else if(spTable.length == 1 && str.indexOf("*") != -1) {
                if(!fileIOOperator.tableExistsInDB(spTable[0].trim())) {
                    Log.i("qaq","-------------select-----查找表不存在----------");
                    return "2";
                } else {
                    int stField = str.trim().toLowerCase().indexOf("select"), etField = st;
                    String subField = str.trim().substring(stField + 7, etField - 1).trim();
                    String spField[] = subField.trim().split(",");
                    if (spField.length == 0) {
                        Log.i("qaq", "-------------select-----需要查找的字段为空----------");
                        return "1";
                    }
                    if (!spField[0].trim().equals("*")) {
                        Log.i("qaq", "-------------select-----需要查找的字段不是‘*’----------");
                        return "1";
                    }
                    int posWhere = str.trim().toLowerCase().indexOf("where");
                    if(posWhere == -1) {
                        Log.i("qaq","-------------select-----查找语句中没有‘where’字符串----------");
                        return "1";
                    }
                    String subWhere = str.trim().substring(posWhere+6).trim();
                    String spWhere[] = subWhere.split("=");
                    if(spWhere.length != 2) {
                        Log.i("qaq","-------------select--------查找语句中‘where’后面字段长度不为2-------");
                        return "1";
                    }
                    int cur = 0;
                    int posWhereId = -1;
                    String curStr;
                    fr = new FileReader("/sdcard/edbms/table/"+spTable[0].trim()+"/index.txt");
                    br = new BufferedReader(fr);
                    br.readLine();
                    while((curStr = br.readLine()) != null) {
                        String spCur[] = curStr.trim().split("\\s");
                        if(spCur[0].trim().equals(spWhere[0].trim())) {
                            posWhereId = cur;
                        }
                        if(cur == 0) {
                            ans += spCur[0].trim();
                        } else {
                            ans += "      " + spCur[0].trim();
                        }
                        cur++;
                    }
                    ans += "\n";
                    if(posWhereId == -1) {
                        Log.i("qaq","-------------select-----查找语句中‘where’字段在表中不存在----------");
                        return "3";
                    }
                    br.close();
                    fr.close();
                    if(!have_power(spTable[0].trim(),"select")) {
                        Log.i("qaq","-------------select-----没有查找权限----------");
                        return "4";
                    }
                    fr = new FileReader("/sdcard/edbms/table/"+spTable[0].trim()+"/content.txt");
                    br = new BufferedReader(fr);
                    while((curStr = br.readLine()) != null) {
                        String spCur[] = curStr.trim().split("\\s");
                        if(spCur[posWhereId].trim().equals(spWhere[1].trim().substring(1,spWhere[1].trim().length()-1))) {
                            for(int i = 0; i < spCur.length; i++) {
                                if(i == 0) {
                                    ans += spCur[i].trim();
                                } else {
                                    ans += "      " + spCur[i].trim();
                                }
                            }
                            ans += "\n";
                        }
                    }
                    br.close();
                    fr.close();
                    Log.i("qaq","-------------select-----查找成功----------");
                    return ans;
                }
                //select * from dsad,das where dsad.ds = das.dsd
            } else if(spTable.length != 1 && str.indexOf("*") != -1) {
                if(!arrSql[1].trim().equals("*") || !arrSql[2].trim().toLowerCase().equals("from")) {
                    Log.i("qaq","-------------select-----连接查询不是'select * from'----------");
                    return "1";
                }
                for(int i = 0; i < spTable.length; i++) {
                    if(!fileIOOperator.tableExistsInDB(spTable[i].trim())) {
                        Log.i("qaq", "-------------select-----连接查询表不存在----------");
                        return "2";
                    }
                }
                int pos_where = str.trim().toLowerCase().indexOf("where");
                String subWhere = str.trim().substring(pos_where+6).trim();
                String spWhere[] = subWhere.trim().split("=");
                if(spWhere.length != 2) {
                    Log.i("qaq", "-------------select-----连接where子句错误----------");
                    return "1";
                }
                String spWhereBio[] = spWhere[0].trim().split("\\.");
                if(spWhereBio.length != 2) {
                    Log.i("qaq","-------------select-----单值键值错误----------"+spWhere[0].trim());
                    return "1";
                }
                String tableName1 = spWhereBio[0].trim();
                String tableField1 = spWhereBio[1].trim();
                if(find_fieldId(tableName1, tableField1) == -1) {
                    Log.i("qaq","-------------select-----左值表键值不存在----------");
                    return "3";
                }
                String spWhereSecond[] = spWhere[1].trim().split("\\.");
                String tableName2 = spWhereSecond[0].trim();
                String tableField2 = spWhereSecond[1].trim();
                if(find_fieldId(tableName2, tableField2) == -1) {
                    Log.i("qaq","-------------select-----右值表键值不存在----------");
                    return "3";
                }
                ArrayList<String> temarraylist = new ArrayList<>();
                String tmp = "";
                tmp = find_allField(tableName1);
                tmp += find_allField(tableName2);
                int tmpfield1 = find_fieldId(tableName1,tableField1);
                int tmpfield2 = find_fieldId(tableName2,tableField2);
                temarraylist.add(tmp);
                FileReader tmpfr = new FileReader("/sdcard/edbms/table/"+tableName1.trim()+"/content.txt");
                BufferedReader tmpbr = new BufferedReader(tmpfr);
                String tmpcur;
                while((tmpcur = tmpbr.readLine()) != null) {
                    tmp = "";
                    int is_have = 0;
                    tmp += tmpcur.trim() + " ";
                    String sptmp[] = tmpcur.trim().split("\\s");
                    FileReader tmpfr2 = new FileReader("/sdcard/edbms/table/"+tableName2.trim()+"/content.txt");
                    BufferedReader tmpbr2 = new BufferedReader(tmpfr2);
                    String tmpcur2;
                    while((tmpcur2 = tmpbr2.readLine()) != null) {
                        String sptmp2[] = tmpcur2.trim().split("\\s");
                        if(sptmp2[tmpfield2].trim().equals(sptmp[tmpfield1].trim())) {
                            tmp += tmpcur2.trim();
                            is_have = 1;
                            break;
                        }
                    }
                    tmpbr2.close();
                    tmpfr2.close();
                    if(is_have == 1) temarraylist.add(tmp);
                }
                tmpbr.close();
                tmpfr.close();
                ans = "";
                for(String s:temarraylist) {
                    ans += s + "\n";
                }
                return ans;
            } else {
                Log.i("qaq","---------select---未知查找错误----------");
                return "1";
            }
        }
    }

    /*
     * 1.语法错误
     * 2.命名不合法
     * 3.表不存在
     * 4.索引已存在
     * 5.字段不存在
     * 6.创建成功
     * create index indexname on tablename(dasd,saddas)
     */
    public int indexCreate(String str) throws IOException {
        if(str.trim().toLowerCase().indexOf("on") == -1) return 1;
        String arrSql[] = str.trim().split("\\s");

        FileReader fr = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        BufferedReader br = null;

        if(arrSql.length < 5) return 1;
        if(fileIOOperator.indexExistsInDB(arrSql[2].trim())) return 4;
        int stTable = str.indexOf("on"), enTable = str.indexOf("(");
        if(stTable > enTable) return 1;
        String subStr = str.trim().substring(stTable+3,enTable).trim();
        String tableName = subStr;
        if(!fileIOOperator.tableExistsInDB(subStr.trim())) return 3;
        String subField = str.substring(enTable+1, str.trim().length()-1);
        String spField[] = subField.trim().split(",");
        int len = spField.length;
        int cnt = 0, cur = 0;
        fr = new FileReader("/sdcard/edbms/table/"+tableName.trim()+"/index.txt");
        br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            for(int i = 0; i < len; i++) {
                if(spField[i].trim().equals(spCur[0].trim())) {
                    posFiled[i] = cur;
                    cnt++;
                }
            }
            cur++;
        }
        br.close();
        fr.close();
        if(cnt != len) return 5;
        ArrayList<String> arrayList = new ArrayList<>();
        fr = new FileReader("/sdcard/edbms/table/"+tableName.trim()+"/content.txt");
        br = new BufferedReader(fr);
        while((curStr = br.readLine()) != null) {
            arrayList.add(curStr);
        }
        br.close();
        fr.close();
        fileIOOperator.newFile("/retir/"+arrSql[2].trim());
        File file = new File("/sdcard/edbms/retir/"+arrSql[2].trim()+"/index.txt");
        file.createNewFile();
        file = new File("/sdcard/edbms/retir/"+arrSql[2].trim()+"/content.txt");
        file.createNewFile();
        fw = new FileWriter("/sdcard/edbms/retir/"+arrSql[2].trim()+"/index.txt");
        bw = new BufferedWriter(fw);
        bw.write(len+"\n");
        for(int i = 0; i < len; i++) {
            bw.write(spField[i].trim()+"\n");
        }
        bw.close();
        fw.close();
        Collections.sort(arrayList,new InexSortComparator());
        fw = new FileWriter("/sdcard/edbms/retir/"+arrSql[2].trim()+"/index.txt");
        bw = new BufferedWriter(fw);
        bw.write(spField.length+"\n");
        for(int i = 0; i < len; i++) {
            bw.write(spField[i].trim()+"\n");
        }
        bw.close();
        fw.close();
        fw = new FileWriter("/sdcard/edbms/retir/"+arrSql[2].trim()+"/content.txt");
        bw = new BufferedWriter(fw);
        for(String s:arrayList) {
            bw.write(s+"\n");
        }
        bw.close();
        fw.close();
        fw = new FileWriter("/sdcard/edbms/db/"+ EDBMSConf.getInstance().dbName.trim()+"/retirindex.txt",true);
        bw = new BufferedWriter(fw);
        bw.write(arrSql[2].trim()+"\n");
        bw.close();
        fw.close();
        fw = new FileWriter("/sdcard/edbms/table/"+tableName.trim()+"/retir.txt",true);
        bw = new BufferedWriter(fw);
        bw.write(arrSql[2].trim()+"\n");
        bw.close();
        fw.close();
        return 6;
    }

    /*
     * 1.语法错误
     * 2 表不存在
     * 3 用户不存在
     * 4 没有授予权限
     * 5 授予成功
     * GRANT INSERT,update,delete,select ON films TO PUBLIC;
     */
    public int grant(String str) throws IOException {
        String arrSql[] = str.trim().split("\\s");
        if(arrSql.length < 6) return 1;
        if(str.toLowerCase().indexOf("on") == -1 || str.toLowerCase().indexOf("to") == -1) return 1;
        int spower = str.trim().toLowerCase().indexOf("grant"), epower = str.trim().toLowerCase().indexOf("on");
        if(spower > epower) return 1;
        String subStr = str.substring(spower+6, epower-1).trim();
        String spPower[] = subStr.trim().split(",");
        if(spPower.length < 1 || spPower.length > 4) return 1;
        for(String s:spPower) {
            if(s.trim().toLowerCase().equals("insert"));
            else if(s.trim().toLowerCase().equals("update"));
            else if(s.trim().toLowerCase().equals("delete"));
            else if(s.trim().toLowerCase().equals("select"));
            else return 1;
        }
        int stb = epower, etb = str.trim().toLowerCase().indexOf("to");
        String tableName = str.trim().substring(stb+3,etb-1).trim();
        if(!fileIOOperator.tableExistsInDB(tableName)) return 2;
        String userName = str.trim().substring(etb+3).trim();
        FileReader fr = new FileReader("/sdcard/edbms/user/user.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        int is_have = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(userName.equals(spCur[0].trim())) {
                is_have = 1;
                break;
            }
        }
        br.close();
        fr.close();
        if(is_have == 0) return 3;
        fr = new FileReader("/sdcard/edbms/user/grant.txt");
        br = new BufferedReader(fr);
        is_have = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(EDBMSConf.getInstance().username) && spCur[1].trim().equals(tableName.trim())) {
                is_have = 1;
                break;
            }
        }
        br.close();
        fr.close();
        if(is_have == 0) return 4;
        FileWriter fw = new FileWriter("/sdcard/edbms/user/power.txt",true);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i = 0; i < spPower.length; i++) {
            bw.write(userName+" "+ tableName+" "+spPower[i].trim()+"\n");
        }
        bw.close();
        fw.close();
        return 5;
    }

    /*
     * 1.语法错误
     * 2.表不存在
     * 3.用户不存在
     * 4.没有收回权限
     * 5.收回成功
     * 6.对方为超级管理员，收回失败！
     * revoke select，insert,delete,update on sc from public
     */
    public int revoke(String str) throws IOException {
        String arrSql[] = str.trim().split("\\s");
        if(arrSql.length < 6) return 1;
        if(str.toLowerCase().indexOf("on") == -1 || str.toLowerCase().indexOf("from") == -1) return 1;
        int spower = str.trim().toLowerCase().indexOf("revoke"), epower = str.trim().toLowerCase().indexOf("on");
        if(spower > epower) return 1;
        String subStr = str.substring(spower+7, epower-1).trim();
        String spPower[] = subStr.trim().split(",");
        if(spPower.length < 1 || spPower.length > 4) return 1;
        for(String s:spPower) {
            if(s.trim().toLowerCase().equals("insert"));
            else if(s.trim().toLowerCase().equals("update"));
            else if(s.trim().toLowerCase().equals("delete"));
            else if(s.trim().toLowerCase().equals("select"));
            else return 1;
        }
        int stb = epower, etb = str.trim().toLowerCase().indexOf("from");
        String tableName = str.trim().substring(stb+3,etb-1).trim();
        if(!fileIOOperator.tableExistsInDB(tableName)) return 2;

        String userName = str.trim().substring(etb+5).trim();
        FileReader fr = new FileReader("/sdcard/edbms/user/user.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        int is_have = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(userName.equals(spCur[0].trim())) {
                is_have = 1;
                break;
            }
        }
        br.close();
        fr.close();
        if(is_have == 0) return 3;

        fr = new FileReader("/sdcard/edbms/user/grant.txt");
        br = new BufferedReader(fr);
        is_have = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(EDBMSConf.getInstance().username) && spCur[1].trim().equals(tableName.trim())) {
                is_have = 1;
                break;
            }
        }
        br.close();
        fr.close();
        if(is_have == 0) return 4;

        fr = new FileReader("/sdcard/edbms/user/super.txt");
        br = new BufferedReader(fr);
        is_have = 0;
        while((curStr = br.readLine()) != null) {
            if(curStr.trim().equals(userName.trim())) {
                is_have = 1;
                break;
            }
        }
        br.close();
        fr.close();
        if(is_have == 1) return 6;
        ArrayList<String> arrayList = new ArrayList<>();
        fr = new FileReader("/sdcard/edbms/user/power.txt");
        br = new BufferedReader(fr);
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            int have_power = 0;
            if(spCur[0].trim().equals(userName) && spCur[1].trim().equals(tableName)) {
                for(int i = 0; i < spPower.length; i++) {
                    if(spCur[2].trim().equals(spPower[i].trim())) {
                        have_power = 1;
                        break;
                    }
                }
                if(have_power == 0) {
                    arrayList.add(curStr.trim());
                    continue;
                }
            }
            else {
                arrayList.add(curStr.trim());
            }
        }
        br.close();
        fr.close();
        FileWriter fw = new FileWriter("/sdcard/edbms/user/power.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        for(String s:arrayList) {
            bw.write(s+"\n");
        }
        bw.close();
        fw.close();
        return 5;
    }

    //show database
    public String showDatabase() throws IOException {
        File file = new File("/sdcard/edbms/db/");
        File arrFile[] = file.listFiles();
        String nameFile[] = file.list();
        String showStr = "";
        for (int i = 0; i < arrFile.length; i++) {
            showStr += "Database: " + nameFile[i] + "\n";
            showStr += "--table:\n";
            FileReader fr = new FileReader("/sdcard/edbms/db/" + nameFile[i] + "/dbindex.txt");
            BufferedReader bf = new BufferedReader(fr);
            String tableName;
            while ((tableName = bf.readLine()) != null) {
                showStr += "        " + tableName + "\n";
            }
            bf.close();
            fr.close();
            showStr += "--index:\n";
            fr = new FileReader("/sdcard/edbms/db/" + nameFile[i] + "/retirindex.txt");
            bf = new BufferedReader(fr);
            String indexName;
            while ((indexName = bf.readLine()) != null) {
                showStr += "        " + indexName + "\n";
            }
            bf.close();
            fr.close();
        }
        return showStr;
    }

    /*
     * show table tablename
     * return string
     * -1 table 不存在
     * -2 命令错误
     */
    public String showTable(String sql) throws IOException {
        String ans = "属性      类型      约束\n";
        String arrSql[] = sql.split("\\s");
        if (arrSql.length < 3) return "-2";
        if (!fileIOOperator.tableExistsInDB(arrSql[2].trim())) return "-1";
        else {
            FileReader fr = new FileReader("/sdcard/edbms/table/" + arrSql[2].trim() + "/index.txt");
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String field;
            while ((field = br.readLine()) != null) {
                ans += field + "\n";
            }
        }
        return ans;
    }

    /*
     * 1 索引不存在
     * 2 命令不合法
     * 3 返回str
     * help index indexname
     */
    public String showIndex(String str) throws IOException {
        String arrSql[] = str.trim().split("\\s");
        if(arrSql.length <= 2 || arrSql.length > 3) return "2";
        if(!fileIOOperator.indexExistsInDB(arrSql[2].trim())) return "1";
        String ans = "索引属性: ";
        FileReader fr = new FileReader("/sdcard/edbms/retir/"+arrSql[2].trim()+"/index.txt");
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        while((curStr = br.readLine()) != null) {
            ans += "     " + curStr.trim();
        }
        ans += "\n";
        br.close();
        fr.close();
        return ans;
    }

    //判断字符串为数字
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //检查insert语句输入值的格式
    public boolean checkType(String type, String value) {
        if (type.substring(0, 3).equals("var")) {
            int len = Integer.valueOf(type.substring(8, type.length() - 1));
            if (value.length() > len) return false;
            else return true;
        } else if (type.substring(0, 3).equals("int")) {
            return isNumeric(value);
        } else if (type.substring(0, 3).equals("cha")) {
            int len = Integer.valueOf(type.substring(5, type.length() - 1));
            if (value.length() > len) return false;
            else return true;
        }
        return false;
    }

    //删除table内容
    public void deleteTableContext(String tablename) throws IOException {
        FileWriter fw = new FileWriter("/sdcard/edbms/table/"+tablename+"/content.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.close();
        fw.close();
    }

    //更新索引内容
    public void refreshIndex(String tableName) throws IOException {
        FileReader fr = null;
        FileWriter fw = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        ArrayList<String> arrayList = new ArrayList<>();
        fr = new FileReader("/sdcard/edbms/table/"+tableName.trim()+"/content.txt");
        br = new BufferedReader(fr);
        String curStr;
        while((curStr = br.readLine()) != null) {
            arrayList.add(curStr);
        }
        br.close();
        fr.close();
        String field[] = new String[1000];
        fr = new FileReader("/sdcard/edbms/table/"+tableName.trim()+"/index.txt");
        br = new BufferedReader(fr);
        br.readLine();
        int cur = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            field[cur++] = spCur[0].trim();
        }
        br.close();
        fr.close();

        fr = new FileReader("/sdcard/edbms/table/"+tableName.trim()+"/retir.txt");
        br = new BufferedReader(fr);
        String indexName;
        while ((indexName = br.readLine()) != null) {
            FileReader indexFr = new FileReader("/sdcard/edbms/retir/"+indexName.trim()+"/index.txt");
            BufferedReader indexBr = new BufferedReader(indexFr);
            indexBr.readLine();
            String indexCur;
            int indexc = 0;
            while((indexCur = indexBr.readLine()) != null) {
                for(int i = 0; i < cur; i++) {
                    if(field[i].trim().equals(indexCur.trim())) {
                        posFiled[indexc] = i;
                        break;
                    }
                }
                indexc++;
            }
            indexBr.close();
            indexFr.close();
            Collections.sort(arrayList,new InexSortComparator());
            fw = new FileWriter("/sdcard/edbms/retir/"+indexName.trim()+"/content.txt");
            bw = new BufferedWriter(fw);
            bw.write("");
            for(String s:arrayList) {
                bw.write(s+"\n");
                Log.i("aaa",s);
            }
            bw.close();
            fw.close();
        }
        br.close();
        fr.close();
    }

    public boolean have_power(String tablename, String power) throws  IOException{
        boolean have_power = false;
        FileReader fr = new FileReader("/sdcard/edbms/user/super.txt");
        BufferedReader bf = new BufferedReader(fr);
        String user;
        while((user = bf.readLine()) != null) {
            if(user.trim().equals(EDBMSConf.getInstance().username)) {
                have_power = true;
                break;
            }
        }
        bf.close();
        fr.close();
        fr = new FileReader("/sdcard/edbms/user/grant.txt");
        bf = new BufferedReader(fr);
        while((user = bf.readLine()) != null) {
            String spUser[] = user.split("\\s");
            if(spUser[0].trim().equals(EDBMSConf.getInstance().username) && spUser[1].trim().equals(tablename.trim())) {
                have_power = true;
                break;
            }
        }
        bf.close();
        fr.close();
        fr = new FileReader("/sdcard/edbms/user/power.txt");
        bf = new BufferedReader(fr);
        while((user = bf.readLine()) != null) {
            String spUser[] = user.split("\\s");
            if(spUser[0].trim().equals(EDBMSConf.getInstance().username) && spUser[1].trim().equals(tablename.trim()) &&
                    spUser[2].trim().equals(power.trim())) {
                have_power = true;
                break;
            }
        }
        bf.close();
        fr.close();
        return have_power;
    }

    //字段可以非空
    public boolean can_null(String tablename, String field) throws  IOException{
        FileReader fr = new FileReader("/sdcard/edbms/table/"+tablename.trim()+"/index.txt");
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(field.trim())) {
                if(curStr.toLowerCase().indexOf("not null") != -1 || curStr.toLowerCase().indexOf("primary")!= -1 ||
                        curStr.toLowerCase().indexOf("unique")!= -1 || curStr.toLowerCase().indexOf("foreign") != -1) {
                    br.close();
                    fr.close();
                    return false;
                }
                break;
            }
        }
        br.close();
        fr.close();
        return true;
    }

    //字段可以更改
    public boolean can_change(String tablename, String field) throws IOException{
        FileReader fr = new FileReader("/sdcard/edbms/user/map.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(tablename.trim()) && spCur[1].trim().equals(field.trim())) {
                br.close();
                fr.close();
                return false;
            }
        }
        br.close();
        fr.close();
        return true;
    }

    //是否有唯一约束
    public boolean have_unique(String tablename, String field) throws IOException{
        FileReader fr = new FileReader("/sdcard/edbms/table/"+tablename.trim()+"/index.txt");
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(field.trim())) {
                if(curStr.toLowerCase().indexOf("primary") != -1 || curStr.toLowerCase().indexOf("unique") != -1) {
                    br.close();
                    fr.close();
                    return true;
                }
                break;
            }
        }
        br.close();
        fr.close();
        return false;
    }

    //插入是否唯一
    public boolean is_unique(String tablename, int fieldId, String content) throws IOException{
        FileReader fr = new FileReader("/sdcard/edbms/table/"+tablename.trim()+"/content.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[fieldId].trim().equals(content.trim())) {
                br.close();
                fr.close();
                return false;
            }
        }
        br.close();
        fr.close();
        return true;
    }

    //内容可以删除
    public boolean can_delete(String tablename) throws IOException{
        FileReader fr = new FileReader("/sdcard/edbms/user/map.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[0].trim().equals(tablename.trim())) {
                br.close();
                fr.close();
                return false;
            }
        }
        br.close();
        fr.close();
        return true;
    }

    //查询序号对应字段
    public String find_field(String tablename, int fieldId) throws  IOException{
        FileReader fr = new FileReader("/sdcard/edbms/table/"+tablename.trim()+"/index.txt");
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        int cur = 0;
        String ans="";
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(cur == fieldId) {
                ans = spCur[0].trim();
                br.close();
                fr.close();
                return ans;
            }
            cur++;
        }
        br.close();
        fr.close();
        return "-1";
    }

    //查询字段对应序号
    public int find_fieldId(String tablename, String field) throws  IOException{
        FileReader fr = new FileReader("/sdcard/edbms/table/"+tablename.trim()+"/index.txt");
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String curStr;
        int cur = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(field.trim().equals(spCur[0].trim())) {
                br.close();
                fr.close();
                return cur;
            }
            cur++;
        }
        br.close();
        fr.close();
        return -1;
    }

    //被参照表中是否有参照表阻断对应元组
    public boolean is_reference(String tablename, String field, String content) throws IOException {
        FileReader fr = new FileReader("/sdcard/edbms/user/map.txt");
        BufferedReader br = new BufferedReader(fr);
        String curStr;
        int is_have = 0;
        int is_ref = 0;
        while((curStr = br.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            if(spCur[2].trim().equals(tablename) && spCur[3].trim().equals(field)){
                Log.i("qaq","正在检索被参照表："+spCur[0].trim());
                is_have = 1;
                int fieldId = find_fieldId(spCur[0].trim(), spCur[1].trim());
                FileReader curfr = new FileReader("/sdcard/edbms/table/"+spCur[0].trim()+"/content.txt");
                BufferedReader curbr = new BufferedReader(curfr);
                String curs;
                is_ref = 0;
                while((curs = curbr.readLine()) != null) {
                    String spcurs[] = curs.trim().split("\\s");
                    if(spcurs[fieldId].trim().equals(content)) {
                        is_ref = 1;
                        break;
                    }
                }
                if(is_ref == 0) {
                    Log.i("qaq","被参照表："+spCur[0].trim()+"不包含字段"+fieldId+"内容为："+content);
                    br.close();
                    fr.close();
                    return false;
                }
            }
        }
        br.close();
        fr.close();
        if(is_have == 0 || is_ref == 1) return true;
        return false;
    }

    //查找表的所有字段
    public String find_allField(String tablename) throws IOException{
        FileReader fr = new FileReader("/sdcard/edbms/table/"+tablename.trim()+"/index.txt");
        BufferedReader bf = new BufferedReader(fr);
        String ans = "";
        bf.readLine();
        String curStr;
        while((curStr = bf.readLine()) != null) {
            String spCur[] = curStr.trim().split("\\s");
            ans += spCur[0].trim()+" ";
        }
        bf.close();
        fr.close();
        return ans;
    }
}

