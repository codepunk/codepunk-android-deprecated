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

package com.codepunk.codepunkshell.app;

import com.google.android.gms.common.AccountPicker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codepunk.codepunkshell.BuildConfig;
import com.codepunk.codepunkshell.R;
import com.codepunk.codepunkshell.model.User;
import com.codepunk.codepunkshell.util.AccountManagerUtils;

import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.codepunk.codepunkshell.app.AppConstants.ACTION_SESSION_STATE_CHANGED;
import static com.codepunk.codepunkshell.app.AppConstants.EXTRA_EXCEPTION;
import static com.codepunk.codepunkshell.app.AppConstants.EXTRA_SESSION_STATE;
import static com.codepunk.codepunkshell.app.AppConstants.KEY_SAVED_ACCOUNT_NAME;
import static com.codepunk.codepunkshell.api.AuthTokenType.AUTH_TOKEN_TYPE_DEFAULT;

/**
 * <p>
 * Class that manages session and session state. This includes keeping track of the current
 * {@link Account} (along with its authToken, refreshToken, etc.) and the currently authenticated
 * {@link User}.
 * </p>
 *
 * <p>
 * To begin the authentication process, get an instance of the SessionManager and call its
 * {@link SessionManager#authenticate(Activity)} method. The following example is from the
 * <code>onCreate</code> method of an Activity:
 * </p>
 * <pre>
 * private SessionManager mSessionManager;
 *
 * protected void onCreate(Bundle savedInstanceState) {
 *   super.onCreate(savedInstanceState);
 *   mSessionManager = SessionManager.getInstance();
 *   if (savedInstanceState == null) {
 *     mSessionManager.authenticate(this);
 *   }
 * }
 * </pre>
 *
 * <p>
 * Note that the authentication process may need to make use of the
 * {@link Activity#startActivityForResult(Intent, int)} method. Because of this, any Activity
 * that calls SessionManager's {@link SessionManager#authenticate(Activity)} method should also
 * handle the {@link Activity#onActivityResult(int, int, Intent)} method and call SessionManager's
 * {@link SessionManager#onActivityResult(Activity, int, int, Intent)} method:
 * </p>
 * <pre>
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *   super.onActivityResult(requestCode, resultCode, data);
 *   mSessionManager.onActivityResult(this, requestCode, resultCode, data);
 * }
 * </pre>
 *
 * <p>
 * When the {@link State} changes (i.e. from {@link State#NOT_AUTHENTICATED} to
 * {@link State#AUTHENTICATING} for example), SessionManager will send a local broadcast with
 * an action of <code>ACTION_SESSION_STATE_CHANGED</code> and an extra of
 * <code>EXTRA_SESSION_STATE</code>. Interested parties can set up a {@link LocalBroadcastManager}
 * and listen for <code>ACTION_SESSION_STATE_CHANGED</code> in order to react to the changes.
 * </p>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SessionManager {

  /**
   * For singleton creation.
   */
  private static final Object sLock = new Object();

  /**
   * Base request code to avoid request code collision.
   */
  private static final int CODEPUNK = 0xC0DE;

  /**
   * Request code for choosing an account from {@link AccountPicker}.
   */
  private static final int REQUEST_CODE_CHOOSE_ACCOUNT = CODEPUNK + 1;

  /**
   * The singleton instance.
   */
  @SuppressLint("StaticFieldLeak")
  private static SessionManager sInstance;

  /**
   * The current {@link Environment}.
   */
  private Environment mEnvironment = BuildConfig.DEFAULT_ENVIRONMENT;

  /**
   * The current session state.
   */
  private State mState = State.INITIALIZED;

  /**
   * The currently-authenticated account.
   */
  private Account mAccount;

  /**
   * The currently-authenticated user.
   */
  private User mUser;

  /**
   * Any AccountListener that is waiting for a call to
   * {@link SessionManager#onActivityResult(Activity, int, int, Intent)}.
   */
  private AccountListener mWaitingAccountListener;

  /**
   * Returns a singleton SessionManager instance.
   * @return A SessionManager instance.
   */
  public static SessionManager getInstance() {
    synchronized (sLock) {
      if (sInstance == null) {
        sInstance = new SessionManager();
      }
      return sInstance;
    }
  }

  /**
   * Returns the current {@link Environment}.
   * @return The current environment.
   */
  public Environment getEnvironment() {
    return mEnvironment;
  }

  /**
   * Sets the current {@link Environment}.
   * @param environment The current environment.
   */
  public void setEnvironment(Environment environment) {
    mEnvironment = environment;
  }

  /**
   * Returns the current session state.
   * @return The current session state.
   */
  public State getState() {
    return mState;
  }

  /**
   * Sets the current session state.
   * @param context The {@link Context} to use to send a local broadcast.
   * @param state The new state.
   * @param src An optional {@link Intent} with extras that will be sent along with the local
   *            broadcast.
   */
  private void setState(Context context, State state, Intent src) {
    if (mState != state) {
      mState = state;
      Intent intent = new Intent(ACTION_SESSION_STATE_CHANGED);
      intent.putExtra(EXTRA_SESSION_STATE, state);
      if (src != null) {
        intent.putExtras(src);
      }
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
  }

  /**
   * Sets the current session state.
   * @param context The {@link Context} to use to send a local broadcast.
   * @param state The new state.
   */
  private void setState(Context context, State state) {
    setState(context, state, null);
  }

  /**
   * Returns the current {@link Account}.
   * @return The current account.
   */
  public Account getAccount() {
    return mAccount;
  }

  /**
   * Returns the currently-authenticated {@link User}.
   * @return The currently-authenticated user.
   */
  public User getUser() {
    return mUser;
  }

  /**
   * Performs authentication logic. This may optionally show an account picker dialog and/or
   * an authenticator activity as needed.
   * @param activity An {@link Activity} that will be used to optionally show an account picker
   *                 dialog and/or an authenticator activity.
   */
  public void authenticate(final Activity activity) {
    if (mState != State.INITIALIZED && mState != State.NOT_AUTHENTICATED) {
      return;
    }

    setState(activity, State.AUTHENTICATING);
    final SharedPreferences sharedPreferences =
        activity.getSharedPreferences(mEnvironment.name(), MODE_PRIVATE);
    final String accountName = sharedPreferences.getString(KEY_SAVED_ACCOUNT_NAME, null);
    final String accountType = activity.getString(R.string.authenticator_account_type);
    final Account account =
        TextUtils.isEmpty(accountName) ?
            null :
            AccountManagerUtils.getAccountByNameAndType(
                AccountManager.get(activity),
                accountName,
                accountType);
    final AccountListener listener = new AccountListener(activity);
    if (account ==  null) {
      mWaitingAccountListener = listener;
      Intent intent = AccountPicker.newChooseAccountIntent(
          null /* selectedAccount */,
          null /* allowableAccounts */,
          new String[]{ accountType },
          true,
          null /* descriptionOverrideText */,
          AUTH_TOKEN_TYPE_DEFAULT.getString(),
          null /* addAccountRequiredFeatures */,
          null /* addAccountOptions */);
      activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_ACCOUNT);
    } else {
      listener.onAccount(account);
    }
  }

  /**
   * Invalidates any currently-authenticated account and user and returns the session state
   * to {@link State#NOT_AUTHENTICATED}.
   * @param removeSavedAccountName Whether to clear the selected account name from the app's
   *                               SharedPreferences.
   */
  public void invalidate(Context context, boolean removeSavedAccountName) {
    if (mState != State.AUTHENTICATED) {
      return;
    }

    if (removeSavedAccountName) {
      final SharedPreferences sharedPreferences =
          context.getSharedPreferences(mEnvironment.name(), MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.remove(KEY_SAVED_ACCOUNT_NAME);
      editor.apply();
    }

    mAccount = null;
    mUser = null;
    setState(context, State.NOT_AUTHENTICATED);
  }

  /**
   * Invalidates any currently-authenticated account and user and returns the session state
   * to {@link State#NOT_AUTHENTICATED}.
   */
  public void invalidate(Context context) {
    invalidate(context, true);
  }

  /**
   * Cancels any authentication that is currently in process.
   */
  public void cancel(Context context) {
    if (mState != State.AUTHENTICATING) {
      return;
    }
    setState(context, State.CANCELING);
  }

  /**
   * Handles the result of the account picker dialog.
   * @param activity The activity that started the account picker dialog.
   * @param requestCode The integer request code originally supplied to startActivityForResult(),
   *                    allowing you to identify who this result came from.
   * @param resultCode The integer result code returned by the child activity through its
   *                   setResult().
   * @param data An Intent, which can return result data to the caller (various data can be
   *             attached to Intent "extras").
   */
  public void onActivityResult(
      @NonNull Activity activity,
      int requestCode,
      int resultCode,
      Intent data) {
    switch (requestCode) {
      case REQUEST_CODE_CHOOSE_ACCOUNT:
        switch (resultCode) {
          case RESULT_CANCELED:
            if (mWaitingAccountListener != null) {
              mWaitingAccountListener.onAccountCanceled();
            }
            break;
          case RESULT_OK:
          default:
            String accountName = data.getStringExtra(KEY_ACCOUNT_NAME);
            SharedPreferences sharedPreferences =
                activity.getSharedPreferences(mEnvironment.name(), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_SAVED_ACCOUNT_NAME, accountName);
            editor.apply();
            Account account =
                TextUtils.isEmpty(accountName) ?
                    null :
                    AccountManagerUtils.getAccountByNameAndType(
                        AccountManager.get(activity),
                        accountName,
                        activity.getString(R.string.authenticator_account_type));
            if (mWaitingAccountListener != null) {
              mWaitingAccountListener.onAccount(account);
            }
        }
        break;
    }
  }

  /**
   * Base class that captures a {@link Context} and contains logic for stopping authentication
   * if the authentication is being canceled.
   */
  private abstract class BaseSessionListener {
    /**
     * The context to use for setting state.
     */
    Context mContext;

    /**
     * Constructor that takes a {@link Context}.
     * @param context The context to use for setting state.
     */
    private BaseSessionListener(Context context) {
      mContext = context;
    }

    /**
     * Convenience method that clears authenticated information and sets the state to
     * {@link State#NOT_AUTHENTICATED}.
     * @return True if the state was {@link State#CANCELING} and false otherwise.
     */
    boolean stopIfCanceling() {
      if (mState == State.CANCELING) {
        mAccount = null;
        mUser = null;
        setState(mContext, State.NOT_AUTHENTICATED);
        return true;
      }
      return false;
    }
  }

  /**
   * Nested class that listens for the result of getting the current {@link Account} for
   * authentication purposes (or if the user canceled the process along the way).
   */
  private class AccountListener
      extends BaseSessionListener {

    /**
     * An activity to use to get the authorization token from {@link AccountManager}.
     */
    private Activity mActivity;

    /**
     * {@link AccountManager} singleton instance.
     */
    private AccountManager mAccountManager;

    /**
     * {@link SessionAccountManagerCallback} instance.
     */
    private SessionAccountManagerCallback mAccountManagerCallback;

    /**
     * Simple constructor that accepts an activity.
     * @param activity An activity to use to get the authorization token from
     *                 {@link AccountManager}.
     */
    private AccountListener(Activity activity) {
      super(activity);
      mActivity = activity;
      mAccountManager = AccountManager.get(mActivity);
      mAccountManagerCallback = new SessionAccountManagerCallback(activity);
    }

    /**
     * Processes a successful {@link Account} request.
     * @param account An account to be used to get the authorization token.
     */
    void onAccount(Account account) {
      if (stopIfCanceling()) {
        return;
      }
      mAccount = account;
      mAccountManager.getAuthToken(
          account,
          AUTH_TOKEN_TYPE_DEFAULT.getString(),
          null /* options */,
          mActivity,
          mAccountManagerCallback,
          null);
    }

    /**
     * Processes the user canceling the account picker dialog.
     */
    void onAccountCanceled() {
      if (stopIfCanceling()) {
        return;
      }
      setState(mActivity, State.NOT_AUTHENTICATED);
    }
  }

  /**
   * A class that processes the result of a call to
   * {@link AccountManager#getAuthToken(Account, String, Bundle, Activity, AccountManagerCallback,
   * Handler)}.
   */
  private class SessionAccountManagerCallback
      extends BaseSessionListener
      implements AccountManagerCallback<Bundle> {
    /**
     * {@link ApiManager} instance.
     */
    private final ApiManager mApiManager;

    /**
     * {@link UserListener} instance.
     */
    private final UserListener mUserListener;

    /**
     * {@link UserErrorListener} instance.
     */
    private final UserErrorListener mUserErrorListener;

    /**
     * Constructor that takes a {@link Context} and initializes listeners.
     * @param context A
     */
    private SessionAccountManagerCallback(Context context) {
      super(context);
      mApiManager = ApiManager.getInstance(context);
      mUserListener = new UserListener(context);
      mUserErrorListener = new UserErrorListener(context);
    }

    @Override
    public void run(AccountManagerFuture<Bundle> future) {
      if (stopIfCanceling()) {
        return;
      }
      try {
        Bundle bundle = future.getResult();
        String authToken = bundle.getString(KEY_AUTHTOKEN);
        mApiManager.getUserDelegate()
            .getAuthenticatedUser(
                authToken,
                mUserListener,
                mUserErrorListener);
      } catch (Exception e) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_EXCEPTION, e);
        setState(mContext, State.ERROR, intent);
      }
    }
  }

  /**
   * A class that processes a successfully-authenticated user.
   */
  private class UserListener
      extends BaseSessionListener
      implements Listener<User> {

    UserListener(Context context) {
      super(context);
    }

    @Override
    public void onResponse(User user) {
      if (stopIfCanceling()) {
        return;
      }
      mUser = user;
      setState(mContext, State.AUTHENTICATED);
    }
  }

  /**
   * A class that processes any error encountered while getting the authenticated user from the
   * server.
   */
  private class UserErrorListener
      extends BaseSessionListener
      implements ErrorListener {

    UserErrorListener(Context context) {
      super(context);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
      if (stopIfCanceling()) {
        return;
      }
      Intent intent = new Intent();
      intent.putExtra(EXTRA_EXCEPTION, error);
      setState(mContext, State.ERROR, intent);
    }
  }

  /**
   * Enum that represents the session state.
   */
  public enum State {

    /**
     * The session is not authenticated, and no attempt has yet been made.
     */
    INITIALIZED(R.string.session_initialized),

    /**
     * The session is in the process of authenticating.
     */
    AUTHENTICATING(R.string.session_authenticating),

    /**
     * The session has successfully authenticated the user.
     */
    AUTHENTICATED(R.string.session_authenticated),

    /**
     * The session is in the process of canceling due to the {@link SessionManager#cancel(Context)}
     * method being invoked.
     */
    CANCELING(R.string.session_canceling),

    /**
     * The session is not authenticated (but at some point had at the very least begun the process).
     */
    NOT_AUTHENTICATED(R.string.session_not_authenticated),

    /**
     * An error occurred while authenticating.
     */
    ERROR(R.string.session_error);

    /**
     * A resource ID associated with a user-friendly string representing the current session state.
     */
    private int mResId;

    /**
     * A simple constructor that accepts a string resource ID.
     * @param resId The string resource ID.
     */
    State(@StringRes int resId) {
      mResId = resId;
    }

    /**
     * Returns the string resource ID associated with this session state.
     * @return The string resource ID.
     */
    public int getResId() {
      return mResId;
    }
  }
}
