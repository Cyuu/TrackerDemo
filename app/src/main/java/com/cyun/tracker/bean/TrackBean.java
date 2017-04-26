package com.cyun.tracker.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * 轨迹类 -- 集成到GreenDao里
 */
@Entity
public class TrackBean implements Serializable {
    @Id(autoincrement = true)
    private Long id; // 必须用Long，而不能用long

    @Unique
    private String trackid; // 唯一id
    private String user;    // 用户名
    private String pwd;     // 密码
    private String date;    // 开始日期  年 月 日
    private String time;    // 开始时间  时分秒
    private long timeStamp; // 时间戳 -- 秒值

    private String content;  // 内容
    private String pic;      // 图片path,url

    private String Longitude;  // 经度
    private String Latitude;   // 维度
    private String addr;       // 地址
    public String getAddr() {
        return this.addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getLatitude() {
        return this.Latitude;
    }
    public void setLatitude(String Latitude) {
        this.Latitude = Latitude;
    }
    public String getLongitude() {
        return this.Longitude;
    }
    public void setLongitude(String Longitude) {
        this.Longitude = Longitude;
    }
    public String getPic() {
        return this.pic;
    }
    public void setPic(String pic) {
        this.pic = pic;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getPwd() {
        return this.pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getUser() {
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getTrackid() {
        return this.trackid;
    }
    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    @Generated(hash = 1293212074)
    public TrackBean(Long id, String trackid, String user, String pwd, String date,
            String time, long timeStamp, String content, String pic,
            String Longitude, String Latitude, String addr) {
        this.id = id;
        this.trackid = trackid;
        this.user = user;
        this.pwd = pwd;
        this.date = date;
        this.time = time;
        this.timeStamp = timeStamp;
        this.content = content;
        this.pic = pic;
        this.Longitude = Longitude;
        this.Latitude = Latitude;
        this.addr = addr;
    }
    @Generated(hash = 2100714331)
    public TrackBean() {
    }




}
