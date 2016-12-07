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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepunk.codepunklib.util.log.LogManager;
import com.codepunk.codepunklib.util.log.FormattingLogger;
import com.codepunk.codepunk.app.SessionManager;
import com.codepunk.codepunk.app.SessionManager.State;

import static com.codepunk.codepunk.app.AppConstants.ACTION_SESSION_STATE_CHANGED;
import static com.codepunk.codepunk.app.AppConstants.EXTRA_SESSION_STATE;
import static com.codepunk.codepunk.app.SessionManager.State.INITIALIZED;

// TODO Follow Scala style guide at http://docs.scala-lang.org/style/
// Google java style guide https://google.github.io/styleguide/javaguide.html

/**
 * The main activity for the application.
 */
@SuppressWarnings({"unused"})
public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {

  /**
   * A {@link SessionManager} instance.
   */
  private SessionManager mSessionManager;

  /**
   * A {@link LocalBroadcastManager} instance.
   */
  private LocalBroadcastManager mLocalBroadcastManager;

  /**
   * {@link TextView} used to display a message to the user.
   */
  private TextView mHelloTextView;

  /**
   * {@link Button} used to log in or log out.
   */
  private Button mAuthenticateButton;

  /**
   * Sets up the activity.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mSessionManager = SessionManager.getInstance();
    mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    mHelloTextView = (TextView) findViewById(R.id.text_hello);
    mAuthenticateButton = (Button) findViewById(R.id.button_authenticate);

    LogManager manager = LogManager.getInstance();
    FormattingLogger logger = manager.get(Log.VERBOSE);
    boolean loggable = logger.isLoggable(Log.DEBUG);
  }

  /**
   * Registers receivers and responds to current session state.
   */
  @Override
  protected void onStart() {
    super.onStart();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(ACTION_SESSION_STATE_CHANGED);
    mLocalBroadcastManager.registerReceiver(mAuthenticatedUserReceiver, intentFilter);
    State state = mSessionManager.getState();
    onStateChanged(state, null);
    if (state == INITIALIZED) {
      mSessionManager.authenticate(this);
    }
  }

  /**
   * Unregisters receivers.
   */
  @Override
  protected void onStop() {
    super.onStop();
    mLocalBroadcastManager.unregisterReceiver(mAuthenticatedUserReceiver);
  }

  /**
   * Redirects the onActivityResult call to {@link SessionManager}.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mSessionManager.onActivityResult(this, requestCode, resultCode, data);
  }

  /**
   * Responds to session state changing.
   * @param state The new state.
   * @param intent An {@link Intent} with optional additional information about the state.
   */
  private void onStateChanged(State state, Intent intent) {
    switch (state) {
      case AUTHENTICATING:
        mHelloTextView.setText(state.getResId());
        mAuthenticateButton.setText(android.R.string.cancel);
        mAuthenticateButton.setEnabled(true);
        break;
      case AUTHENTICATED:
        String username = mSessionManager.getUser().getUsername();
        mHelloTextView.setText(getString(R.string.main_hello, username));
        mAuthenticateButton.setText(R.string.app_logout);
        mAuthenticateButton.setEnabled(true);
        break;
      case CANCELING:
        mHelloTextView.setText(state.getResId());
        mAuthenticateButton.setEnabled(false);
        break;
      default:
        // TODO Show specific error/reason if state == ERROR
        mHelloTextView.setText(state.getResId());
        mAuthenticateButton.setText(R.string.app_login);
        mAuthenticateButton.setEnabled(true);
    }
  }

  /**
   * Logs the user in or out depending on the current session state.
   */
  @Override
  public void onClick(View view) {
    if (view == mAuthenticateButton) {
      State state = mSessionManager.getState();
      switch (state) {
        case AUTHENTICATING:
          mSessionManager.cancel(this);
          break;
        case AUTHENTICATED:
          mSessionManager.invalidate(this);
          break;
        case CANCELING:
          // No action
          break;
        default:
          mSessionManager.authenticate(this);
      }
    }
  }

  /**
   * Directs broadcasts to the appropriate method.
   */
  BroadcastReceiver mAuthenticatedUserReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (TextUtils.equals(action, ACTION_SESSION_STATE_CHANGED)) {
        onStateChanged((State) intent.getSerializableExtra(EXTRA_SESSION_STATE), intent);
      }
    }
  };
}
