package com.clxk.h.easydbms.utils;

import com.clxk.h.easydbms.controller.CheckSqlGram;

import java.util.Comparator;

public class InexSortComparator implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        int posField[] = CheckSqlGram.posFiled;
        String s1 = o.toString();
        String s2 = t1.toString();
        String spS1[] = s1.trim().split("\\s");
        String spS2[] = s2.trim().split("\\s");
        String ans1 = "", ans2 = "";
        for(int i = 0; i < posField.length; i++) {
            ans1 += spS1[posField[i]].trim();
            ans2 += spS2[posField[i]].trim();
        }
        return ans1.compareTo(ans2);
    }
}
