package com.clxk.h.easydbms.bean;

import java.util.ArrayList;

public class Index {
    private String indexname;
    private String tablename;
    private String indexField[];
    private ArrayList<String> indexContent;

    public Index() {
        indexField = new String[1000];
        indexContent = new ArrayList<>();
    }

}
