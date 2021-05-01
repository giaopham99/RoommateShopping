package cs.uga.edu.finalproject;

public class Item {
    String title;
    double price;
    String description;
    String boughtBy;

    public Item(){
        title ="";
        price = 0;
        description = "";
        boughtBy ="";
    }

    public Item(String title, String desc, double price) {
        this.title = title;
        this.description = desc;
        this.price = Math.round(price * 100);
        this.price /= 100;
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
        this.price = Math.round(price * 100);
        this.price /= 100;
    }

    public String getUser() {
        return title;
    }

    public void setUser(String username) { this.boughtBy = username; }
}
