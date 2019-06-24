package org.metabrainz.mobile.presentation.features.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import org.metabrainz.mobile.R;
import org.metabrainz.mobile.data.sources.api.MusicBrainzServiceGenerator;
import org.metabrainz.mobile.data.sources.api.entities.AccessToken;
import org.metabrainz.mobile.data.sources.api.entities.userdata.UserInfo;
import org.metabrainz.mobile.presentation.MusicBrainzActivity;
import org.metabrainz.mobile.util.Log;

import java.util.Objects;

public class LoginActivity extends MusicBrainzActivity {
    private LoginViewModel loginViewModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getAccessTokenLiveData().observe(this, this::saveOAuthToken);
        loginViewModel.getUserInfoLiveData().observe(this, this::saveUserInfo);

        findViewById(R.id.login_btn).setOnClickListener(v -> startLogin());
        findViewById(R.id.logout_btn).setOnClickListener(v -> logoutUser());
    }

    @Override
    protected void onResume() {
        Uri callbackUri = getIntent().getData();
        if (callbackUri != null &&
                callbackUri.toString().startsWith(MusicBrainzServiceGenerator.OAUTH_REDIRECT_URI)) {

            String code = callbackUri.getQueryParameter("code");
            if (code != null) loginViewModel.fetchAccessToken(code);

        }
        super.onResume();
    }

    private void startLogin() {
        if (LoginSharedPreferences.getLoginStatus() == LoginSharedPreferences.STATUS_LOGGED_OUT) {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(MusicBrainzServiceGenerator.AUTH_URL
                            + "?response_type=code"
                            + "&client_id=" + MusicBrainzServiceGenerator.CLIENT_ID
                            + "&redirect_uri=" + MusicBrainzServiceGenerator.OAUTH_REDIRECT_URI
                            + "&scope=profile%20collection%20tag%20rating"));
            startActivity(intent);
        } else Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        LoginSharedPreferences.logoutUser();
        Toast.makeText(getApplicationContext(),
                "User has successfully logged out.",
                Toast.LENGTH_LONG).show();
        finish();
    }

    private void saveOAuthToken(AccessToken accessToken) {
        if (accessToken != null) {
            Log.d(accessToken.getAccessToken());
            LoginSharedPreferences.saveOAuthToken(accessToken);
            loginViewModel.fetchUserInfo();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Failed to obtain access token ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserInfo(UserInfo userInfo) {
        if (userInfo != null &&
                LoginSharedPreferences.getLoginStatus() == LoginSharedPreferences.STATUS_LOGGED_OUT) {
            LoginSharedPreferences.saveUserInfo(userInfo);
            Toast.makeText(getApplicationContext(),
                    "Login successful. " + userInfo.getUsername() + " is now logged in.",
                    Toast.LENGTH_LONG).show();
            Log.d(userInfo.getUsername());
            finish();
        }
    }
}