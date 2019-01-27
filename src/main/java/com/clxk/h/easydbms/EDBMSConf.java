package com.clxk.h.easydbms;

import android.app.Application;

public class EDBMSConf extends Application {

    public String dbName;
    public String tableName;
    public String viewName;
    public String username;
    private static EDBMSConf edbmsConf;

    public void onCreate() {
        edbmsConf = this;
        super.onCreate();
    }
    public static EDBMSConf getInstance() {
        return edbmsConf;
    }
}
