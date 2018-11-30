package com.mcarving.thecloset.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ToWearItem  implements Parcelable {
    private String dateString;
    private String categoryName;
    private String clothName;
    private int clothId;
    private String imageUrl;

    public ToWearItem(String dateString,
                      String categoryName,
                      String clothName,
                      int clothId,
                      String imageUrl
                      ){
        this.dateString = dateString;
        this.categoryName = categoryName;
        this.clothName = clothName;
        this.clothId = clothId;
        this.imageUrl = imageUrl;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getClothName() {
        return clothName;
    }

    public void setClothName(String clothName) {
        this.clothName = clothName;
    }

    public int getClothId() {
        return clothId;
    }

    public void setClothId(int clothId) {
        this.clothId = clothId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ToWearItem(Parcel in){
        this.dateString = in.readString();
        this.categoryName = in.readString();
        this.clothName = in.readString();
        this.clothId = in.readInt();
        this.imageUrl = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateString);
        dest.writeString(categoryName);
        dest.writeString(clothName);
        dest.writeInt(clothId);
        dest.writeString(imageUrl);
    }

    public static final Creator<ToWearItem> CREATOR = new Creator<ToWearItem>() {
        @Override
        public ToWearItem createFromParcel(Parcel parcel) {
            return new ToWearItem(parcel);
        }

        @Override
        public ToWearItem[] newArray(int size) {
            return new ToWearItem[size];
        }
    };
}
