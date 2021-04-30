package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.firebase.auth.FirebaseAuth;

public class SignoutActivity extends AppCompatActivity {
    Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);
        returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new SignoutActivity.ReturnButtonClickListener());
    }

    private class ReturnButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //Call next page
            Intent intent = new Intent(SignoutActivity.this, SplashScreenActivity.class);
            startActivity(intent);
        }
    }
}