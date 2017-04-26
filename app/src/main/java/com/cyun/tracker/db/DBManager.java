package com.cyun.tracker.db;

import android.database.sqlite.SQLiteDatabase;

import com.cyun.tracker.app.MyApplication;
import com.cyun.tracker.bean.TrackBean;
import com.cyun.tracker.gen.DaoMaster;
import com.cyun.tracker.gen.DaoSession;
import com.cyun.tracker.gen.TrackBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * GreenDao数据库管理
 */

public class DBManager {

    private final static String dbName = "tracker_db";
    private static DBManager mInstance;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private TrackBeanDao trackDao;
    private DaoMaster.DevOpenHelper openHelper;
//    private Context context;

    public DBManager() {
//        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(MyApplication.getApplication(), dbName, null);
        daoMaster = new DaoMaster(getReadableDatabase());
        daoSession = daoMaster.newSession();
    }


    /**
     * 获取单例引用
     */
    public static DBManager getInstance() {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(MyApplication.getApplication(), dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(MyApplication.getApplication(), dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    private TrackBeanDao getTrackBeanDao() {
        if (trackDao == null) {
            trackDao = daoSession.getTrackBeanDao();
        }
        return trackDao;
    }

    /**
     * 插入一条记录
     */
    public void insertTrackBean(TrackBean bean) {
        getTrackBeanDao().insert(bean);
    }

//    /**
//     * 插入一条记录
//     */
//    public void insertTrackBeanToFirst(TrackBean bean) {
//        getTrackBeanDao().
//    }


    /**
     * 删除一条记录
     */
    public void deleteTrackBean(TrackBean bean) {
        getTrackBeanDao().delete(bean);
    }


    /**
     * 删除整张表的数据
     */
    public void deleteTrackBeanAll() {
        getTrackBeanDao().deleteAll();
    }


    /**
     * 更新数据 -- 因为删除任何一条，都会导致相邻数据的相隔字段发生变化
     */
    public void modifyTrackBean(TrackBean bean) {
        getTrackBeanDao().delete(bean);
    }


    /**
     * 查询所有TrackBean列表
     */
    public List<TrackBean> queryTrackBeanList() {
        QueryBuilder<TrackBean> qb = getTrackBeanDao().queryBuilder();
        qb.orderAsc(TrackBeanDao.Properties.TimeStamp);
        List<TrackBean> list = qb.list();
        return list;
    }


    /**
     * 查询指定TrackBean列表--某天的
     */
    public List<TrackBean> queryTrackBeanListAtDay(String date) {
        QueryBuilder<TrackBean> qb = getTrackBeanDao().queryBuilder();
        qb.where(TrackBeanDao.Properties.Date.eq(date)).orderAsc(TrackBeanDao.Properties.TimeStamp);
        List<TrackBean> list = qb.list();
        return list;
    }

    /**
     * 查询指定TrackBean列表--某两天之间的
     */
    public List<TrackBean> queryTrackBeanListBetween(String date1, String date2) {
        QueryBuilder<TrackBean> qb = getTrackBeanDao().queryBuilder();
        qb.where(TrackBeanDao.Properties.Date.between(date1, date2)).orderAsc(TrackBeanDao.Properties.TimeStamp);
        List<TrackBean> list = qb.list();
        return list;
    }


    /**
     * 分页查询
     *
     * @param pagesize 每页多少个
     * @param offset   第几页
     */
    public List<TrackBean> queryTrackListPageing(int pagesize, int offset) {
        QueryBuilder<TrackBean> qb = getTrackBeanDao().queryBuilder();
        List<TrackBean> trackList = qb
                .orderDesc(TrackBeanDao.Properties.TimeStamp)
                .offset(offset * pagesize).limit(pagesize).list();
        return trackList;
    }


    /**
     * 分页查询 - 每次查询10个item
     *
     * @param offset 第几页
     */
    public List<TrackBean> queryTrackListPaingPerTen(int offset) {
        return queryTrackListPageing(10, offset);
    }


}
