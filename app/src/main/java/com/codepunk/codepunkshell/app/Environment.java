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

package com.codepunk.codepunkshell.app;

import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static com.codepunk.codepunkshell.app.PrivateAppConstants.PRODUCTION_CLIENT_ID;
import static com.codepunk.codepunkshell.app.PrivateAppConstants.PRODUCTION_CLIENT_SECRET;
import static com.codepunk.codepunkshell.app.PrivateAppConstants.DEVELOPMENT_CLIENT_ID;
import static com.codepunk.codepunkshell.app.PrivateAppConstants.DEVELOPMENT_CLIENT_SECRET;
import static com.codepunk.codepunkshell.app.PrivateAppConstants.LOCAL_CLIENT_ID;
import static com.codepunk.codepunkshell.app.PrivateAppConstants.LOCAL_CLIENT_SECRET;

/**
 * <p>
 * Class that represents a runtime environment (i.e. production, development, etc.). Each
 * of these environments has their own client ID and client secret for use in making api calls.
 * </p>
 *
 * <p>
 * Additionally, the Environment class can be used to configure how (or how often) logging occurs
 * as well as other app behaviors.
 * </p>
 */
public enum Environment {

  /**
   * Enum value representing the production runtime environment.
   */
  PRODUCTION(PRODUCTION_CLIENT_ID, PRODUCTION_CLIENT_SECRET, INFO),

  /**
   * Enum value representing the development runtime environment.
   */
  DEVELOPMENT(DEVELOPMENT_CLIENT_ID, DEVELOPMENT_CLIENT_SECRET, VERBOSE),

  /**
   * Enum value representing the local runtime environment.
   */
  LOCAL(LOCAL_CLIENT_ID, LOCAL_CLIENT_SECRET, VERBOSE);

  /**
   * The client ID used for making api calls.
   */
  private final String mClientId;

  /**
   * The client secret used for making api calls.
   */
  private final String mClientSecret;

  /**
   * The logger level for this runtime environment.
   */
  private final  int mLoggerLevel;

  /**
   * Default constructor.
   * @param clientId The client ID used for making api calls in this runtime environment.
   * @param clientSecret The client secret used for making api calls in this runtime environment.
   * @param loggerLevel The logger level for this runtime environment.
   */
  Environment(
      String clientId,
      String clientSecret,
      int loggerLevel) {
    mClientId = clientId;
    mClientSecret = clientSecret;
    mLoggerLevel = loggerLevel;
  }

  /**
   * Returns the client ID used for making api calls in this runtime environment.
   * @return The client ID.
   */
  public String getClientId() {
    return mClientId;
  }

  /**
   * Returns the client secret used for making api calls in this runtime environment.
   * @return The client secret.
   */
  public String getClientSecret() {
    return mClientSecret;
  }

  /**
   * Returns the logger level for this runtime environment.
   * @return The logger level.
   */
  public int getLoggerLevel() {
    return mLoggerLevel;
  }
}
