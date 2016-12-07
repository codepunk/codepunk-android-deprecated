/*
 * Copyright 2016 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codepunk.codepunk;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codepunk.codepunk.app.ApiManager;
import com.codepunk.codepunk.model.AuthInfo;
import com.codepunk.codepunk.util.AccountManagerUtils;

import java.util.Calendar;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_PASSWORD;
import static com.codepunk.codepunk.app.AppConstants.EXTRA_ADDING_NEW_ACCOUNT;
import static com.codepunk.codepunk.app.AppConstants.KEY_EXPIRES_AT;
import static com.codepunk.codepunk.app.AppConstants.MILLIS_PER_SECOND;
import static com.codepunk.codepunk.api.AuthTokenType.AUTH_TOKEN_TYPE_DEFAULT;

/**
 * {@link AccountAuthenticatorActivity} that handles account authentication with direct user
 * interaction.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity
    implements View.OnClickListener {

  /**
   * Whether we are adding a new account. If we are re-authenticating only, this will be false.
   */
  private boolean mAddingNewAccount;

  /**
   * The account type.
   */
  private String mAccountType;

  /**
   * An {@link AccountManager} instance.
   */
  private AccountManager mAccountManager;

  /**
   * An {@link ApiManager} instance.
   */
  private ApiManager mApiManager;

  /**
   * {@link EditText} used to input the username.
   */
  private EditText mUsernameEdit;

  /**
   * {@link EditText} used to input the password.
   */
  private EditText mPasswordEdit;

  /**
   * {@link Button} used to submit the user's credentials for authentication.
   */
  private Button mLoginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authenticator);

    onNewIntent(getIntent());
    mAccountType = getString(R.string.authenticator_account_type);

    mAccountManager = AccountManager.get(this);
    mApiManager = ApiManager.getInstance(this);

    mUsernameEdit = (EditText) findViewById(R.id.edit_username);
    mPasswordEdit = (EditText) findViewById(R.id.edit_password);
    mLoginButton = (Button) findViewById(R.id.button_login);

    mLoginButton.setOnClickListener(this);
  }

  /**
   * Determines whether we are adding a new account.
   * @param intent The new {@link Intent}.
   */
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    mAddingNewAccount = intent.getBooleanExtra(EXTRA_ADDING_NEW_ACCOUNT, false);
  }

  /**
   * Validates the user's input before attempting to authenticate.
   * @param view
   */
  @Override
  public void onClick(View view) {
    if (view == mLoginButton) {
      final String username = String.valueOf(mUsernameEdit.getText());
      final String password = String.valueOf(mPasswordEdit.getText());
      if (TextUtils.isEmpty(username)) {
        Toast.makeText(
            this,
            R.string.authenticator_username_required,
            Toast.LENGTH_LONG).show();
        mUsernameEdit.requestFocus();
        return;
      } else if (TextUtils.isEmpty(password)) {
        Toast.makeText(
            this,
            R.string.authenticator_password_required,
            Toast.LENGTH_LONG).show();
        mPasswordEdit.requestFocus();
        return;
      } else if (mAddingNewAccount &&
          AccountManagerUtils.getAccountByNameAndType(
              mAccountManager,
              username,
              mAccountType) != null) {
        String text = getString(R.string.authenticator_account_exists, username);
        Toast.makeText(AuthenticatorActivity.this, text, Toast.LENGTH_LONG).show();
        return;
      }
      // TODO Spinner
      mLoginButton.setEnabled(false);
      authenticate(username, password);
    }
  }

  /**
   * Attemps to authenticate the user with the supplied username and password.
   * @param username The username to use to authenticate.
   * @param password The password to use to authenticate.
   */
  private void authenticate(final String username, final String password) {
    final long currentTime = Calendar.getInstance().getTimeInMillis();
    mApiManager.getAuthDelegate()
        .getAuthToken(
            username,
            password,
            new Response.Listener<AuthInfo>() {
              @Override
              public void onResponse(AuthInfo authInfo) {
                // TODO Remove spinner

                final String refreshToken = authInfo.getRefreshToken();

                Account account = new Account(username, mAccountType);
                if (!mAccountManager.addAccountExplicitly(account, refreshToken, null)) {
                  mAccountManager.setPassword(account, refreshToken);
                }

                final String authToken = authInfo.getAccessToken();
                mAccountManager.setAuthToken(
                    account,
                    AUTH_TOKEN_TYPE_DEFAULT.getString(),
                    authToken);
                mAccountManager.setUserData(
                    account,
                    KEY_EXPIRES_AT,
                    String.valueOf(currentTime + authInfo.getExpiresIn() * MILLIS_PER_SECOND));

                Bundle result = new Bundle();
                result.putString(KEY_ACCOUNT_NAME, username);
                result.putString(KEY_ACCOUNT_TYPE, mAccountType);
                result.putString(KEY_AUTHTOKEN, authToken);
                result.putString(KEY_PASSWORD, refreshToken);
                setAccountAuthenticatorResult(result);
                setResult(RESULT_OK);
                finish();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                mLoginButton.setEnabled(true);
                String text = error.getMessage();
                if (TextUtils.isEmpty(text)) {
                  text = error.getClass().getSimpleName(); // TODO
                }
                Toast.makeText(AuthenticatorActivity.this, text, Toast.LENGTH_LONG).show();
              }
            });
  }
}
