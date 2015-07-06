package com.example.xuyushi.loadertest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.daoexample.Student;

/**
 * Created by xuyushi on 15/7/5.
 */
public class StudentAdapter extends BaseAdapter {
    private int mresourceId; //对应的xml布局文件，也可以不传出来，在getView中写死。
    private Context mcontext;
    private List<Student> mdata;

    public StudentAdapter(Context context, int resourceId, List<Student> data) {
        super();
        mresourceId = resourceId;
        mcontext = context;
        mdata = data;
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView用于将之前加载好的view缓存
        ViewHolder holder = null;
        View view;
        Student student = (Student) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            view = LayoutInflater.from(mcontext).inflate(mresourceId, null);
            holder.id = (TextView) view.findViewById(R.id.id);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.age = (TextView) view.findViewById(R.id.age);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.id.setText("id" + student.getId().toString());
        holder.name.setText("name" + student.getName());
        holder.age.setText("age" + student.getAge());


        return view;
    }

    public class ViewHolder {
        TextView id;
        TextView name;
        TextView age;
    }
}
