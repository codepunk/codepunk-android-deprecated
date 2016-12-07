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

package com.codepunk.codepunkshell.api;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.codepunk.codepunkshell.model.CodepunkResponse;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * <p>
 * Base class for all Codepunk requests. Codepunk requests deliver responses that contain
 * either a {@code result} object or an {@code error} object.
 * </p>
 * @param <T> The type of parsed response this request expects.
 * @see CodepunkResponse
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CodepunkRequest<T> extends SymfonyRequest<CodepunkResponse<T>> {

  /**
   * Helper method that looks for an {@code error} object in the response and delivers an
   * error if one is found.
   * @param response The parsed response from the network.
   * @param listener Listener interface for successful responses.
   * @param errorListener Listener interface for errors.
   * @param <T> The type of parsed response this request expects.
   */
  private static <T> void handleResponse(
      CodepunkResponse<T> response,
      Listener<T> listener, ErrorListener errorListener) {
    CodepunkResponse.Error error = response.getError();
    if (error != null) {
      if (errorListener != null) {
        VolleyError volleyError = new VolleyError(); // TODO Make volleyError meaningful
        errorListener.onErrorResponse(volleyError);
      }
    } else if (listener != null) {
      listener.onResponse(response.getResult());
    }
  }

  /**
   * Creates a new request with the given method (one of the values from
   * {@link com.android.volley.Request.Method}), URL, response class, headers, params, listener
   * and error listener.
   * @param method The request method.
   * @param url URL of this request.
   * @param responseClass The class of T, for Gson's reflection.
   * @param headers A list of extra HTTP headers to go along with this request.
   * @param params A Map of POST parameters to be used for this request, or null if
   * a simple GET should be used.
   * @param listener Listener interface for successful responses.
   * @param errorListener Listener interface for errors.
   */
  public CodepunkRequest(
      int method,
      String url,
      Class<CodepunkResponse<T>> responseClass,
      Map<String, String> headers,
      Map<String, String> params,
      final Listener<T> listener,
      final ErrorListener errorListener) {
    super(
        method,
        url,
        responseClass,
        headers,
        params,
        new Listener<CodepunkResponse<T>>() {
          @Override
          public void onResponse(CodepunkResponse<T> response) {
            handleResponse(response, listener, errorListener);
          }
        },
        errorListener);
  }

  /**
   * Creates a new request with the given method (one of the values from
   * {@link com.android.volley.Request.Method}), URL, response type, headers, params, listener and
   * error listener.
   * @param method The request method.
   * @param url URL of this request.
   * @param responseType The specific genericized type of T, for Gson's reflection. You can obtain
   *                     this type by using the {@link com.google.gson.reflect.TypeToken} class.
   *                     For example, to get the type for {@code Collection<Foo>}, you should use:
   *                     <pre>
   *                     Type responseType = new TypeToken&lt;Collection&lt;Foo&gt;&gt;(){}
   *                     .getType();
   *                     </pre>
   * @param headers A list of extra HTTP headers to go along with this request.
   * @param params A Map of POST parameters to be used for this request, or null if
   * a simple GET should be used.
   * @param listener Listener interface for successful responses.
   * @param errorListener Listener interface for errors.
   */
  public CodepunkRequest(
      int method,
      String url,
      Type responseType,
      Map<String, String> headers,
      Map<String, String> params,
      final Listener<T> listener,
      final ErrorListener errorListener) {
    super(
        method,
        url,
        responseType,
        headers,
        params,
        new Listener<CodepunkResponse<T>>() {
          @Override
          public void onResponse(CodepunkResponse<T> response) {
            handleResponse(response, listener, errorListener);
          }
        },
        errorListener);
  }
}
