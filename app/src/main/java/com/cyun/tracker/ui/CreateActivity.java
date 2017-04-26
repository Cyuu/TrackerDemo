package com.cyun.tracker.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.cyun.tracker.R;
import com.cyun.tracker.app.MyApplication;
import com.cyun.tracker.base.BaseActivity;
import com.cyun.tracker.bean.TrackBean;
import com.cyun.tracker.db.DBManager;
import com.cyun.tracker.event.RefreshListEvent;
import com.cyun.tracker.service.LocationService;
import com.cyun.tracker.util.DialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;


/**
 * 创建新笔记
 * ----
 * 尚无草稿功能
 */
public class CreateActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_rest)
    TextView tv_rest;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.iv_image)
    ImageView iv_image;

    @BindView(R.id.iv_del_img)
    ImageView iv_del_img;

    @BindView(R.id.tv_addr)
    TextView tv_addr;

    private int MAX_NUM = 500;

    private TrackBean mTrackBean = new TrackBean();

    private LocationService locationService;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_create);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        toolbar.canShowOverflowMenu();
        iv_image.setOnClickListener(this);
        iv_del_img.setOnClickListener(this);

//        et_content.setText("要说点什么呢？");
//        et_content.setSelection(et_content.length() - 1);

        iv_del_img.setVisibility(View.GONE);

//        setTTF(et_content);

        //设置返回键可用，如果某个页面想隐藏掉返回键比如首页，可以调用mToolbar.setNavigationIcion(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        et_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_date.setText(getToday());

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //只要编辑框内容有变化就会调用该方法，s为编辑框变化后的内容
                Log.i("onTextChanged", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 编辑框内容变化之前会调用该方法，s为编辑框内容变化之前的内容
                Log.i("beforeTextChanged", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
                Log.i("afterTextChanged", s.toString());
                if (s.length() > MAX_NUM) {
                    s.delete(MAX_NUM, s.length());
                }
                int num = MAX_NUM - s.length();
//                tv_rest.setText(String.valueOf(num));
                tv_rest.setText("已输入" + s.length() + "字");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_pic:
                choosePicture();
                break;
            case R.id.action_loc:
                DialogUtil.editDialog(context, "输入位置信息",
                        new DialogUtil.EditDialogSureListener() {
                            @Override
                            public void getContent(String content) {
                                tv_addr.setText(content);
                            }
                        });

                break;
            case R.id.action_done:
                createTrack();
                break;
        }

        return true;
    }

    private void createTrack() {

        String content = et_content.getText().toString().trim();
        mTrackBean.setContent(content);

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mTrackBean.setDate(month + "." + day);

        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        mTrackBean.setTime(hour + "." + minute);

        String addr = tv_addr.getText().toString().trim();
        if (TextUtils.isEmpty(addr) || addr.equals(getResources().getString(R.string.str_locating))) {
            addr = "来无影去无踪，找不到小主啊~";
        }
        mTrackBean.setAddr(addr);

        mTrackBean.setTimeStamp(System.currentTimeMillis());
        DBManager.getInstance().insertTrackBean(mTrackBean);

        EventBus.getDefault().postSticky(new RefreshListEvent());

        showToast("小主，您有了一条新的记录哦~");
        finish();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_image:
                choosePicture();
                break;
            case R.id.iv_del_img:
                if (mTrackBean != null && !TextUtils.isEmpty(mTrackBean.getPic())) {
                    iv_image.setImageBitmap(null);
                    iv_image.setBackgroundResource(R.drawable.ic_png);
                    mTrackBean.setPic("");
                    iv_del_img.setVisibility(View.GONE);
                }
                break;
            default:

                break;
        }

    }


    private static final int CODE_CHOOSE_PIC = 0x1;
    private static final int CODE_CHOOSE_PIC_KITKAT = 0x2;

    private void choosePicture() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT); // ACTION_OPEN_DOCUMENT
        /* 取得相片后返回本画面 */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            startActivityForResult(intent, CODE_CHOOSE_PIC); // CODE_CHOOSE_PIC_KITKAT
        } else {
            startActivityForResult(intent, CODE_CHOOSE_PIC);
        }

    }

    public String getToday() {
        String value = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        value = sdf.format(date);
        return value;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_CHOOSE_PIC) {

                // 方法1 获取路径
                Uri uri = data.getData();
                String path = getImagePathFromURI(uri);

//                // 第二种方式去读取路径,不适合华为
//                ContentResolver cr = this.getContentResolver();
//                Cursor cursor = cr.query(uri, null, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                String path = cursor.getString(column_index);


                Log.i(TAG, "图片的路径" + path);
                mTrackBean.setPic(path);

                try {
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    iv_image.setImageBitmap(bmp);
                    iv_del_img.setVisibility(View.VISIBLE);
//                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//                    iv_image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CODE_CHOOSE_PIC_KITKAT) {

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 根据URI获取路径
     * @param uri
     * @return
     */
    public String getImagePathFromURI(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }


    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        locationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK

    }


    /**
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer buffer = new StringBuffer();
//                buffer.append(location.getCity());
//                buffer.append(location.getDistrict());
                buffer.append(location.getAddrStr()); // 完整的地址信息
                final String value = buffer.toString();
                Log.i(TAG, value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_addr.setText(value);
                    }
                });

            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };


}
