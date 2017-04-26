package com.cyun.tracker.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cyun.tracker.R;
import com.cyun.tracker.adapter.TrackListAdapter;
import com.cyun.tracker.base.BaseActivity;
import com.cyun.tracker.bean.TrackBean;
import com.cyun.tracker.db.DBManager;
import com.cyun.tracker.event.RefreshListEvent;
import com.cyun.tracker.util.DataUtils;
import com.cyun.tracker.util.VUtils;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    private Context context = this;

    @BindView(R.id.swipy_layout)
    SwipyRefreshLayout swipy_layout;
    @BindView(R.id.lv_track)
    ListView lv_track;

    TrackListAdapter trackAdapter;
    List<TrackBean> trackList = null;

    private int curPage = 0;

    // 首页申请百度地图权限
    private String permissionInfo; // 百度地图权限信息
    private final int SDK_PERMISSION_REQUEST = 127;


    @Override
    public void setContentView() {
        setContentView(R.layout.app_bar_main);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goActivity(CreateActivity.class);
//                goActivity(TestCreateActivity.class);

            }
        });

        trackAdapter = new TrackListAdapter(context);
        lv_track.setAdapter(trackAdapter);

        lv_track.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TrackBean item = trackList.get(position);
                    String content = item.getContent();
                    showSnack4Content(view, content);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        VUtils.setSwipyColor(swipy_layout);
        swipy_layout.setDirection(SwipyRefreshLayoutDirection.BOTH); // 只能从上面刷新
        swipy_layout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                switch (direction) {
                    case TOP:
                        curPage = 0;
                        break;
                    case BOTTOM:
                        curPage += 1;
                        break;
                }
                requestData(curPage);
            }
        });

        swipy_layout.setRefreshing(false);

        requestData(0);

    }

    /**
     * 查询数据 ,默认每次10个
     *
     * @param page
     */
    private void requestData(int page) {
        new QueryAsyncTask(page).execute();
    }

    @Override
    public void onClick(View v) {

    }


    private void showView() {
//       trackList = DataUtils.getTrackListTest();

        if (trackList == null || trackList.isEmpty()) {
            showToast("还没有留下任何痕迹，创建一条吧！");
        }
        trackAdapter.setDataList(trackList);
        trackAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if () {
//
//        } else {
//            super.onBackPressed();
//        }
    }


    // 右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            showToast("筛选");
        } else if (item.getItemId() == R.id.action_new) {
            addNewManul();
        } else if (item.getItemId() == R.id.action_del) {
            deleteAll();
            if (trackList != null) {
                trackList.clear();
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    private void addNewManul() {
        List<TrackBean> list = DataUtils.getTrackListTest();
        for (TrackBean item : list) {
            DBManager.getInstance().insertTrackBean(item);
        }
        showToast("已增加");
        new QueryAsyncTask(0).execute();
    }


    private void deleteAll() {
        DBManager.getInstance().deleteTrackBeanAll();
        showToast("已清空");
        showView();
    }


    public void goActivity(Class<?> clz) {
        Intent intent = new Intent(this, clz);
        startActivity(intent);

    }


    /**
     * 查询列表的异步任务
     */
    class QueryAsyncTask extends AsyncTask<Integer, Integer, List<TrackBean>> {
        int page;

        public QueryAsyncTask(int page) {
            super();
            this.page = page;
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数
         * 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected List<TrackBean> doInBackground(Integer... params) {
            List<TrackBean> temp = DBManager.getInstance().queryTrackListPaingPerTen(page);
            if (page == 0) {
                trackList = temp;
            } else {
                if (trackList == null || trackList.isEmpty()) {
                    trackList = temp;
                } else {
                    trackList.addAll(temp);
                }
            }
            return trackList;
        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            int vlaue = values[0];
        }


        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(List<TrackBean> list) {
            showView();
            swipy_layout.setRefreshing(false);
        }

    }

    /**
     * 推送处理：有新内容
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void dealListEvent(RefreshListEvent event) {
        requestData(0);
    }

}
