package com.aviv_pos.olgats.avivinventory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by olgats on 14/12/2015.
 */
public class ItemSearchCriteria  implements Parcelable{
    private String code;
    private String name;

    public ItemSearchCriteria() {

    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
    }

    protected ItemSearchCriteria(Parcel in) {
        code = in.readString();
        name = in.readString();
    }

    public static final Creator<ItemSearchCriteria> CREATOR = new Creator<ItemSearchCriteria>() {
        @Override
        public ItemSearchCriteria createFromParcel(Parcel in) {
            return new ItemSearchCriteria(in);
        }

        @Override
        public ItemSearchCriteria[] newArray(int size) {
            return new ItemSearchCriteria[size];
        }
    };

}
