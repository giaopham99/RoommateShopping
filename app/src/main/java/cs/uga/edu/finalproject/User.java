package cs.uga.edu.finalproject;

public class User {
    String username;
    String email;
    double paid = 0;
    double owes = 0;

    public User(){
        username = null;
        email = null;
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

    public void setOwes(double owes){
        this.owes = Math.round(owes * 100);
        this.owes /= 100;
    }

    public double getOwes(){return this.owes;}

    public void setPaid(double owed){
        this.paid = Math.round(owed * 100);
        this.paid /= 100;
    }

    public double getPaid(){ return this.paid;}

    public String getUsername() {return username;}

    public String getEmail(){return email;}
}
