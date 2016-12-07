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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.codepunk.codepunklib.util.CollectionUtils;
import com.codepunk.codepunklib.util.log.FormattingLogger;
import com.codepunk.codepunklib.util.log.LogManager;
import com.codepunk.codepunk.app.SessionManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * <p>
 * Base class for all Gson requests.
 * </p>
 * @param <T> The type of parsed response this request expects.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class GsonRequest<T> extends Request<T> {

  /**
   * Common {@link Gson} instance used by all requests.
   */
  protected static final Gson sGson =
      new GsonBuilder()
          .setPrettyPrinting()
          // .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
          .create();

  /**
   * Common {@link JsonParser} instance used by all requests.
   */
  protected static final JsonParser sJsonParser = new JsonParser();

  /**
   * Common {@link SessionManager} instance used by all requests.
   */
  protected static final SessionManager sSessionManager = SessionManager.getInstance();

  /**
   * Common {@link LogManager} instance used by all requests.
   */
  protected static final LogManager sLogManager = LogManager.getInstance();

  /**
   * A Map of extra HTTP headers to go along with this request.
   */
  private final Map<String, String> mHeaders;

  /**
   * A Map of parameters to be used for a POST or PUT request.
   */
  private final Map<String, String> mParams;

  /**
   * The class of T, for Gson's reflection.
   */
  private final Class<T> mResponseClass;

  /**
   * The specific genericized type of T, for Gson's reflection.
   */
  private final Type mResponseType;

  /**
   * Listener interface for successful responses.
   */
  private final Response.Listener<T> mResponseListener;

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
  public GsonRequest(
      int method,
      String url,
      Class<T> responseClass,
      Map<String, String> headers,
      Map<String, String> params,
      Listener<T> responseListener,
      ErrorListener errorListener) {
    super(method, url, errorListener);
    mResponseClass = responseClass;
    mResponseType = null;
    mHeaders = headers;
    mParams = params;
    mResponseListener = responseListener;
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
  public GsonRequest(
      int method,
      String url,
      Type responseType,
      Map<String, String> headers,
      Map<String, String> params,
      Listener<T> responseListener,
      ErrorListener errorListener) {
    super(method, url, errorListener);
    mResponseClass = null;
    mResponseType = responseType;
    mHeaders = headers;
    mParams = params;
    mResponseListener = responseListener;
  }

  /**
   * Returns a Map of extra HTTP headers to go along with this request. Can
   * throw {@link AuthFailureError} as authentication may be required to
   * provide these values.
   * @return A Map of extra HTTP headers to go along with this request.
   * @throws AuthFailureError In the event of auth failure.
   */
  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
    return mHeaders == null ? super.getHeaders() : mHeaders;
  }

  /**
   * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
   * {@link AuthFailureError} as authentication may be required to provide these values.
   *
   * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
   *
   * @return A Map of parameters to be used for a POST or PUT request.
   * @throws AuthFailureError in the event of auth failure.
   */
  @Override
  protected Map<String, String> getParams() throws AuthFailureError {
    return mParams == null ? super.getParams() : mParams;
  }

  @Override
  public Request<?> setRequestQueue(RequestQueue requestQueue) {
    Request<?> result = super.setRequestQueue(requestQueue);
    FormattingLogger logger = getLogger();
    if (logger.isLoggable(Log.VERBOSE)) {
      StringBuilder builder = new StringBuilder("request=\n")
          .append("url=")
          .append(getUrl());
      if (!CollectionUtils.isEmpty(mParams)) {
        builder.append("\nparams=")
            .append(sGson.toJson(mParams));
      }
      if (!CollectionUtils.isEmpty(mHeaders)) {
        builder.append("\nheaders=")
            .append(sGson.toJson(mHeaders));
      }
      logger.v(builder.toString());
    }
    return result;
  }

  /**
   * Parses the raw network response and returns the appropriate response type. The response will
   * not be delivered if you return null.
   * @param response Response from the network.
   * @return The parsed response, or null in the case of an error.
   */
  @Override
  protected Response<T> parseNetworkResponse(NetworkResponse response) {
    try {
      T deserializedResponse = deserializeResponse(response);
      return Response.success(deserializedResponse,
          HttpHeaderParser.parseCacheHeaders(response));
    } catch (UnsupportedEncodingException e) {
      return Response.error(new ParseError(e));
    } catch (JsonSyntaxException e) {
      return Response.error(new ParseError(e));
    }
  }

  /**
   * Performs delivery of the parsed response to the listener(s). The given response is guaranteed
   * to be non-null; responses that fail to parse are not delivered.
   * @param response The parsed response returned by {@link #parseNetworkResponse(NetworkResponse)}.
   */
  @Override
  protected void deliverResponse(T response) {
    mResponseListener.onResponse(response);
  }

  /**
   * Convenience method for getting the {@link FormattingLogger} based on the current environment.
   * @return The FormattingLogger based on the current environment.
   */
  private static FormattingLogger getLogger() {
    return sLogManager.get(sSessionManager.getEnvironment().getLoggerLevel());
  }

  /**
   * Deserializes a raw {@link NetworkResponse} into the class specified by either mResponseClass
   * or mResponseType, depending on which version of the constructor was used.
   * @param response The {@link NetworkResponse} to deserialize
   * @return An object of type T (the type of parsed response this request expects).
   * @throws UnsupportedEncodingException If the parsing to a JSON string fails.
   * @throws JsonSyntaxException If the Gson deserialization fails.
   */
  private T deserializeResponse (NetworkResponse response)
      throws UnsupportedEncodingException, JsonSyntaxException {
    String json = new String(
        response.data,
        HttpHeaderParser.parseCharset(response.headers));
    JsonElement jsonElement = sJsonParser.parse(json);
    FormattingLogger logger = getLogger();
    if (logger.isLoggable(Log.VERBOSE)) {
      logger.v("response=\n" + sGson.toJson(jsonElement));
    }
    if (mResponseClass != null) {
      return sGson.fromJson(jsonElement, mResponseClass);
    } else if (mResponseType != null) {
      return sGson.fromJson(jsonElement, mResponseType);
    } else {
      throw new IllegalStateException();
    }
  }

  /**
   * Returns the listener interface for successful responses.
   * @return The listener interface for successful responses.
   */
  public Listener<T> getResponseListener() {
    return mResponseListener;
  }
}
