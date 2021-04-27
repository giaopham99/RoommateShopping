package cs.uga.edu.finalproject;

public class Item {
    String text;
    double price;

    public Item(String text, double price) {
        this.text = text;
        this.price = price;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
