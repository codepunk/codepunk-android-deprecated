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

import com.google.common.collect.ImmutableMap;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.codepunk.codepunk.app.Environment;
import com.codepunk.codepunk.model.AuthInfo;

/**
 * <p>
 * An {@link ApiDelegate} class that handles api calls related to authorization.
 * </p>
 */
public class ApiAuthDelegate extends ApiDelegate {

  /**
   * The base endpoint for get auth token api calls.
   */
  private static final String ENDPOINT_GET_AUTH_TOKEN = "oauth/v2/token";

  private static final String QUERY_PARAM_CLIENT_ID = "client_id";
  private static final String QUERY_PARAM_CLIENT_SECRET = "client_secret";
  private static final String QUERY_PARAM_GRANT_TYPE = "grant_type";
  private static final String QUERY_PARAM_PASSWORD = "password";
  private static final String QUERY_PARAM_REFRESH_TOKEN = "refresh_token";
  private static final String QUERY_PARAM_USERNAME = "username";

  /**
   * Constructor that takes an {@link Api} instance.
   * @param api The {@link Api} instance that controls this delegate.
   */
  ApiAuthDelegate(Api api) {
    super(api);
  }

  /**
   * Calls the api endpoint for retrieving an authorization token.
   * @param username The username used to generate the authorization token.
   * @param password The password used to generate the authorization token.
   * @param listener A {@link Listener} that will wait for a successful api response.
   * @param errorListener A {@link ErrorListener} that will wait for an api response error.
   */
  public void getAuthToken(
      String username,
      String password,
      final Listener<AuthInfo> listener,
      final ErrorListener errorListener) {
    final Environment environment = mSessionManager.getEnvironment();
    final String url = buildUrl(ENDPOINT_GET_AUTH_TOKEN);
    final ImmutableMap<String, String> params = new ImmutableMap.Builder<String, String>()
        .put(QUERY_PARAM_GRANT_TYPE, AuthGrantType.USER_CREDENTIALS.getValue())
        .put(QUERY_PARAM_CLIENT_ID, environment.getClientId())
        .put(QUERY_PARAM_CLIENT_SECRET, environment.getClientSecret())
        .put(QUERY_PARAM_USERNAME, username)
        .put(QUERY_PARAM_PASSWORD, password)
        .build();
    final SymfonyRequest<AuthInfo> request =
        new SymfonyRequest<>(
            Request.Method.POST,
            url,
            AuthInfo.class,
            null,
            params,
            listener,
            errorListener);
    mVolleyManager.addToRequestQueue(request);
  }

  /**
   * Calls the api endpoint for refreshing an authorization token.
   * @param refreshToken The refreshToken used to generate the new authorization token.
   * @param listener A {@link Listener} that will wait for a successful api response.
   * @param errorListener A {@link ErrorListener} that will wait for an api response error.
   */
  public void refreshToken(
      String refreshToken,
      final Listener<AuthInfo> listener,
      final ErrorListener errorListener) {
    final Environment environment = mSessionManager.getEnvironment();
    final String url = buildUrl(ENDPOINT_GET_AUTH_TOKEN);
    final ImmutableMap<String, String> params = new ImmutableMap.Builder<String, String>()
        .put(QUERY_PARAM_GRANT_TYPE, AuthGrantType.REFRESH_TOKEN.getValue())
        .put(QUERY_PARAM_CLIENT_ID, environment.getClientId())
        .put(QUERY_PARAM_CLIENT_SECRET, environment.getClientSecret())
        .put(QUERY_PARAM_REFRESH_TOKEN, refreshToken)
        .build();
    final SymfonyRequest<AuthInfo> request =
        new SymfonyRequest<>(
            Request.Method.POST,
            url,
            AuthInfo.class,
            null,
            params,
            listener,
            errorListener);
    mVolleyManager.addToRequestQueue(request);
  }
}
