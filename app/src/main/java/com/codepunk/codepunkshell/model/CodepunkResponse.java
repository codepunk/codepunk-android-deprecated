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

package com.codepunk.codepunkshell.model;

import com.google.gson.annotations.SerializedName;

/* TODO Can probably return an appropriate response code with an error and then the
   situation described in the JavaDoc wouldn't occur. */

/**
 * Class that represents a Codepunk api response. This response can contain either a valid result
 * or an error. Since an error returned by the api would still constitute a "valid" response,
 * {@link com.codepunk.codepunkshell.api.CodepunkRequest} converts that into a
 * {@link com.android.volley.VolleyError} as appropriate.
 * @param <T> The type of parsed response this response wraps.
 */
@SuppressWarnings({"unused"})
public class CodepunkResponse<T> {

  /**
   * The Codepunk response result, if one exists.
   */
  @SerializedName("result")
  private T mResult;

  /**
   * The Codepunk response error, if one exists.
   */
  @SerializedName("error")
  private Error mError;

  /**
   * Returns the Codepunk response result if one exists, or null otherwise.
   * @return The Codepunk response result.
   */
  public T getResult() {
    return mResult;
  }

  /**
   * Returns the Codepunk response error if one exists, or null otherwise.
   * @return The Codepunk response error.
   */
  public Error getError() {
    return mError;
  }

  /**
   * A class that represents an error returned by the Codepunk api.
   */
  public static class Error {
    // TODO
  }
}
