package com.tabutech.recordmp4.Others;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingFile implements Parcelable {

    private String mName; // file name
    private String mFilePath; //file path
    private int mId; //id in database
    private int mLength; // length of recording in seconds
    private long mTime; // date/time of the recording


    public RecordingFile(){};


    protected RecordingFile(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
        mId = in.readInt();
        mLength = in.readInt();
        mTime = in.readLong();
    }

    public static final Creator<RecordingFile> CREATOR = new Creator<RecordingFile>() {
        @Override
        public RecordingFile createFromParcel(Parcel in) {
            return new RecordingFile(in);
        }

        @Override
        public RecordingFile[] newArray(int size) {
            return new RecordingFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmLength() {
        return mLength;
    }

    public void setmLength(int mLength) {
        this.mLength = mLength;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public static Creator<RecordingFile> getCREATOR() {
        return CREATOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mFilePath);
        dest.writeInt(mId);
        dest.writeInt(mLength);
        dest.writeLong(mTime);
    }
}
