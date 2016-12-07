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
import com.google.gson.reflect.TypeToken;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.codepunk.codepunk.model.CodepunkResponse;
import com.codepunk.codepunk.model.User;

import java.lang.reflect.Type;

/**
 * <p>
 * An {@link ApiDelegate} class that handles user-related api calls.
 * </p>
 */
public class ApiUserDelegate extends ApiDelegate {

  /**
   * The base endpoint for the get authenticated user api call.
   */
  private static final String ENDPOINT_GET_AUTHENTICATED_USER =
      "api/v1/authenticated_user/get.json";

  /**
   * The {@link User} type used to delver a User instance back to the caller.
   */
  private static final Type USER_TYPE = new TypeToken<CodepunkResponse<User>>() {}.getType();

  /**
   * Constructor that takes an {@link Api} instance.
   * @param api The {@link Api} instance that controls this delegate.
   */
  ApiUserDelegate(Api api) {
    super(api);
  }

  /**
   * Calls the api endpoint for retrieving the authenticated user.
   * @param authToken The authorization token.
   * @param listener A {@link Listener} that will wait for a successful api response.
   * @param errorListener A {@link ErrorListener} that will wait for an api response error.
   */
  public void getAuthenticatedUser(
      final String authToken,
      final Listener<User> listener,
      final ErrorListener errorListener) {
        final ImmutableMap<String, String> headers =
            new ImmutableMap.Builder<String, String>()
                .put(HEADER_KEY_AUTHORIZATION, getAuthorizationValue(authToken))
                .build();
        final CodepunkRequest<User> request =
            new CodepunkRequest<>(
                Request.Method.GET,
                buildUrl(ENDPOINT_GET_AUTHENTICATED_USER),
                USER_TYPE,
                headers,
                null /* params */,
                listener,
                errorListener);
        mVolleyManager.addToRequestQueue(request);
  }
}
