package cs.uga.edu.finalproject;

public class Item {
    String title;
    double price;
    String description;

    public Item(String title, String desc, double price) {
        this.title = title;
        this.description = desc;
        int temp = (int)price*100;
        this.price = temp/100;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String text) { this.title = title; }

    public String getDesc() { return description; }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        int temp = (int)price*100;
        this.price = temp/100;
        this.price = price;
    }
}
