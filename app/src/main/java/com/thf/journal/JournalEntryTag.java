package com.thf.journal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class JournalEntryTag implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private int color;

    public JournalEntryTag(String name, int color){
        this.name = name;
        this.color = color;
    }

    protected JournalEntryTag(Parcel in) {
        id = in.readInt();
        name = in.readString();
        color = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(color);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JournalEntryTag> CREATOR = new Creator<JournalEntryTag>() {
        @Override
        public JournalEntryTag createFromParcel(Parcel in) {
            return new JournalEntryTag(in);
        }

        @Override
        public JournalEntryTag[] newArray(int size) {
            return new JournalEntryTag[size];
        }
    };

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
