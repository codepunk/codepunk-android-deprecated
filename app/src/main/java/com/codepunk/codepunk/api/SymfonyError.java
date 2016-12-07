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
import com.android.volley.VolleyError;

/**
 * A Symfony-specific version of VolleyError that includes an {@link AuthErrorType}.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SymfonyError extends VolleyError {

  /**
   * The auth error type associated with this error.
   */
  private AuthErrorType mErrorType;

  SymfonyError(AuthErrorType errorType) {
    super();
    mErrorType = errorType;
  }

  SymfonyError(AuthErrorType errorType, NetworkResponse response) {
    super(response);
    mErrorType = errorType;
  }

  SymfonyError(AuthErrorType errorType, String exceptionMessage) {
    super(exceptionMessage);
    mErrorType = errorType;
  }

  SymfonyError(AuthErrorType errorType, String exceptionMessage, Throwable reason) {
    super(exceptionMessage, reason);
    mErrorType = errorType;
  }

  SymfonyError(AuthErrorType errorType, Throwable cause) {
    super(cause);
    mErrorType = errorType;
  }

  /**
   * Returns the auth error type.
   * @return The auth error type.
   */
  public AuthErrorType getErrorType() {
    return mErrorType;
  }
}
