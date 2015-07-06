package de.greenrobot;

/**
 * Created by xuyushi on 15/7/5.
 */

import android.content.Context;
import android.content.Loader;

import com.example.xuyushi.loadertest.AppApplication;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Student;
import de.greenrobot.daoexample.StudentDao;

/**
 * Created by xuyushi on 15/7/5.
 */
public class DBHelper {
    private static Context mContext;
    private static DBHelper instance;
    private Loader.ForceLoadContentObserver mObserver;
//    private CustomerDao Customer;
//    private NoteDao Note;
//    private OrderDao Order;
//    private PriceDao Price;

    private StudentDao Student;

    private DBHelper() {
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (mContext == null) {
                mContext = context;
            }

            // 数据库对象
            DaoSession daoSession = AppApplication.getDaoSession(mContext);
//            instance.Customer = daoSession.getCustomerDao();
//            instance.Order = daoSession.getOrderDao();
//            instance.Note = daoSession.getNoteDao();
//            instance.Price = daoSession.getPriceDao();
            instance.Student = daoSession.getStudentDao();


        }
        return instance;
    }

    /**
     * 添加数据
     */
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

    public void registerObserver(Loader<List<Student>>.ForceLoadContentObserver Observer) {
        this.mObserver = Observer;
    }


//    /**
//     * 添加数据
//     */
//    public void addToNoteTable(Note note) {
//        if (null == Note) {
//            return;
//        }
//        if (null == note) {
//            return;
//        }
//        Note.insert(note);
//    }
//
//    public List<Note> getNote() {
//        return Note.loadAll();
//    }
//
//    public void addPriceTable(Price price) {
//        if (null == Price) {
//            return;
//        }
//        if (null == price) {
//            return;
//        }
//        Price.insert(price);
//    }
//
//    public List<Price> getPrice() {
//        return Price.loadAll();
//    }

}
