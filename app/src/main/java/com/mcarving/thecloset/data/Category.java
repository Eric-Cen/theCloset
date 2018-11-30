package com.mcarving.thecloset.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

    private int id;
    private String categoryName;
    private int categoryItemCount;
    private String categoryImageUrl;

    public Category(int id,
                    String name,
                    int count,
                    String imageUrl){
        this.id = id;
        this.categoryName = name;
        this.categoryItemCount = count;
        this.categoryImageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryItemCount() {
        return categoryItemCount;
    }

    public void setCategoryItemCount(int categoryItemCount) {
        this.categoryItemCount = categoryItemCount;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Category(Parcel in){
        this.id = in.readInt();
        this.categoryName = in.readString();
        this.categoryItemCount = in.readInt();
        this.categoryImageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(categoryName);
        dest.writeInt(categoryItemCount);
        dest.writeString(categoryImageUrl);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel parcel) {
            return new Category(parcel);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
