package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView welcomeText;
    Button signoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        signoutButton = findViewById(R.id.signoutButton);
        welcomeText = findViewById(R.id.welcomeText);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in
            Log.d("Main", "onAuthStateChanged:signed_in:" + currentUser.getUid());
            String username = currentUser.getDisplayName();
            //Gets the username before @ sign
            welcomeText.setText("Welcome, " + username+ "!");

        } else {
            // User is signed out
            Log.d("Main", "onAuthStateChanged:signed_out");
        }
        signoutButton.setOnClickListener(new MainActivity.SignoutButtonClickListener());
    }

    private class SignoutButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //Sign out
            FirebaseAuth.getInstance().signOut();
            Log.d("Main", "onAuthStateChanged:signed_out");

            //Call next page
            Intent intent = new Intent(MainActivity.this, SignoutActivity.class);
            startActivity(intent);
        }
    }
}