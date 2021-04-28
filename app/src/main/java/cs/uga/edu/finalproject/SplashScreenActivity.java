package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Button registerButton = findViewById(R.id.signupButton);
        Button loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(new RegisterButtonClickListener());
        loginButton.setOnClickListener(new LoginButtonClickListener());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
            mAuth.signOut();
    }

    private class RegisterButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), RegistrationActivity.class);
            v.getContext().startActivity(intent);
        }
    }


    private class LoginButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), LoginActivity.class);
            v.getContext().startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // check for sign in activity result
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                // Sign In: SUCCESS
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }else{
                Toast.makeText(getApplicationContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}