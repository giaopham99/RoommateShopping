package cs.uga.edu.finalproject;

public class User {
    String username;
    String email;
    double paid = 0;
    double amountReturned = 0;

    public User(){
        username = null;
        email = null;
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

    public void setAmountReturned(double amount){
        this.amountReturned = Math.round(amount * 100);
        this.amountReturned /= 100;
    }

    public double getAmountReturned(){return this.amountReturned;}

    public void setPaid(double owed){
        this.paid = Math.round(owed * 100);
        this.paid /= 100;
    }

    public double getPaid(){ return this.paid;}

    public String getUsername() {return username;}

    public String getEmail(){return email;}
}
