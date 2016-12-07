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

import android.annotation.SuppressLint;
import android.content.Context;

import com.codepunk.codepunklib.support.v1.ObjectsCompat;
import com.codepunk.codepunklib.util.plugin.PluginManager;
import com.codepunk.codepunkshell.api.Api;
import com.codepunk.codepunkshell.api.ApiAuthDelegate;
import com.codepunk.codepunkshell.api.ApiUserDelegate;

/**
 * Manager class that manages {@link Api} instances based on the current {@link Environment}.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ApiManager extends PluginManager<Api, Environment> {

  /**
   * For singleton creation.
   */
  private static final Object sLock = new Object();

  /**
   * The singleton instance.
   */
  @SuppressLint("StaticFieldLeak")
  private static ApiManager sInstance;

  /**
   * The singleton {@link SessionManager} instance.
   */
  private static SessionManager sSessionManager = SessionManager.getInstance();

  /**
   * The application context.
   */
  private final Context mAppContext;

  /**
   * Constructor that accepts an application context. Use {@link ApiManager#getInstance(Context)}
   * to get the singleton instance.
   * @param context The context to use to get the application context.
   */
  private ApiManager(Context context) {
    mAppContext = context.getApplicationContext();
  }

  /**
   * Returns a singleton ApiManager instance associated with a Context.
   * @param context The context to use to create the ApiManager instance.
   * @return An ApiManager instance.
   */
  public static ApiManager getInstance(Context context) {
    synchronized (sLock) {
      if (sInstance == null) {
        sInstance = new ApiManager(context);
      }
      return sInstance;
    }
  }

  /**
   * Creates a new {@link Api} instance when necessary.
   * @param env The {@link Environment} to use to create the new instance.
   * @return An {@link Api} instance.
   */
  @Override
  protected Api newPlugin(Environment env) {
    return Api.newInstance(mAppContext, env);
  }

  /**
   * Determines if the current {@link Api} plugin instance is dirty and needs to be recreated.
   * @param api The current {@link Api} plugin instance.
   * @param oldEnv The {@link Environment} used to create the current instance.
   * @param env The {@link Environment} to check against the existing Environment.
   * @return True if the plugin is dirty and needs to be recreated.
   */
  @Override
  protected boolean isPluginDirty(Api api, Environment oldEnv, Environment env) {
    return !ObjectsCompat.equals(oldEnv, env);
  }

  /**
   * Convenience method to get an {@link Api} instance based on the current {@link Environment}.
   * @return An {@link Api} instance.
   */
  public Api get() {
    return get(sSessionManager.getEnvironment());
  }

  /**
   * Convenience method to get an {@link ApiAuthDelegate} instance based on the current
   * {@link Environment}.
   * @return An {@link ApiAuthDelegate} instance.
   */
  public ApiAuthDelegate getAuthDelegate() {
    return get().getAuthDelegate();
  }

  /**
   * Convenience method to get an {@link ApiUserDelegate} instance based on the current
   * {@link Environment}.
   * @return An {@link ApiUserDelegate} instance.
   */
  public ApiUserDelegate getUserDelegate() {
    return get().getUserDelegate();
  }
}
