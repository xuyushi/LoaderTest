package com.example.xuyushi.loadertest;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import de.greenrobot.DBHelper;
import de.greenrobot.daoexample.Student;


public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";
    private  int STUDENT_NUM = 20;

    private Student student;
    private List<Student> ListStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        ListStudent = DBHelper.getInstance(this).getStudent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void init() {
        DBHelper.getInstance(this).clearStudent();

        for (int i=0;i < STUDENT_NUM;i++) {
            student = new Student();
            student.setName("testname"+i);
            student.setAge(10+i);
            DBHelper.getInstance(this).addToStudentTable(student);
        }
    }

    public void add_sth(View view) {
        student = new Student();
        student.setName("createdByButton");
        student.setAge(100);
        DBHelper.getInstance(this).addToStudentTable(student);
    }
}
