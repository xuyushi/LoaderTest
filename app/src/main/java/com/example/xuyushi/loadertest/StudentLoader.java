package com.example.xuyushi.loadertest;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.DBHelper;
import de.greenrobot.daoexample.Student;

/**
 * Created by xuyushi on 15/7/5.
 */
public class StudentLoader extends AsyncTaskLoader<List<Student>> {
    private List<Student> mdata;
    final ForceLoadContentObserver mObserver ;
    public StudentLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    @Override
    public List<Student> loadInBackground() {


        try {

            mdata =  DBHelper.getInstance(getContext()).getStudent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return mdata;
    }

    @Override
    public void deliverResult(List<Student> data) {
        mdata = data;
        if (isStarted()) {
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mdata != null) {
            deliverResult(mdata);
        }
        if (mdata == null) {
            forceLoad();
        }
        DBHelper.getInstance(getContext()).registerObserver(mObserver);
    }

    @Override
    public void onCanceled(List<Student> data) {
        super.onCanceled(data);
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mdata != null) {
            //释放资源
            mdata = null;
        }
    }
}
