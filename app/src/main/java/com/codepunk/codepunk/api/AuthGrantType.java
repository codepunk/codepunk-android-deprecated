/*
 * Copyright 2016 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.codepunk.codepunk.api;

import com.codepunk.codepunklib.util.EnumUtils;

import java.util.Map;

/**
 * <p>
 * Enum class that represents the various grant types used in an OAuth2 implementation.
 * </p>
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4">
 *   https://tools.ietf.org/html/rfc6749#section-4</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public enum AuthGrantType implements EnumUtils.LookupKeyProvider<String> {
  /**
   * The authorization code grant type is used to obtain both access
   * tokens and refresh tokens and is optimized for confidential clients.
   */
  AUTH_CODE("authorization_code"),

  /**
   * The implicit grant type is used to obtain access tokens (it does not
   * support the issuance of refresh tokens) and is optimized for public
   * clients known to operate a particular redirection URI.  These clients
   * are typically implemented in a browser using a scripting language
   * such as JavaScript.
   */
  IMPLICIT("implicit"),

  /**
   * <p>The resource owner password credentials grant type is suitable in
   * cases where the resource owner has a trust relationship with the
   * client, such as the device operating system or a highly privileged
   * application.  The authorization server should take special care when
   * enabling this grant type and only allow it when other flows are not
   * viable.</p>
   *
   * <p>This grant type is suitable for clients capable of obtaining the
   * resource owner's credentials (username and password, typically using
   * an interactive form).  It is also used to migrate existing clients
   * using direct authentication schemes such as HTTP Basic or Digest
   * authentication to OAuth by converting the stored credentials to an
   * access token.</p>
   */
  USER_CREDENTIALS("password"),

  /**
   * The client can request an access token using only its client
   * credentials (or other supported means of authentication) when the
   * client is requesting access to the protected resources under its
   * control, or those of another resource owner that have been previously
   * arranged with the authorization server.
   */
  CLIENT_CREDENTIALS("client_credentials"),

  /**
   * If the authorization server issued a refresh token to the client, the
   * client can make a refresh request to the token endpoint.
   */
  REFRESH_TOKEN("refresh_token"),

  /**
   * The client uses an extension grant type by specifying the grant type
   * using an absolute URI (defined by the authorization server) as the
   * value of the "grant_type" parameter of the token endpoint, and by
   * adding any additional parameters necessary.
   */
  EXTENSIONS("extensions");

  /**
   * A map used to convert a string to an AuthGrantType.
   */
  private static Map<String, AuthGrantType> sLookupMap;

  /**
   * The corresponding string value of the grant type.
   */
  String mValue;

  AuthGrantType(String str) {
    mValue = str;
  }

  /**
   * Return the AuthGrantType that corresponds to the specified string value. Returns
   * <code>null</code> if no AuthGrantType corresponds to the specified value.
   * @param value The value to convert to an AuthGrantType.
   * @return The enum constant with the specified value.
   */
  public static AuthGrantType fromValue(String value) {
    if (sLookupMap == null) {
      sLookupMap = EnumUtils.buildLookupMap(AuthGrantType.class);
    }
    return sLookupMap.get(value);
  }

  /**
   * Returns the corresponding string value.
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
