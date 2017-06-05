package karikuncheva.dominosapp.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import karikuncheva.dominosapp.catalog.CatalogActivity;
import karikuncheva.dominosapp.DBManager;
import karikuncheva.dominosapp.LoginActivity;
import karikuncheva.dominosapp.R;
import karikuncheva.dominosapp.model.User;

public class ProfileActivity extends AppCompatActivity {



    private TextView welcome;
    private EditText name;
    private EditText phone;
    private EditText password;
    private EditText confirm;
    private Button save;
    private Button cancel;
    private Button back;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //user =  DBManager.getInstance(ProfileActivity.this).getUser(username);
        back = (Button) findViewById(R.id.back_button_profile);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CatalogActivity.class);
                ProfileActivity.this.startActivity(intent);
            }
        });

        welcome = (TextView) findViewById(R.id.welcome);
        phone = (EditText) findViewById(R.id.phone_edit_et);
        name = (EditText) findViewById(R.id.name_edit_et);
        password = (EditText) findViewById(R.id.password_edit_et);
        confirm = (EditText) findViewById(R.id.password2_edit_et);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);


        welcome.setText("Welcome, " + LoginActivity.loggedUser.getUsername());

        if (LoginActivity.loggedUser.getName() != null) {
            name.setText(LoginActivity.loggedUser.getName());
        }
        if (LoginActivity.loggedUser.getPhoneNumber() != null) {
            phone.setText(LoginActivity.loggedUser.getPhoneNumber());
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateData()) {
                    DBManager.getInstance(ProfileActivity.this).updateUser(LoginActivity.loggedUser.getUsername());
                    Toast.makeText(ProfileActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, CatalogActivity.class);
                startActivity(i);
            }
        });
    }

    public boolean validateMobileNumber(String mobileNumber) {
        Pattern regexPattern = Pattern.compile("^((088)|(089)|(087))[0-9]{7}$");
        Matcher regMatcher = regexPattern.matcher(mobileNumber);
        if (regMatcher.matches()) {
            return true;
        }
        return false;
    }

    private boolean validateData() {
        LoginActivity.loggedUser.setName(name.getText().toString());

        if (!phone.getText().toString().isEmpty() && !validateMobileNumber(phone.getText().toString())) {
            phone.setError("Ivalid phone number!");
            phone.setText("");
            return false;
        } else {
            LoginActivity.loggedUser.setPhoneNumber(phone.getText().toString());
        }
        if (!password.getText().toString().isEmpty()  && password.getText().toString().equals(confirm.getText().toString())) {
            LoginActivity.loggedUser.setPassword(password.getText().toString());
        } else {
            if (!password.getText().toString().isEmpty() || !confirm.getText().toString().isEmpty()) {
                password.setError("Please, make sure your passwords match!");
                password.requestFocus();
                password.setText("");
                confirm.setText("");
                return false;
            }
        }
        return true;
    }
}
