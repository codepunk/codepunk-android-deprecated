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

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A class representing Symfony authorization info. This includes information such as an
 * access token, refresh token, scope, etc.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AuthInfo
    implements Parcelable {

  /**
   * {@link android.os.Parcelable.Creator} implementation.
   */
  public static final Parcelable.Creator<AuthInfo> CREATOR
      = new Parcelable.Creator<AuthInfo>() {
    public AuthInfo createFromParcel(Parcel in) {
      return new AuthInfo(in);
    }

    public AuthInfo[] newArray(int size) {
      return new AuthInfo[size];
    }
  };

  /**
   * The access token (otherwise known as auth token).
   */
  @SerializedName("access_token")
  private String mAccessToken;

  /**
   * The lifetime of the token (in seconds).
   */
  @SerializedName("expires_in")
  private int mExpiresIn;

  /**
   * The token type.
   */
  @SerializedName("token_type")
  private TokenType mTokenType;

  /**
   * The scope of this token.
   */
  @SerializedName("scope")
  private String mScope;

  /**
   * A refresh token that can be used to get a new access token after the access token expires.
   */
  @SerializedName("refresh_token")
  private String mRefreshToken;

  /**
   * Private constructor.
   */
  private AuthInfo() {
  }

  /**
   * Parcelable constructor.
   * @param in The {@link Parcel} used to construct the AuthInfo.
   */
  private AuthInfo(Parcel in) {
    mAccessToken = in.readString();
    mExpiresIn = in.readInt();
    mTokenType = (TokenType) in.readSerializable();
    mScope = in.readString();
    mRefreshToken = in.readString();
  }

  /**
   * Returns the access token.
   * @return The access token.
   */
  public String getAccessToken() {
    return mAccessToken;
  }

  /**
   * Returns the lifetime of the token (in seconds).
   * @return The lifetime of the token.
   */
  public int getExpiresIn() {
    return mExpiresIn;
  }

  /**
   * Returns the token type.
   * @return The token type.
   */
  public TokenType getTokenType() {
    return mTokenType;
  }

  /**
   * Returns the scope of the token.
   * @return The scope of the token.
   */
  public String getScope() {
    return mScope;
  }

  /**
   * Returns a refresh token. A refresh token can be used to obtain a renewed access token at any
   * time.
   * @return The refresh token.
   */
  public String getRefreshToken() {
    return mRefreshToken;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  /**
   * Flattens this object into a {@link Parcel}.
   * @param out The {@link Parcel} in which the object should be written.
   * @param flags Additional flags about how the object should be written.
   */
  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(mAccessToken);
    out.writeInt(mExpiresIn);
    out.writeSerializable(mTokenType);
    out.writeString(mScope);
    out.writeString(mRefreshToken);
  }

  /**
   * Returns a hash code value for the object.
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(31, 31, this);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param obj The reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument; <code>false</code>
   * otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AuthInfo) && EqualsBuilder.reflectionEquals(this, obj);
  }

  /**
   * Returns a string representation of the object.
   * @return The string representation.
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * Enum representing the possible values for {@link AuthInfo#mTokenType}.
   */
  public enum TokenType {

    /**
     * Bearer token type. Any party in possession of a bearer token (a "bearer") can use it to get
     * access to the associated resources.
     */
    @SerializedName("bearer")
    BEARER("bearer"),

    /**
     * MAC (Message Authentication Code) token type. The HTTP MAC access authentication scheme
     * provides a method for making authenticated HTTP requests with partial cryptographic
     * verification of the request, covering the HTTP method, request URI, and host.
     */
    @SerializedName("mac")
    MAC("mac");

    /**
     * The string representation of this TokenType.
     */
    private String mValue;

    /**
     * Simple constructor that takes a value string.
     * @param value The string representation of this TokenType.
     */
    TokenType(String value) {
      mValue = value;
    }

    /**
     * Returns the string representation of this TokenType.
     * @return The string representation of this TokenType.
     */
    public String getValue() {
      return mValue;
    }
  }
}
