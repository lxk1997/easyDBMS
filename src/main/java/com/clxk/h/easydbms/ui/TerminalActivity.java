package com.clxk.h.easydbms.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.clxk.h.easydbms.EDBMSConf;
import com.clxk.h.easydbms.R;
import com.clxk.h.easydbms.controller.CheckSqlGram;
import com.clxk.h.easydbms.operator.FileIOOperator;

import java.io.IOException;

public class TerminalActivity extends AppCompatActivity {

    private TextView title;
    private EditText et_terminal;
    private TextView et_content;
    private FileIOOperator fileIOOperator;
    private CheckSqlGram checkSqlGram;
    private EDBMSConf edbmsConf;
    private boolean flag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        //find
        title = (TextView)findViewById(R.id.tv_titleInTerminal);
        et_terminal = (EditText)findViewById(R.id.et_terminal);
        et_content = (TextView)findViewById(R.id.et_content);
        try {
            fileIOOperator = new FileIOOperator();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            checkSqlGram = new CheckSqlGram();
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag = false;
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.sure);
        drawable.setBounds(0,0,260,150);
        title.setCompoundDrawables(null,null,drawable,null);
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getX() >= title.getCompoundDrawables()[2].getBounds().width()) {
                    try {
                        try {
                            solveSql(et_terminal.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    void solveSql(String sql) throws InterruptedException, IOException {
        if(checkSqlGram.enable(sql) && !flag) {
            flag = true;
        }
        if(!flag) {
            et_content.setText("请键入'enable'命令启动命令模式!");
            et_terminal.setText(null);
        } else {
            if(checkSqlGram.enable(sql)) {
                et_content.setText(R.string.app_hello);
                et_terminal.setText(null);
            }
            int operator = checkSqlGram.checkSql(sql);
            switch (operator) {
                //create db
                case 1:
                    int tag_createDB = checkSqlGram.CheckDBCreate(sql);
                    switch (tag_createDB) {
                        case 0:
                            et_content.setText("语法错误!");
                            et_terminal.setText(null);
                            break;
                        case 1:
                            et_content.setText("数据库存在!");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("数据库命名不合法!");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("数据库创建成功!");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //use db
                case 2:
                    int tag_useDB = checkSqlGram.CheckDBUse(sql);
                    switch (tag_useDB) {
                        case 0:
                            et_content.setText("语法错误!");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("数据库不存在!");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            String arrsql[] = sql.split("\\s");
                            et_content.setText("正在使用数据库"+arrsql[1].trim());
                            et_terminal.setText(null);
                            edbmsConf = EDBMSConf.getInstance();
                            edbmsConf.dbName = arrsql[1];
                            break;
                    }
                    break;
                //create table
                /* 6 主键不唯一
                 * 7 外键表不存在
                 * 8 外键表对应字段不存在
                 * 9 外键表对应字段非主键
                 */
                case 3:
                    int tag_createTable = checkSqlGram.CheckTableCreate(sql);
                    switch(tag_createTable) {
                        case 1:
                            et_content.setText("表命名不合法!");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("表已存在!");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("语法错误!");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("表创建完成");
                            et_terminal.setText(null);
                            break;
                        case 6:
                            et_content.setText("主键不唯一！");
                            et_terminal.setText(null);
                            break;
                        case 7:
                            et_content.setText("外键表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 8:
                            et_content.setText("外键表对应字段不存在！");
                            et_terminal.setText(null);
                            break;
                        case 9:
                            et_content.setText("外键表对应字段非主键！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //insert to table
                case 4:
                    int tag_insertInToTable = checkSqlGram.CheckTableInsert(sql);
                    /*
                     * 1 语法错误
                     * 2 表不存在
                     * 3 输入值有误
                     * 4 值已存在
                     * 5 插入成功
                     * insert into table values ('','','')
                     */
                    switch (tag_insertInToTable) {
                        case 1:
                            et_content.setText("语法错误!");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("输入值有误！");
                            et_terminal.setText(null);
                            break;
                        case 4:
                            et_content.setText("当前值已存在！");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("插入成功！");
                            et_terminal.setText(null);
                            break;
                        case 6:
                            et_content.setText("当前用户没有insert权限！");
                            et_terminal.setText(null);
                            break;
                        case 7:
                            et_content.setText("违反非空约束，插入失败！");
                            et_terminal.setText(null);
                            break;
                        case 8:
                            et_content.setText("违反唯一约束，插入失败！");
                            et_terminal.setText(null);
                            break;
                        case 9:
                            et_content.setText("违反参照约束，插入失败！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //delete from table
                /*
                 * 1 语法错误
                 * 2 表不存在
                 * 3 值不存在
                 * 4 删除成功
                 * delete from tablename where id=nio
                 */
                case 5:
                    int tag_deleteFromTable = checkSqlGram.deleteFromTable(sql);
                    switch(tag_deleteFromTable) {
                        case 1:
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("值不存在！");
                            et_terminal.setText(null);
                            break;
                        case 4:
                            et_content.setText("删除成功！");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("当前用户没有delete权限！");
                            et_terminal.setText(null);
                            break;
                        case 6:
                            et_content.setText("该表为被参照表，删除失败！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //help database
                case 6:
                    String ans_db = checkSqlGram.showDatabase();
                    et_content.setText(ans_db);
                    et_terminal.setText(null);
                    break;
                //help table tablename
                case 7:
                    String ans_tb = checkSqlGram.showTable(sql);
                    if(ans_tb.equals("-1")) {
                        et_content.setText("表不存在！");
                        et_terminal.setText(null);
                    } else if(ans_tb.equals("-2")) {
                        et_content.setText("语法错误！");
                        et_terminal.setText(null);
                    } else {
                        et_content.setText(ans_tb);
                        et_terminal.setText(null);
                    }
                    break;
                //updata in table
                /*
                 * 1 语法错误
                 * 2 表不存在
                 * 3 值不存在
                 * 4 字段不存在
                 * 5 更新成功
                 * update tablename set id='dd',dsa='das' where ss='dsad'
                 * */
                case 8:
                    int tag_updateInTable = checkSqlGram.updateInTable(sql);
                    switch (tag_updateInTable) {
                        case 1:
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("值不存在！");
                            et_terminal.setText(null);
                            break;
                        case 4:
                            et_content.setText("字段不存在！");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("更新成功！");
                            et_terminal.setText(null);
                            break;
                        case 6:
                            et_content.setText("当前用户没有update权限！");
                            et_terminal.setText(null);
                            break;
                        case 7:
                            et_content.setText("违反非空约束，更新失败！");
                            et_terminal.setText(null);
                            break;
                        case 8:
                            et_content.setText("违反唯一约束，更新失败！");
                            et_terminal.setText(null);
                            break;
                        case 9:
                            et_content.setText("违反参照约束，更新失败！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //select查询
                /*
                 * 1 语法错误
                 * 2 表不存在
                 * 3 字段不存在
                 * string 查询成功
                 * select * from tablename
                 * select * from tablename where dasd = 'dasd'
                 * select dsasd,dsasa from tablename
                 * select dsad,dsad from dasd where dsad = 'dsada'
                 */
                case 9:
                    String ans_select = checkSqlGram.selectOnTable(sql);
                    switch(ans_select) {
                        case "1":
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        case "2":
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case "3":
                            et_content.setText("字段不存在！");
                            et_terminal.setText(null);
                            break;
                        default:
                            et_content.setText(ans_select);
                            et_terminal.setText(null);
                            break;

                    }
                    break;
                //create index indexname on tablename (dasdad, sdasd)
                /*
                 * 1.语法错误
                 * 2.命名不合法
                 * 3.表不存在
                 * 4.索引已存在
                 * 5.字段不存在
                 * 6.创建成功
                 * create index indexname on tablename(dasd,saddas)
                 */
                case 10:
                    int tag_index = checkSqlGram.indexCreate(sql);
                    switch(tag_index) {
                        case 1:
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("命名不合法！");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 4:
                            et_content.setText("索引已存在！");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("字段不存在！");
                            et_terminal.setText(null);
                            break;
                        case 6:
                            et_content.setText("创建成功！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //help index indexname
                /*
                 * 1 索引不存在
                 * 2 命令不合法
                 * 3 返回str
                 * help index indexname
                 */
                case 11:
                    String ans_index = checkSqlGram.showIndex(sql);
                    switch(ans_index) {
                        case "1":
                            et_content.setText("索引不存在！");
                            et_terminal.setText(null);
                            break;
                        case "2":
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        default:
                            et_content.setText(ans_index);
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //grant
                /*
                 * 1.语法错误
                 * 2 表不存在
                 * 3 用户不存在
                 * 4 没有授予权限
                 * 5 授予成功
                 * GRANT INSERT,update,delete,select ON films TO PUBLIC;
                 */
                case 12:
                    int tag_grant = checkSqlGram.grant(sql);
                    switch (tag_grant) {
                        case 1:
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("被授权用户不存在！");
                            et_terminal.setText(null);
                            break;
                        case 4:
                            et_content.setText("当前用户没有grant权限！");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("权限授予成功！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                //revoke
                /*
                 * 1.语法错误
                 * 2.表不存在
                 * 3.用户不存在
                 * 4.没有收回权限
                 * 5.收回成功
                 * 6.对方为超级管理员，收回失败！
                 * revoke select，insert,delete,update on sc from public
                 */
                case 13:
                    int tag_revoke = checkSqlGram.revoke(sql);
                    switch (tag_revoke) {
                        case 1:
                            et_content.setText("语法错误！");
                            et_terminal.setText(null);
                            break;
                        case 2:
                            et_content.setText("表不存在！");
                            et_terminal.setText(null);
                            break;
                        case 3:
                            et_content.setText("被收回用户不存在！");
                            et_terminal.setText(null);
                            break;
                        case 4:
                            et_content.setText("当前用户没有rovoke权限！");
                            et_terminal.setText(null);
                            break;
                        case 5:
                            et_content.setText("权限收回成功！");
                            et_terminal.setText(null);
                            break;
                        case 6:
                            et_content.setText("被收回用户为超级管理员，权限收回失败！");
                            et_terminal.setText(null);
                            break;
                    }
                    break;
                case -1:
                    et_content.setText("语法错误！");
                    et_terminal.setText(null);
                    break;
            }
        }
    }
}
