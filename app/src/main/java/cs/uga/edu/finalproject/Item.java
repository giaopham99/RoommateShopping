package cs.uga.edu.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    String title;
    double price;
    String description;
    String boughtBy;
    boolean isPurchased;

    public Item(){
        title = "";
        price = 0;
        description = "";
        boughtBy ="";
        isPurchased = false;
    }

    public Item(String title, String desc, boolean flag) {
        this.title = title;
        this.description = desc;
        this.isPurchased = flag;
    }

    public Item(String title, String desc, double price, boolean flag) {
        this.title = title;
        this.description = desc;
        this.price = Math.round(price * 100);
        this.price /= 100;
        this.isPurchased = flag;
    }


    protected Item(Parcel in) {
        title = in.readString();
        price = in.readDouble();
        description = in.readString();
        boughtBy = in.readString();
        isPurchased = in.readByte() != 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public void setPurchased(Boolean flag) {
        this.isPurchased = flag;
    }

    public Boolean isPurchased() {
       return isPurchased;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String text) { this.title = text; }

    public String getDesc() { return description; }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        int temp = (int)price*100;
        this.price = Math.round(price * 100);
        this.price /= 100;
    }

    public String getUser() {
        return this.boughtBy;
    }

    public void setUser(String username) {
        this.boughtBy = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeDouble(price);
        dest.writeString(description);
        dest.writeString(boughtBy);
        dest.writeByte((byte) (isPurchased ? 1 : 0));
    }
}
