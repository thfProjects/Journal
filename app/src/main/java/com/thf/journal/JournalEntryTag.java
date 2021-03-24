package com.thf.journal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class JournalEntryTag {

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
