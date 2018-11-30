package com.mcarving.thecloset.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Cloth implements Parcelable {
    private int id;
    private String name;
    private String description;
    private double price;
    private String purchaseDate;
    private String brand;
    private String size;
    private String status; // new, used, worn
    private String imageUrl; // cannot be null
    private String categoryName;


    public Cloth(int id,
                 String name,
                 String description,
                 double price,
                 String purchaseDate,
                 String brand,
                 String size,
                 String status,
                 String imageUrl,
                 String categoryName
                 ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.brand = brand;
        this.size = size;
        this.status = status;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected  Cloth(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.price = in.readDouble();
        this.purchaseDate = in.readString();
        this.brand = in.readString();
        this.size = in.readString();
        this.status = in.readString();
        this.imageUrl = in.readString();
        this.categoryName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(purchaseDate);
        dest.writeString(brand);
        dest.writeString(size);
        dest.writeString(status);
        dest.writeString(imageUrl);
        dest.writeString(categoryName);
    }

    public static final Creator<Cloth> CREATOR = new Creator<Cloth>() {
        @Override
        public Cloth createFromParcel(Parcel parcel) {
            return new Cloth(parcel);
        }

        @Override
        public Cloth[] newArray(int size) {
            return new Cloth[size];
        }
    };
}
