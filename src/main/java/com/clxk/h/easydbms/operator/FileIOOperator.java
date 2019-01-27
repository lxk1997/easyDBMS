package com.clxk.h.easydbms.operator;

import com.clxk.h.easydbms.EDBMSConf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIOOperator {

    public FileIOOperator() throws IOException {
        File mFile = new File("/sdcard/edbms/table/");
        if(!mFile.exists()) mFile.mkdirs();
        mFile = new File("/sdcard/edbms/view/");
        if(!mFile.exists()) mFile.mkdirs();
        mFile = new File("/sdcard/edbms/retir/");
        if(!mFile.exists()) mFile.mkdirs();
        mFile = new File("/sdcard/edbms/db/");
        if(!mFile.exists()) mFile.mkdirs();
        mFile = new File("/sdcard/edbms/user/");
        if(!mFile.exists()) mFile.mkdirs();
        mFile = new File("/sdcard/edbms/user/simple.txt");
        if(!mFile.exists()) mFile.createNewFile();
        mFile = new File("/sdcard/edbms/user/super.txt");
        if(!mFile.exists()) mFile.createNewFile();
        mFile = new File("/sdcard/edbms/user/user.txt");
        if(!mFile.exists()) mFile.createNewFile();
        mFile = new File("/sdcard/edbms/user/power.txt");
        if(!mFile.exists()) mFile.createNewFile();
        mFile = new File("/sdcard/edbms/user/dbcreate.txt");
        if(!mFile.exists()) mFile.createNewFile();
        mFile = new File("/sdcard/edbms/user/grant.txt");
        if(!mFile.exists()) mFile.createNewFile();
        mFile = new File("/sdcard/edbms/user/map.txt");
        if(!mFile.exists()) mFile.createNewFile();
    }

    public boolean is_exists(String path) {
        File mFile = new File("/sdcard/edbms"+path.toString());
        return mFile.exists();
    }

    public void newFile(String path) {
        File mFile = new File("/sdcard/edbms"+path.toString().trim());
        if(!mFile.exists()) mFile.mkdirs();
    }

    public boolean tableExistsInDB(String tableName) throws IOException {
        EDBMSConf edbmsConf = EDBMSConf.getInstance();
        String db = edbmsConf.dbName;
        File mFile = new File("/sdcard/edbms/db/" + db + "/dbindex.txt");
        FileReader fileReader = new FileReader("/sdcard/edbms/db/" + db + "/dbindex.txt");
        BufferedReader reader = new BufferedReader(fileReader);
        String tname;
        while((tname = reader.readLine()) != null) {
            if(tname.equals(tableName)){
                return true;
            }
        }
        fileReader.close();
        reader.close();
        return false;
    }

    public boolean indexExistsInDB(String indexName) throws IOException {
        File file = new File("/sdcard/edbms/retir/"+indexName.trim()+"/index.txt");
        return file.exists();
    }

    public boolean tableInDB(String tableName) throws IOException {
        String db = EDBMSConf.getInstance().dbName;
        newFile("/table/" + tableName);
        File mfile = new File("/sdcard/edbms/table/" + tableName + "/index.txt");
        mfile.createNewFile();
        mfile = new File("/sdcard/edbms/table/" + tableName + "/content.txt");
        mfile.createNewFile();
        mfile = new File("/sdcard/edbms/table/" + tableName + "/retir.txt");
        mfile.createNewFile();
        //File mFile = new File("/sdcard/edbms/db/" + db + "/dbindex.txt");
        FileWriter fw = new FileWriter("/sdcard/edbms/db/" + db + "/dbindex.txt",true);
        BufferedWriter bf = new BufferedWriter(fw);
        bf.write(tableName + "\n");
        bf.close();
        fw.close();
        return true;
    }

}
