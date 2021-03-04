package com.thf.journal;

import android.graphics.Color;
import android.icu.text.DateFormat;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(
        entity = JournalEntryTag.class,
        parentColumns = "id",
        childColumns = "tag_id"
))
@TypeConverters(DateConverters.class)
public class JournalEntry implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(defaultValue = "")
    @NonNull
    private String text;

    @ColumnInfo
    private int tag_id;

    @ColumnInfo(defaultValue = "")
    @NonNull
    private String title;

    @ColumnInfo(defaultValue = "0")
    private boolean archived;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    private Date dateAdded;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    private Date dateEditted;

    public JournalEntry(String text, int tag_id, String title, boolean archived, Date dateAdded, Date dateEditted){
        this.text = text;
        this.tag_id = tag_id;
        this.title = title;
        this.archived = archived;
        this.dateAdded = dateAdded;
        this.dateEditted = dateEditted;
    }

    protected JournalEntry(Parcel in) {
        id = in.readInt();
        text = in.readString();
        tag_id = in.readInt();
        title = in.readString();
        archived = in.readInt() != 0;
        dateAdded = new Date(in.readLong());
        dateEditted = new Date(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeInt(tag_id);
        dest.writeString(title);
        dest.writeInt(archived? 1:0);
        dest.writeLong(dateAdded.getTime());
        dest.writeLong(dateEditted.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JournalEntry> CREATOR = new Creator<JournalEntry>() {
        @Override
        public JournalEntry createFromParcel(Parcel in) {
            return new JournalEntry(in);
        }

        @Override
        public JournalEntry[] newArray(int size) {
            return new JournalEntry[size];
        }
    };

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getArchived() {
        return archived;
    }

    public void setArchived(boolean archived){
        this.archived = archived;
    }

    @NonNull
    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(@NonNull Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @NonNull
    public Date getDateEditted() {
        return dateEditted;
    }

    public void setDateEditted(@NonNull Date dateEditted) {
        this.dateEditted = dateEditted;
    }
}
