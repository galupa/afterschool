/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jemput.middup.jemputan.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.auth.ui.email.RegisterEmailActivity;
import com.google.android.gms.common.Scopes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemput.middup.jemputan.R;
import com.jemput.middup.jemputan.activities.RegisterStudentActivity;
import com.jemput.middup.jemputan.activities.SplashActivity;
import com.jemput.middup.jemputan.activities.StudentStatusActivity;
import com.jemput.middup.jemputan.activities.TeacherDashboardActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthUiActivity extends AppCompatActivity {
    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE-ME";
    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final int RC_SIGN_IN = 100;

    @BindView(R.id.sign_in)
    Button mSignIn;


    @BindView(R.id.root)
    View mRootView;

    private DatabaseReference mRef;
    private DatabaseReference teacherRef;
    private DatabaseReference adminRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_ui_layout);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.sign_in)
    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getSelectedLogo())
                        .setProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .setIsSmartLockEnabled(true)
                        .setAllowNewEmailAccounts(true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            startActivity(SplashActivity.createIntent(AuthUiActivity.this, response));
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        return AuthUI.getDefaultTheme();
    }

    @MainThread
    @DrawableRes
    private int getSelectedLogo() {
        return R.drawable.logo_color;
    }

    @MainThread
    private List<IdpConfig> getSelectedProviders() {
        List<IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(new IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
        selectedProviders.add(
                new IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());
        return selectedProviders;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();

        return result;
    }

    @MainThread
    private String getSelectedTosUrl() {
        return FIREBASE_TOS_URL;
    }


    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }


    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, AuthUiActivity.class);
        return in;
    }
}
