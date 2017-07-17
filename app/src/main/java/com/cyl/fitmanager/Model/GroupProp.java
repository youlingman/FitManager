package com.cyl.fitmanager.Model;

/**
 * 对训练计划数据的封装
 * size为组数
 * count为每组的计划数
 * interval为组间休息时间，以秒为单位
 * trainingDayBitMap保存了每周训练日对应的七位bitmap，最低位为周日
 * Created by Administrator on 2016-2-18.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class GroupProp implements Parcelable {
    public int size;
    public int count;
    public int interval;
    public int trainingDayBitMap;

    public GroupProp() {
        this.size = 3;
        this.count = 10;
        this.interval = 30;
        // 默认训练日为周一、周二、周四和周六
        this.trainingDayBitMap = 2 + 4 + 16 + 64;
    }

    public GroupProp(int size, int count, int interval, int trainingDayBitMap) {
        this.size = size;
        this.count = count;
        this.interval = interval;
        this.trainingDayBitMap = trainingDayBitMap;
    }

    private GroupProp(Parcel in) {
        size = in.readInt();
        count = in.readInt();
        interval = in.readInt();
        trainingDayBitMap = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GroupProp createFromParcel(Parcel in) {
            return new GroupProp(in);
        }

        public GroupProp[] newArray(int size) {
            return new GroupProp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size);
        dest.writeInt(count);
        dest.writeInt(interval);
        dest.writeInt(trainingDayBitMap);
    }
}
