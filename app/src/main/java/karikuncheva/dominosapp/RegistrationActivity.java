package karikuncheva.dominosapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import karikuncheva.dominosapp.navigation.AddAddressActivity;
import karikuncheva.dominosapp.model.User;

public class RegistrationActivity extends AppCompatActivity {
    private User user;
    private EditText username_reg, email_reg, pass_reg, confirm_pass_reg;
    private String username, email, pass, confirmPass;
    Button regButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username_reg = (EditText) findViewById(R.id.username_reg);
        email_reg = (EditText) findViewById(R.id.email_reg);
        pass_reg = (EditText) findViewById(R.id.password_reg);
        confirm_pass_reg = (EditText) findViewById(R.id.confirm_pass_reg);
        regButton = (Button) findViewById(R.id.registration_button);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(); //call when the button is clicked to validate the input
            }
        });
    }

    public void register() {
        initialize();
        if (validate()) {
            user = new User(username, pass, email);
            boolean isValid = DBManager.getInstance(this).addUser(user);
            if (isValid) {
                LoginActivity.loggedUser = user;
                Intent intent = new Intent(RegistrationActivity.this, MakeOrderActivity.class);
                intent.putExtra("fromActivity", 1);
                RegistrationActivity.this.startActivity(intent);
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                username_reg.setText("");
                username_reg.requestFocus();
            }
        } else {
            Toast.makeText(this, "Registration has failed", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean validate() {
        boolean valid = true;
        this.user = new User(username, pass, email);
        if (username.isEmpty()) {
            username_reg.setError("Username must not be empty!");
            username_reg.requestFocus();
            valid = false;
        }
        if (!user.validatePassword(pass)) {
            pass_reg.setError("Password must contains at least 8 characters, least 1 number and both lower and uppercase letters");
            pass_reg.setText("");
            pass_reg.requestFocus();
            valid = false;
        }
        if (!pass.equals(confirmPass)) {
            confirm_pass_reg.setError("Passwords do not match");
            pass_reg.setText("");
            confirm_pass_reg.setText("");
            pass_reg.requestFocus();
            valid = false;
        }
        return valid;
    }

    public void initialize() {
        username = username_reg.getText().toString().trim();
        email = email_reg.getText().toString().trim();
        pass = pass_reg.getText().toString().trim();
        confirmPass = confirm_pass_reg.getText().toString().trim();
    }
}
