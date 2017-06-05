package karikuncheva.dominosapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import karikuncheva.dominosapp.catalog.CatalogActivity;
import karikuncheva.dominosapp.model.User;


public class LoginActivity extends AppCompatActivity {


    public static User loggedFbUser;
    public static User loggedUser;

    private EditText username_login;
    private EditText password_login;
    private Button loginButton;
    private Button registerButton;
    private LoginButton loginFbButton;

    private String username;
    private String password;
    private User user;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private karikuncheva.dominosapp.Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        session = new karikuncheva.dominosapp.Session(this);

        if (session.loggedin()) {
            session.setLoggedin(false);
            finish();
            startActivity(new Intent(LoginActivity.this, CatalogActivity.class));
        }

        setContentView(R.layout.activity_main);

        username_login = (EditText) this.findViewById(R.id.username_login);
        password_login = (EditText) this.findViewById(R.id.password_login);
        loginButton = (Button) this.findViewById(R.id.login_button);
        registerButton = (Button) this.findViewById(R.id.registration_button);
        loginFbButton = (LoginButton) this.findViewById(R.id.login_fb_button);
        callbackManager = CallbackManager.Factory.create();

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initialize(username_login, password_login);

                if (validate(username, password)) {

                    if (DBManager.getInstance(LoginActivity.this).existsUser(username)) {
                        user = DBManager.getInstance(LoginActivity.this).getUser(username);
                        if (user.getPassword().equals(password)) {
                            Intent intent = new Intent(LoginActivity.this, MakeOrderActivity.class);
                            LoginActivity.this.startActivity(intent);
                        } else {
                            password_login.setError("Wrong password! Please, try again!");
                            password_login.setText("");
                            password_login.requestFocus();
                        }
                    }
                }
            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (Profile.getCurrentProfile() != null) {
                    Intent i = new Intent(LoginActivity.this, CatalogActivity.class);
                    startActivity(i);
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    Intent i = new Intent(LoginActivity.this, CatalogActivity.class);
                    startActivity(i);
                }
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        loginFbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                if (profile != null) {
                    Intent i = new Intent(LoginActivity.this, MakeOrderActivity.class);
                    loggedFbUser = new User(profile.getFirstName().toString(), profile.getId().toString());
                    DBManager.getInstance(LoginActivity.this).addUser(loggedFbUser);
                    if (DBManager.getInstance(LoginActivity.this).existsUser(loggedFbUser.getUsername().toString())) {
                        loggedUser = DBManager.getInstance(LoginActivity.this).getUser(loggedFbUser.getUsername().toString());
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login attempt canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Login attempt failed.", Toast.LENGTH_SHORT).show();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent_reg = new Intent(LoginActivity.this, RegistrationActivity.class);
                LoginActivity.this.startActivity(intent_reg);
            }
        });
    }

    public boolean validate(String username, String password) {
        initialize(username_login, password_login);
        if (username.isEmpty()) {
            username_login.setError("Please, enter a valid username!");
            username_login.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            password_login.setError("Please, enter a valid password");
            password_login.requestFocus();
            return false;
        }
        return true;
    }

    public void initialize(TextView username_login, TextView password_login) {
        username = username_login.getText().toString().trim();
        password = password_login.getText().toString().trim();
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

