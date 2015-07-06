# LoaderTest
title: loader 总结
date: July 5, 2015 3:57 PM
categories: android
tags: [loader,greendao,android]
description: 在listfragment使用自定义的adpter现实数据库的数据，当数据库的数据发生变化时，自动刷新
---

[TOC]
## 本文实现的功能

在listfragment使用自定义的adpter现实数据库的数据，当数据库的数据发生变化时，自动刷新

效果如下
![loader](https://raw.githubusercontent.com/xuyushi/Blog_img/master/loader1.gif)
## 用到的知识点
1. listfragment
2. [GreenDao](http://xuyushi.github.io/2015/06/29/GreenDao学习/)
3. 自定义loader
4. 自定义adapter
5. loaderManger
6. contentobserver监听数据库

## 实现过程
### 数据库的简历
不做过多分析，详见[GreenDao](http://xuyushi.github.io/2015/06/29/GreenDao学习/)
建立了student表，包括
1. id
2. name
3. age

在DBHleper中实现了以下增加，删除的方法

```java
    public void addToStudentTable(de.greenrobot.daoexample.Student student) {
        if (null == Student) {
            return;
        }
        if (null == student) {
            return;
        }
        Student.insert(student);
        if (mObserver!=null) {
            mObserver.onChange(true);
        }
    }

    public List<Student> getStudent() {
        return Student.loadAll();
    }

    public void clearStudent() {
        Student.deleteAll();
    }
```

在MainActivity的onCreat方法初始化数据库
```java
    private void init() {
        DBHelper.getInstance(this).clearStudent();

        for (int i=0;i < STUDENT_NUM;i++) {
            student = new Student();
            student.setName("testname"+i);
            student.setAge(10+i);
            DBHelper.getInstance(this).addToStudentTable(student);
        }
    }
```

在主界面中增加一个按钮，用来增加数据库，用来观测loader是否刷新List
```java
    public void add_sth(View view) {
        student = new Student();
        student.setName("createdByButton");
        student.setAge(100);
        DBHelper.getInstance(this).addToStudentTable(student);
    }
```
### 实现自定义adapter

```java
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

```

使用holder可以大大加快程序效率，避免重复加载。

### 自定义loader
```java
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

```

* onStartLoading：注册一些监听器到loader上，并且执行一次forceLoad(); 否则loader不会开始工作
* loadInBackground：加载数据并且返回，其实这个数据就返回到了回调函数中LoaderManager的onLoadFinished方法第二个data参数
* onStopLoading：停止加载数据，但不要停止监听也不要释放数据，就可以随时重启loader
* onReset：先确保已经停止加载数据了，然后释放掉监听器并设为null
* onCanceled： 在这里可以释放资源，如果是list就不需要做什么了，但是象cursor或者打开了什么文件就应该关闭一下

### fragment 
继承fragmentlist ，并实现 LoaderManager.LoaderCallbacks方法
```java

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

    }
}
```
* onLoadFinished:中的第二个参数，即loader在doinbackfround的返回值

> 注：在调试中发现虽然 mStudents 的数据刷新了，但是直接调用 mAdapter.notifyDataSetChanged();并不能刷新界面。
> 原因是:mAdapter会通过mStudents获取数据库中的内容。但是实际上可能是，在调用super(MyActivity.this, R.layout.item, usersList)时mAdapter保存了指向原数据库的引用，在调用query函数之后，mstudent指向了一个新的List（List b）。但是在调用notifyDataSetChanged()时，mAdapter会跟据保存的引用（即指向List a的引用）去更新，因此当然还是原来的结果，不会进行更新。

所以有以下修改
```java
mStudents.clear();           mStudents.addAll(DBHelper.getInstance(getActivity().getApplicationContext()).getStudent());
            mAdapter.notifyDataSetChanged();
```

### contentobserver监听数据库
通过contentobserver可以实时的更新数据库中的信息，无需去轮询。
#### 在StudentLoader中注册contentobserver

```java
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
```

#### DBHelper中
##### 定义mObserver
```java
private Loader.ForceLoadContentObserver mObserver;
```

##### 实现注册函数registerObserver
```java
    public void registerObserver(Loader<List<Student>>.ForceLoadContentObserver Observer) {
        this.mObserver = Observer;
    }
```
即把变量取出
##### 在数据库改变的地方发出通知
```java
    public void addToStudentTable(de.greenrobot.daoexample.Student student) {
        if (null == Student) {
            return;
        }
        if (null == student) {
            return;
        }
        Student.insert(student);
        if (mObserver!=null) {
            mObserver.onChange(true);
        }
    }
```

使用`mObserver.onChange(true);`发出通知

源码见 ：https://github.com/xuyushi/LoaderTest
