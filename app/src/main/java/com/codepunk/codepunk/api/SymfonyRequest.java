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

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

// TODO Things I might want to do:
// * Instead of CodepunkRequest inheriting from SymfonyRequest, just move
//   SymfonyRequest.parseNetworkError to CodepunkRequest.

/**
 * <p>
 * Base class for all Symfony requests.
 * </p>
 * @param <T> The type of parsed response this request expects.
 */
@SuppressWarnings({"unused", "WeakerAccess", "StatementWithEmptyBody"})
public class SymfonyRequest<T> extends GsonRequest<T> {

  /**
   * Default charset for JSON request
   */
  private static final String PROTOCOL_CHARSET = "utf-8";

  /**
   * Creates a new request with the given method (one of the values from
   * {@link com.android.volley.Request.Method}), URL, response class, headers, params,
   * responseListener and error responseListener.
   * @param method The request method.
   * @param url URL of this request.
   * @param responseClass The class of T, for Gson's reflection.
   * @param headers A Map of extra HTTP headers to go along with this request.
   * @param params A Map of POST parameters to be used for this request, or null if
   * a simple GET should be used.
   * @param responseListener Listener interface for successful responses.
   * @param errorListener Listener interface for errors.
   */
  public SymfonyRequest(
      int method,
      String url,
      Class<T> responseClass,
      Map<String, String> headers,
      Map<String, String> params,
      Listener<T> responseListener,
      ErrorListener errorListener) {
    super(method, url, responseClass, headers, params, responseListener, errorListener);
  }

  /**
   * Creates a new request with the given method (one of the values from
   * {@link com.android.volley.Request.Method}), URL, response type, headers, params,
   * responseListener and error responseListener.
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
   * @param responseListener Listener interface for successful responses.
   * @param errorListener Listener interface for errors.
   */
  public SymfonyRequest(
      int method,
      String url,
      Type responseType,
      Map<String, String> headers,
      Map<String, String> params,
      Listener<T> responseListener,
      ErrorListener errorListener) {
    super(method, url, responseType, headers, params, responseListener, errorListener);
  }



  /**
   * Parses 'networkError' and returns a more specific error.
   * @param volleyError the error retrieved from the network
   * @return an NetworkError augmented with additional information
   */
  @Override
  protected VolleyError parseNetworkError(VolleyError volleyError) {
    NetworkResponse response = volleyError.networkResponse;
    if (response == null) {
      return volleyError;
    }



    // TODO Anything with volleyError.networkResponse.statusCode?

    try {
      String responseString = new String(response.data,
          HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

      // TODO isHtml? isJson?
      boolean isJson = true; // TODO TEMP
      boolean isHtml = false;

      if (isJson) {
        AuthError error =
            sGson.fromJson(responseString, AuthError.class);
        AuthErrorType errorType = error.getType();
        if (errorType != null) {
          return new SymfonyError(errorType, error.getDescription(), volleyError);
        }
      } else if (isHtml) {
        // TODO
      } else {
        // TODO If we got here, it's a regular string
      }

      return volleyError;
    } catch (UnsupportedEncodingException e) {
      return new ParseError(e);
    } catch (IllegalStateException e) {
      return volleyError;
    }
  }
}
