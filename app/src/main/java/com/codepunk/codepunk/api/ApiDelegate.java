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

import android.net.Uri;

import com.codepunk.codepunk.app.SessionManager;
import com.codepunk.codepunk.app.VolleyManager;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * This class serves as the base delegate for api calls made via the {@link Api} class.
 * </p>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
abstract class ApiDelegate {

  /**
   * The header key for auth-enabled api calls.
   */
  static final String HEADER_KEY_AUTHORIZATION = "Authorization";

  /**
   * The format used when building the header value for auth-enabled api calls.
   */
  private static final String HEADER_VALUE_AUTHORIZATION_FORMAT = "Bearer %s";

  /**
   * The error message passed when
   */
  protected static final String NO_ACCOUNT_MSG = "No account has been authorized in SessionManager";

  /**
   * A convenience method that builds the header value for auth-enabled api calls.
   * @param authToken The authorization token.
   * @return The header value for auth-enabled api calls.
   */
  static String getAuthorizationValue(String authToken) {
    return String.format(Locale.US, HEADER_VALUE_AUTHORIZATION_FORMAT, authToken);
  }

  /**
   * The {@link Api} instance that controls this delegate.
   */
  protected final Api mApi;

  /**
   * The {@link SessionManager} instance used for getting the current
   * {@link com.codepunk.codepunk.app.Environment}.
   */
  protected final SessionManager mSessionManager;

  /**
   * The {@link VolleyManager} instance used to make api calls.
   */
  protected final VolleyManager mVolleyManager;

  /**
   * Constructor that takes an {@link Api} instance.
   * @param api The {@link Api} instance that controls this delegate.
   */
  ApiDelegate(Api api) {
    mApi = api;
    mSessionManager = SessionManager.getInstance();
    mVolleyManager = VolleyManager.getInstance(api.getContext());
  }

  /**
   * Builds the URL string for the given endpoint.
   * @param endpoint The base string that represents the endpoint.
   * @param queryParams A map of query parameters.
   * @return The URL string used to getAuthToken the api endpoint.
   */
  String buildUrl(String endpoint, Map<String, String> queryParams) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(mApi.getScheme())
        .encodedAuthority(mApi.getAuthority())
        .appendEncodedPath(mApi.getPath());
    builder.appendEncodedPath(endpoint);

    if (queryParams != null) {
      Set<String> keys = queryParams.keySet();
      for (String key : keys) {
        builder.appendQueryParameter(key, queryParams.get(key));
      }
    }

    // TODO Append all kinds of other things -- accessToken, etc.

    return builder.build().toString();
  }

  /**
   * Builds a URL string for an endpoint without query parameters.
   * @param endpoint The base string that represents the endpoint.
   * @return The URL string used to getAuthToken the api endpoint.
   */
  String buildUrl(String endpoint) {
    return buildUrl(endpoint, null);
  }
}
