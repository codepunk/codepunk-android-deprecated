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

import android.content.Context;

import com.codepunk.codepunk.app.Environment;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * This class serves as the means by which API calls are made. The concrete classes
 * ({@link ApiProduction}, {@link ApiDevelopment}, {@link ApiLocal} etc.) contain the specifics
 * required to communicate with the appropriate server based on the current {@link Environment}.
 * </p>
 *
 * <p>
 * To obtain an Api instance, use {@link com.codepunk.codepunk.app.ApiManager}:
 * <pre>
 * Context context = ...;
 * Environment environment = Environment.getCurrentEnvironment();
 * Api api = ApiManager.getInstance(context).get(environment);
 * </pre>
 * </p>
 *
 * <p>
 * The actual api endpoint methods are located in the various {@link ApiDelegate} classes. You
 * can obtain instances of these classes using the get methods (i.e. {@link Api#getAuthDelegate()},
 * {@link Api#getUserDelegate()}, etc.) or by using the matching convenience methods in
 * {@link com.codepunk.codepunk.app.ApiManager} directly. The following two examples both
 * yield the same result:
 * <pre>
 * ApiAuthDelegate delegate =
 *     ApiManager.getInstance(context)
 *         .get(Environment.getCurrentEnvironment())
 *         .getAuthDelegate();
 *
 * ApiAuthDelegate delegate = ApiManager.getInstance(context).getAuthDelegate();
 * </pre>
 * </p>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Api {

  /**
   * Default timeout duration for api calls.
   */
  public static final int DEFAULT_TIMEOUT_DURATION = 30;

  /**
   * Default timeout time unit for api calls.
   */
  public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

  /**
   * The {@link Context} associated with this class.
   */
  private Context mContext;

  /**
   * The {@link ApiDelegate} associated with authorization api calls.
   */
  private ApiAuthDelegate mApiAuthDelegate;

  /**
   * The {@link ApiDelegate} associated with user api calls.
   */
  private ApiUserDelegate mApiUserDelegate;

  /**
   * Simple constructor that saves a {@link Context}. Don't create this directly;
   * use the {@link Api#newInstance(Context, Environment)} method instead.
   * @param context The {@link Context} to associate with this Api instance.
   */
  protected Api(Context context) {
    mContext = context.getApplicationContext();
  }

  /**
   * Creates a new Api instance based on the given {@link Environment}.
   * @param context the Context associated with this Api instance
   * @param environment the Environment to use to create this Api instance
   * @return Api instance to use for making api calls
   */
  public static Api newInstance(Context context, Environment environment) {
    switch (environment) {
      case DEVELOPMENT:
        return new ApiDevelopment(context);
      case LOCAL:
        return new ApiLocal(context);
      case PRODUCTION:
      default:
        return new ApiProduction(context);
    }
  }

  /**
   * Returns the context the Api instance is running in, through which it can
   * access the current theme, resources, etc.
   *
   * @return The Api instance's Context.
   */
  public Context getContext() {
    return mContext;
  }

  /**
   * Returns the Api instance's URL scheme.
   *
   * @return The Api instance's URL scheme.
   */
  public abstract String getScheme();

  /**
   * Returns the Api instance's URL authority.
   *
   * @return The Api instance's URL authority.
   */
  public abstract String getAuthority();

  /**
   * Returns the Api instance's URL path.
   *
   * @return The Api instance's URL path.
   */
  public abstract String getPath();

  /**
   * Returns a reference to an {@link ApiAuthDelegate} instance, creating a new one if
   * necessary.
   * @return An ApiAuthDelegate instance.
   */
  public ApiAuthDelegate getAuthDelegate() {
    if (mApiAuthDelegate == null) {
      mApiAuthDelegate = new ApiAuthDelegate(this);
    }
    return mApiAuthDelegate;
  }

  /**
   * Returns a reference to an {@link ApiUserDelegate} instance, creating a new one if
   * necessary.
   * @return An ApiUserDelegate instance.
   */
  public ApiUserDelegate getUserDelegate() {
    if (mApiUserDelegate == null) {
      mApiUserDelegate = new ApiUserDelegate(this);
    }
    return mApiUserDelegate;
  }

  /**
   * A concrete Api class that points to the development environment.
   */
  public static class ApiDevelopment extends Api {
    public ApiDevelopment(Context context) {
      super(context);
    }

    @Override
    public String getScheme() {
      return "http";
    }

    @Override
    public String getAuthority() {
      return "dev.codepunk.com";
    }

    @Override
    public String getPath() {
      return "";
    }
  }

  /**
   * A concrete Api class that points to the production environment.
   */
  public static class ApiProduction extends Api {
    public ApiProduction(Context context) {
      super(context);
    }

    @Override
    public String getScheme() {
      return "https";
    }

    @Override
    public String getAuthority() {
      return "www.codepunk.com";
    }

    @Override
    public String getPath() {
      return "";
    }
  }

  /**
   * A concrete Api class that points to the local environment.
   */
  public static class ApiLocal extends Api {
    public ApiLocal(Context context) {
      super(context);
    }

    @Override
    public String getScheme() {
      return "http";
    }

    @Override
    public String getAuthority() {
      return "10.0.2.2:8888"; // TODO Emulator vs. Device vs. Genymotion?
    }

    @Override
    public String getPath() {
      return "app_local.php";
    }
  }
}
