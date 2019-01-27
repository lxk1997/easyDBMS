package com.clxk.h.easydbms.bean;

import java.util.ArrayList;

public class Table {

    private String tablename;
    private String creater;
    private ArrayList<String> content;
    private Node field[];
    private ArrayList<String> retir;

    public Table() {
        content = new ArrayList<>();
        field = new Node[1000];
        retir = new ArrayList<>();
    }

    public class Node {
        String fieldName;
        String fieldType;
        String fieldRest;

        public Node() {
            super();
        }

        public Node(String fieldName, String fieldType,String fieldRest) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldRest = fieldRest;
        }
    }
}
