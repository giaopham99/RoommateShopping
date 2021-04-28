package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText userEmail;
    EditText userPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userEmail = findViewById(R.id.emailEditText);
        userPassword = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new LoginActivity.LoginButtonClickListener());
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }
    }

    /**
     * Login Button Listener
     **/
    private class LoginButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, (task) -> {
                        if (task.isSuccessful()) {
                            // Sign in success with the signed-in user's information
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Call next page
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
