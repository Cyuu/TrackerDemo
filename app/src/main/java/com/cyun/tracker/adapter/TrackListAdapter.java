package com.cyun.tracker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyun.tracker.R;
import com.cyun.tracker.bean.TrackBean;
import com.cyun.tracker.db.DBManager;
import com.cyun.tracker.util.DataUtils;
import com.cyun.tracker.util.DialogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 历史列表适配器
 */
public class TrackListAdapter extends BaseAdapter {

    private List<TrackBean> dataList = null;
    private Context context;
    private SparseArray<String> picArray = new SparseArray<String>();

    public TrackListAdapter(Context context) {
        this.context = context;
    }

    public TrackListAdapter(Context context, List<TrackBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<TrackBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<TrackBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return null == dataList ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_track, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TrackBean bean = dataList.get(position);
        holder.item_content.setText(bean.getContent());
        holder.start_date.setText(bean.getDate());
        holder.start_time.setText(bean.getTime());
        holder.item_addr.setText(bean.getAddr());

        if (position == 0) {
            holder.line_head_view.setVisibility(View.INVISIBLE);
            holder.item_during.setText(getDuring(position) + "前");
        } else {
            holder.line_head_view.setVisibility(View.VISIBLE);
            holder.item_during.setText("相隔" + getDuring(position));
        }

        if (position == dataList.size() - 1) {
            holder.line_tail_view.setVisibility(View.INVISIBLE);
        } else {
            holder.line_tail_view.setVisibility(View.VISIBLE);
        }


        holder.item_btn_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.sureDialog(context, "小主，真的不想看到我了么？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // todo 删除本地数据, 更新date，相隔时间有变化
                        DBManager.getInstance().deleteTrackBean(bean);
                        // 删除列表，更新view
                        DialogUtil.dismiss();
                        dataList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        });

        String img_url = bean.getPic();
        picArray.put(position, img_url);
        String curPic = picArray.get(position);
//        if (!TextUtils.isEmpty(curPic)) {
//            try {
//                Bitmap bmp = BitmapFactory.decodeFile(curPic);
//                holder.item_pic1.setImageBitmap(bmp);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            holder.item_pic1.setBackgroundResource(R.drawable.ic_png);
//        }

        holder.item_pic1.setTag(img_url);
        holder.item_pic1.setImageResource(R.drawable.ic_png);

        if (!TextUtils.isEmpty(img_url)) {
            final ImageView iv_final = holder.item_pic1;
            final String path = curPic;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bmp = BitmapFactory.decodeFile(path);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_final.setImageBitmap(bmp);
                        }
                    });
                }
            }).start();

            holder.item_pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.imageDialog(context, path);
                }
            });

        }

        return convertView;
    }

    private String getDuring(int currentPos) {
        TrackBean curBean = dataList.get(currentPos);
        if (currentPos <= 0) {
            return DataUtils.getDistanceTime(System.currentTimeMillis(), curBean.getTimeStamp());
        } else {
            TrackBean oldBean = dataList.get(currentPos - 1);
            return DataUtils.getDistanceTime(curBean.getTimeStamp(), oldBean.getTimeStamp());

        }
    }


    static class ViewHolder {
        @BindView(R.id.line_head_view)
        View line_head_view;        // 左侧line 头
        @BindView(R.id.line_tail_view)
        View line_tail_view;        // 左侧line 尾

        @BindView(R.id.item_pic1)
        ImageView item_pic1;         // picture

        @BindView(R.id.item_content)
        TextView item_content;      // 内容

        @BindView(R.id.start_date)
        TextView start_date;        // 开始时间 - 月.日

        @BindView(R.id.start_time)
        TextView start_time;        // 开始时间 - 时:分

        @BindView(R.id.item_addr)
        TextView item_addr;         // 地点

        @BindView(R.id.item_btn_zan)
        ImageView item_btn_zan;     // 删除

        @BindView(R.id.item_during)
        TextView item_during;       // 间隔


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
