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

package com.codepunk.codepunk.api;

import com.google.gson.annotations.SerializedName;

import com.codepunk.codepunklib.support.v1.MapCompat;
import com.codepunk.codepunklib.util.EnumUtils;

import java.util.Map;

/**
 * <p>
 * An enum representing a Symfony authorization error type.
 * </p>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public enum AuthErrorType implements EnumUtils.LookupKeyProvider<String> {

  /**
   * The request requires higher privileges than provided by the access token.
   * The resource server SHOULD respond with the HTTP 403 (Forbidden) status
   * code and MAY include the "scope" attribute with the scope necessary to
   * access the protected resource.
   */
  @SerializedName("invalid_scope")
  ERROR_INSUFFICIENT_SCOPE("invalid_scope"),

  /**
   * The client identifier provided is invalid.
   */
  @SerializedName("invalid_client")
  ERROR_INVALID_CLIENT("invalid_client"),

  /**
   * The provided authorization grant is invalid, expired,
   * revoked, does not match the redirection URI used in the
   * authorization request, or was issued to another client.
   */
  @SerializedName("invalid_grant")
  ERROR_INVALID_GRANT("invalid_grant"),

  /**
   * The request is missing a required parameter, includes an unsupported
   * parameter or parameter value, or is otherwise malformed.
   */
  @SerializedName("invalid_request")
  ERROR_INVALID_REQUEST("invalid_request"),

  /**
   * The requested scope is invalid, unknown, or malformed.
   */
  @SerializedName("invalid_scope")
  ERROR_INVALID_SCOPE("invalid_scope"),

  /**
   * The redirection URI provided does not match a pre-registered value.
   */
  @SerializedName("redirect_uri_mismatch")
  ERROR_REDIRECT_URI_MISMATCH("redirect_uri_mismatch"),

  /**
   * The client is not authorized to use the requested response type.
   */
  @SerializedName("unauthorized_client")
  ERROR_UNAUTHORIZED_CLIENT("unauthorized_client"),

  /**
   * The authorization grant is not supported by the authorization server.
   */
  @SerializedName("unsupported_grant_type")
  ERROR_UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),

  /**
   * The requested response type is not supported by the authorization server.
   */
  @SerializedName("unsupported_response_type")
  ERROR_UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),

  /**
   * The end-user or authorization server denied the request.
   * This could be returned, for example, if the resource owner decides to reject
   * access to the client at a later point.
   */
  @SerializedName("access_denied")
  ERROR_USER_DENIED("access_denied");

  /**
   * A map used to convert a string to an AuthErrorType.
   */
  private static Map<String, AuthErrorType> sLookupMap;

  /**
   * Return the {@link AuthErrorType} that corresponds to the specified string value. Returns
   * defaultValue if no {@link AuthErrorType} corresponds to the specified value.
   * @param value The value to convert to an {@link AuthErrorType}.
   * @param defaultValue The {@link AuthErrorType} to return if no {@link AuthErrorType}
   *                     corresponds to the specified value.
   * @return The enum constant with the specified value.
   */
  public static AuthErrorType fromValue(String value, AuthErrorType defaultValue) {
    if (sLookupMap == null) {
      sLookupMap = EnumUtils.buildLookupMap(AuthErrorType.class);
    }
    return MapCompat.getOrDefault(sLookupMap, value, defaultValue);
  }

  /**
   * Return the {@link AuthErrorType} that corresponds to the specified string value. Returns
   * null if no {@link AuthErrorType} corresponds to the specified value.
   * @param value The value to convert to an {@link AuthErrorType}.
   * @return The enum constant with the specified value.
   */
  public static AuthErrorType fromValue(String value) {
    return fromValue(value, null);
  }

  /**
   * The string value associated with this {@link AuthErrorType}.
   */
  private String mValue;

  AuthErrorType(String value) {
    mValue = value;
  }

  /**
   * Returns the string value.
   * @return The string value.
   */
  public String getValue() {
    return mValue;
  }

  /**
   * Returns the lookup key to use to build <code>sLookupMap</code>.
   * @return The lookup key.
   */
  @Override
  public String getLookupKey() {
    return mValue;
  }
}
