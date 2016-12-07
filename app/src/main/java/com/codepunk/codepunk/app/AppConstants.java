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

package com.codepunk.codepunk.app;

/**
 * A class used for storing commonly-used constants across the application.
 */
public class AppConstants {

  /**
   * Constant representing the number milliseconds per second.
   */
  public static final long MILLIS_PER_SECOND = 1000L;

  /**
   * A base string for keys to be used in Bundles, SharedPreferences, Accounts, etc.
   */
  private static final String KEY_BASE = AppConstants.class.getPackage().getName();

  /**
   * A key string used to store the saved {@link android.accounts.Account} name.
   */
  public static final String KEY_SAVED_ACCOUNT_NAME = KEY_BASE + ".savedAccountName";

  /**
   * A key string used to store the time at which an auth token expires.
   */
  public static final String KEY_EXPIRES_AT = KEY_BASE + ".expiresAt";

  /**
   * A base string for referencing {@link android.content.Intent} actions.
   */
  private static final String ACTION = "codepunk.intent.action";

  /**
   * A base string for referencing {@link android.content.Intent} extra data.
   */
  private static final String EXTRA = "codepunk.intent.extra";

  /**
   * An {@link android.content.Intent} action corresponding to when State changes in
   * {@link SessionManager}.
   */
  public static final String ACTION_SESSION_STATE_CHANGED = ACTION + ".SESSION_STATE_CHANGED";

  /**
   * Key string used to store an Exception in an {@link android.content.Intent}.
   */
  public static final String EXTRA_EXCEPTION = EXTRA + ".EXCEPTION";

  /**
   * Key string used to store {@link com.codepunk.codepunk.app.SessionManager.State} in
   * an {@link android.content.Intent}.
   */
  public static final String EXTRA_SESSION_STATE = EXTRA + ".SESSION_STATE";

  /**
   * Key string used to store whether a new {@link android.accounts.Account} is being added in
   * {@link com.codepunk.codepunk.AuthenticatorActivity}.
   */
  public static final String EXTRA_ADDING_NEW_ACCOUNT = EXTRA + ".ADDING_NEW_ACCOUNT";

  /**
   * Private constructor.
   */
  private AppConstants() {
  }
}
