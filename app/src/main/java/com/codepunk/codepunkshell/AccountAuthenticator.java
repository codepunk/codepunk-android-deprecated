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

package com.codepunk.codepunkshell;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.codepunk.codepunkshell.api.AuthTokenType;
import com.codepunk.codepunkshell.api.SymfonyError;
import com.codepunk.codepunkshell.app.ApiManager;
import com.codepunk.codepunkshell.model.AuthInfo;

import java.util.Calendar;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_PASSWORD;
import static com.codepunk.codepunkshell.api.Api.DEFAULT_TIMEOUT_DURATION;
import static com.codepunk.codepunkshell.api.Api.DEFAULT_TIME_UNIT;
import static com.codepunk.codepunkshell.api.AuthTokenType.AUTH_TOKEN_TYPE_DEFAULT;
import static com.codepunk.codepunkshell.app.AppConstants.EXTRA_ADDING_NEW_ACCOUNT;
import static com.codepunk.codepunkshell.app.AppConstants.KEY_EXPIRES_AT;
import static com.codepunk.codepunkshell.app.AppConstants.MILLIS_PER_SECOND;

/**
 * Implementation of {@link AbstractAccountAuthenticator} that authenticates accounts against
 * Symfony's FOSOAuthServerBundle OAuth2 implementation.
 */
@SuppressWarnings("WeakerAccess")
public class AccountAuthenticator extends AbstractAccountAuthenticator {

  /**
   * The {@link Context} associated with this authenticator.
   */
  private final Context mContext;

  /**
   * An {@link AccountManager} instance for syncing auth info with the specified {@link Account}.
   */
  private final AccountManager mAccountManager;

  /**
   * An {@link ApiManager} instance for making api calls.
   */
  private final ApiManager mApiManager;

  /**
   * Constructor that accepts a {@link Context} and sets up the required managers.
   * @param context The {@link Context} associated with this authenticator.
   */
  public AccountAuthenticator(Context context) {
    super(context);
    mContext = context;
    mAccountManager = AccountManager.get(context);
    mApiManager = ApiManager.getInstance(context);
  }

  /**
   * Adds an account of the specified accountType.
   * @param response to send the result back to the AccountManager, will never be null
   * @param accountType the type of account to add, will never be null
   * @param authTokenType the type of auth token to retrieve after adding the account, may be null
   * @param requiredFeatures a String array of authenticator-specific features that the added
   * account must support, may be null
   * @param options a Bundle of authenticator-specific options, may be null
   * @return a Bundle result or null if the result is to be returned via the response. The result
   * will contain either:
   * <ul>
   * <li> {@link AccountManager#KEY_INTENT}, or
   * <li> {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of
   * the account that was added, or
   * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
   * indicate an error
   * </ul>
   * @throws NetworkErrorException if the authenticator could not honor the request due to a
   * network error
   */
  @Override
  public Bundle addAccount(
      AccountAuthenticatorResponse response,
      String accountType,
      String authTokenType,
      String[] requiredFeatures,
      Bundle options) throws NetworkErrorException {
    final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
    intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    intent.putExtra(EXTRA_ADDING_NEW_ACCOUNT, true);
    final Bundle bundle = new Bundle();
    bundle.putParcelable(AccountManager.KEY_INTENT, intent);
    return bundle;
  }

  /**
   * Returns a Bundle that contains the Intent of the activity that can be used to edit the
   * properties. In order to indicate success the activity should call response.setResult()
   * with a non-null Bundle.
   * @param response used to set the result for the request. If the Constants.INTENT_KEY
   *   is set in the bundle then this response field is to be used for sending future
   *   results if and when the Intent is started.
   * @param accountType the AccountType whose properties are to be edited.
   * @return a Bundle containing the result or the Intent to start to continue the request.
   *   If this is null then the request is considered to still be active and the result should
   *   sent later using response.
   */
  @Override
  public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
    // TODO
    return null;
  }

  /**
   * Checks that the user knows the credentials of an account.
   * @param response to send the result back to the AccountManager, will never be null
   * @param account the account whose credentials are to be checked, will never be null
   * @param options a Bundle of authenticator-specific options, may be null
   * @return a Bundle result or null if the result is to be returned via the response. The result
   * will contain either:
   * <ul>
   * <li> {@link AccountManager#KEY_INTENT}, or
   * <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the check succeeded, false otherwise
   * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
   * indicate an error
   * </ul>
   * @throws NetworkErrorException if the authenticator could not honor the request due to a
   * network error
   */
  @Override
  public Bundle confirmCredentials(
      AccountAuthenticatorResponse response,
      Account account,
      Bundle options) throws NetworkErrorException {
    // TODO
    return null;
  }

  /**
   * Gets an auth token for an account.
   *
   * If not {@code null}, the resultant {@link Bundle} will contain different sets of keys
   * depending on whether a token was successfully issued and, if not, whether one
   * could be issued via some {@link android.app.Activity}.
   * <p>
   * If a token cannot be provided without some additional activity, the Bundle should contain
   * {@link AccountManager#KEY_INTENT} with an associated {@link Intent}. On the other hand, if
   * there is no such activity, then a Bundle containing
   * {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} should be
   * returned.
   * <p>
   * If a token can be successfully issued, the implementation should return the
   * {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of the
   * account associated with the token as well as the {@link AccountManager#KEY_AUTHTOKEN}. In
   * addition {@link AbstractAccountAuthenticator} implementations that declare themselves
   * {@code android:customTokens=true} may also provide a non-negative {@link
   * #KEY_CUSTOM_TOKEN_EXPIRY} long value containing the expiration timestamp of the expiration
   * time (in millis since the unix epoch).
   * <p>
   * Implementers should assume that tokens will be cached on the basis of account and
   * authTokenType. The system may ignore the contents of the supplied options Bundle when
   * determining to re-use a cached token. Furthermore, implementers should assume a supplied
   * expiration time will be treated as non-binding advice.
   * <p>
   * Finally, note that for android:customTokens=false authenticators, tokens are cached
   * indefinitely until some client calls {@link
   * AccountManager#invalidateAuthToken(String,String)}.
   *
   * @param response to send the result back to the AccountManager, will never be null
   * @param account the account whose credentials are to be retrieved, will never be null
   * @param authTokenType the type of auth token to retrieve, will never be null
   * @param options a Bundle of authenticator-specific options, may be null
   * @return a Bundle result or null if the result is to be returned via the response.
   * @throws NetworkErrorException if the authenticator could not honor the request due to a
   * network error
   */
  @Override
  public Bundle getAuthToken(
      AccountAuthenticatorResponse response,
      Account account,
      String authTokenType,
      Bundle options) throws NetworkErrorException {
    final long currentTime = Calendar.getInstance().getTimeInMillis();
    String authToken = mAccountManager.peekAuthToken(account, AUTH_TOKEN_TYPE_DEFAULT.getString());

    if (!TextUtils.isEmpty(authToken)) {
      // Check if auth token expired
      final long expiresAt = Long.valueOf(mAccountManager.getUserData(account, KEY_EXPIRES_AT));
      if (currentTime >= expiresAt) {
        mAccountManager.invalidateAuthToken(account.type, authToken);
        authToken = null;
      }
    }

    String refreshToken = mAccountManager.getPassword(account);

    if (TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(refreshToken)) {
      final RequestFuture<AuthInfo> future = RequestFuture.newFuture();
      mApiManager.getAuthDelegate().refreshToken(refreshToken, future, future);
      try {
        AuthInfo authInfo = future.get(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIME_UNIT);
        authToken = authInfo.getAccessToken();
        refreshToken = authInfo.getRefreshToken();

        mAccountManager.setAuthToken(account, AUTH_TOKEN_TYPE_DEFAULT.getString(), authToken);
        mAccountManager.setPassword(account, refreshToken);
        mAccountManager.setUserData(
            account,
            KEY_EXPIRES_AT,
            String.valueOf(currentTime + authInfo.getExpiresIn() * MILLIS_PER_SECOND));
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (!(cause instanceof SymfonyError)) {
          // If the cause was NOT a SymfonyError, it was likely due to a network error.
          throw new NetworkErrorException(cause);
        }
      }
    }

    final Bundle result = new Bundle();
    if (TextUtils.isEmpty(authToken)) {
      // We were unable to get an auth token. We need the user to explicitly log in again
      final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
      intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
      result.putParcelable(AccountManager.KEY_INTENT, intent);
    } else {
      result.putString(KEY_ACCOUNT_NAME, account.name);
      result.putString(KEY_ACCOUNT_TYPE, account.type);
      result.putString(KEY_AUTHTOKEN, authToken);
      result.putString(KEY_PASSWORD, refreshToken);
    }

    return result;
  }

  /**
   * Ask the authenticator for a localized label for the given authTokenType.
   * @param authTokenType the authTokenType whose label is to be returned, will never be null
   * @return the localized label of the auth token type, may be null if the type isn't known
   */
  @Override
  public String getAuthTokenLabel(String authTokenType) {
    AuthTokenType type = AuthTokenType.fromString(authTokenType);
    if (type == null) {
      return mContext.getString(R.string.authenticator_token_type_unknown);
    } else {
      return mContext.getString(type.getResId());
    }
  }

  /**
   * Update the locally stored credentials for an account.
   * @param response to send the result back to the AccountManager, will never be null
   * @param account the account whose credentials are to be updated, will never be null
   * @param authTokenType the type of auth token to retrieve after updating the credentials,
   * may be null
   * @param options a Bundle of authenticator-specific options, may be null
   * @return a Bundle result or null if the result is to be returned via the response. The result
   * will contain either:
   * <ul>
   * <li> {@link AccountManager#KEY_INTENT}, or
   * <li> {@link AccountManager#KEY_ACCOUNT_NAME} and {@link AccountManager#KEY_ACCOUNT_TYPE} of
   * the account whose credentials were updated, or
   * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
   * indicate an error
   * </ul>
   * @throws NetworkErrorException if the authenticator could not honor the request due to a
   * network error
   */
  @Override
  public Bundle updateCredentials(
      AccountAuthenticatorResponse response,
      Account account,
      String authTokenType,
      Bundle options) throws NetworkErrorException {
    // TODO
    return null;
  }

  /**
   * Checks if the account supports all the specified authenticator specific features.
   * @param response to send the result back to the AccountManager, will never be null
   * @param account the account to check, will never be null
   * @param features an array of features to check, will never be null
   * @return a Bundle result or null if the result is to be returned via the response. The result
   * will contain either:
   * <ul>
   * <li> {@link AccountManager#KEY_INTENT}, or
   * <li> {@link AccountManager#KEY_BOOLEAN_RESULT}, true if the account has all the features,
   * false otherwise
   * <li> {@link AccountManager#KEY_ERROR_CODE} and {@link AccountManager#KEY_ERROR_MESSAGE} to
   * indicate an error
   * </ul>
   * @throws NetworkErrorException if the authenticator could not honor the request due to a
   * network error
   */
  @Override
  public Bundle hasFeatures(
      AccountAuthenticatorResponse response,
      Account account,
      String[] features) throws NetworkErrorException {
    // TODO
    return null;
  }
}
