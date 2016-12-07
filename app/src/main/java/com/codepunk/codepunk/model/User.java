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

package com.codepunk.codepunk.model;

import com.google.gson.annotations.SerializedName;

// TODO Make Parcelable?
// TODO Make a Builder?

/**
 * A class which represents a user.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class User {

  /**
   * The user's numeric ID.
   */
  @SerializedName("id")
  private int mId;

  /**
   * Whether the user's credentials have expired.
   */
  @SerializedName("credentials_expired")
  private boolean mCredentialsExpired;

  /**
   * The user's email.
   */
  @SerializedName("email")
  private String mEmail;

  /**
   * A standard, canonical representation of the user's email.
   */
  @SerializedName("email_canonical")
  private String mEmailCanonical;

  /**
   * Whether the user's account is currently enabled.
   */
  @SerializedName("enabled")
  private boolean mEnabled;

  /**
   * Whether the user's account is expired.
   */
  @SerializedName("expired")
  private boolean mExpired;

  /**
   * The date of the user's last login.
   */
  @SerializedName("last_login")
  private String mLastLogin; // TODO Make Date

  /**
   * Whether the user's account is locked.
   */
  @SerializedName("locked")
  private boolean mLocked;

  /**
   * The user's username.
   */
  @SerializedName("username")
  private String mUsername;

  /**
   * A standard, canonical representation of the user's username.
   */
  @SerializedName("username_canonical")
  private String mUsernameCanonical;

  /**
   * Private constructor.
   */
  private User() {
  }

  /**
   * Returns the user's numeric ID.
   * @return The user's ID.
   */
  public int getId() {
    return mId;
  }

  /**
   * Returns whether the user's credentials have expired.
   * @return <code>true</code> if the user's credentials have expired and <code>false</code>
   * otherwise.
   */
  public boolean isCredentialsExpired() {
    return mCredentialsExpired;
  }

  /**
   * Returns the user's email.
   * @return The user's email.
   */
  public String getEmail() {
    return mEmail;
  }

  /**
   * Returns a standard, canonical representation of the user's email.
   * @return A standard, canonical representation of the user's email.
   */
  public String getEmailCanonical() {
    return mEmailCanonical;
  }

  /**
   * Returns whether the user's account is enabled.
   * @return <code>true</code> if the user's account is enabled and <code>false</code> otherwise.
   */
  public boolean isEnabled() {
    return mEnabled;
  }

  /**
   * Returns whether the user's account is expired.
   * @return <code>true</code> if the user's account is expired and <code>false</code> otherwise.
   */
  public boolean isExpired() {
    return mExpired;
  }

  /**
   * Returns the date of the user's last login.
   * @return The date of the user's last login.
   */
  public String getLastLogin() {
    return mLastLogin; // TODO Make Date
  }

  /**
   * Returns whether the user's account is locked.
   * @return <code>true</code> if the user's account is locked and <code>false</code> otherwise.
   */
  public boolean isLocked() {
    return mLocked;
  }

  /**
   * Returns the user's username.
   * @return The username.
   */
  public String getUsername() {
    return mUsername;
  }

  /**
   * Returns a standard, canonical representation of the user's username.
   * @return A standard, canonical representation of the user's username.
   */
  public String getUsernameCanonical() {
    return mUsernameCanonical;
  }
}
