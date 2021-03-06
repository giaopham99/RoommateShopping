package cs.uga.edu.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new RegisterButtonClickListener());
    }

    private class RegisterButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();
            final String username = usernameEditText.getText().toString();

            final FirebaseAuth mAuth = FirebaseAuth.getInstance();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");
            final User userDB = new User(username,email);

            //Authenticate user creation
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegistrationActivity.this, (task) ->{
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),
                                    "User has been registered!",
                                    Toast.LENGTH_SHORT).show();

                            Log.d("Register", "Created new user with email: " + email);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();
                            //update the profile to have username
                            user.updateProfile(profileUpdate)
                                    .addOnCompleteListener(RegistrationActivity.this, (update) ->{
                                        if(update.isSuccessful()){
                                            //Add user to a table in Realtime DB
                                            myRef.push().setValue(userDB).addOnSuccessListener( new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Register", "Registration: The following username was added to DB: " + userDB.getUsername());
                                                }
                                            })
                                                    .addOnFailureListener( new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(Exception e) {
                                                            Log.d("Register", "Could not add username: " + e);
                                                        }
                                                    });
                                            Log.d("Register", "Registration: The following username was added: " + username);
                                        }
                                        else{
                                            Log.w("Register", "Could not add username", update.getException());
                                        }
                                    });
                            //Automatically log in
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(RegistrationActivity.this,
                                    "Registration failed", Toast.LENGTH_SHORT).show();

                            Log.w("Register", "Could not register account", task.getException());
                        }
                    });


        }
    }
}