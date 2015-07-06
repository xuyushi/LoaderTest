package com.example.xuyushi.loadertest;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import de.greenrobot.DBHelper;
import de.greenrobot.daoexample.Student;

/**
 * Created by xuyushi on 15/7/5.
 */
public class Fragment1 extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<Student>> {

    private StudentAdapter mAdapter;
    private List<Student> mStudents;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在fragment中获取context的方法getActivity().getApplicationContext()
        //设置list的Adapter
//        setListAdapter(new StudentAdapter(getActivity().getApplicationContext(),
//                R.layout.student_adpter,
//                DBHelper.getInstance(getActivity().getApplicationContext()).getStudent()));
        mStudents = DBHelper.getInstance(getActivity().getApplicationContext()).getStudent();
        mAdapter = new StudentAdapter(getActivity().getApplicationContext(),
                R.layout.student_adpter,
                mStudents);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(2, null, this);

    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id) {
//        Toast.makeText(getActivity(),
//                "You have selected " + presidents[position],
//                Toast.LENGTH_SHORT).show();
    }


    //回调函数
    @Override
    public Loader<List<Student>> onCreateLoader(int id, Bundle args) {
        return new StudentLoader(getActivity().getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<Student>> loader, List<Student> data) {
        if (null != mAdapter) {
            mStudents.clear();
            mStudents.addAll(DBHelper.getInstance(getActivity().getApplicationContext()).getStudent());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Student>> loader) {
        if (null != mAdapter) {

        }
    }
}
