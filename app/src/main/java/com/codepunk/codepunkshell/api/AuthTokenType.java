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

package com.codepunk.codepunkshell.api;

import android.support.annotation.StringRes;

import com.codepunk.codepunklib.util.EnumUtils;
import com.codepunk.codepunkshell.R;

import java.util.Map;

/**
 * Enum class representing the various authorization token types.
 */
public enum AuthTokenType implements EnumUtils.LookupKeyProvider<String> {
  AUTH_TOKEN_TYPE_DEFAULT("default", R.string.authenticator_token_type_default);

  /**
   * A map used to convert a string to an AuthTokenType.
   */
  private static Map<String, AuthTokenType> sLookupMap;

  /**
   * The string value of this AuthTokenType.
   */
  private final String mString;

  /**
   * A string resource ID corresponding to the localized label for this AuthTokenType.
   */
  private final @StringRes int mResId;

  /**
   * Constructor that takes an auth token type string and a string resource ID.
   * @param string An auth token type string.
   * @param resId A string resource ID corresponding to the localized label for this AuthTokenType.
   */
  AuthTokenType(String string, int resId) {
    mString = string;
    mResId = resId;
  }

  /**
   * Returns the auth token type string.
   * @return The auth token type string.
   */
  public String getString() {
    return mString;
  }

  /**
   * Returns the string resource ID corresponding to the localized label for this AuthTokenType.
   * @return The string resource ID.
   */
  public int getResId() {
    return mResId;
  }

  /**
   * Returns the AuthTokenType associated with the supplied auth token type string.
   * @param string An auth token type string.
   * @return The AuthTokenType associated with the supplied string, or <code>null</code> if no
   * such AuthTokenType is found.
   */
  public static AuthTokenType fromString(String string) {
    if (sLookupMap == null) {
      sLookupMap = EnumUtils.buildLookupMap(AuthTokenType.class);
    }
    return sLookupMap.get(string);
  }

  /**
   * Returns the lookup key to use to build <code>sLookupMap</code>.
   * @return The lookup key.
   */
  @Override
  public String getLookupKey() {
    return mString;
  }
}
