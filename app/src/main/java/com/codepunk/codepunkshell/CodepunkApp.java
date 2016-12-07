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

package com.codepunk.codepunkshell;

import android.app.Application;

import com.codepunk.codepunklib.util.log.FormattingLogger.Placeholder;
import com.codepunk.codepunklib.util.log.LogManager;

/**
 * An {@link Application} class that does some basic initialization.
 */
public class CodepunkApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize LogManager
    LogManager logManager = LogManager.getInstance();
    logManager.setTagFormat("CP|%s", Placeholder.SIMPLE_CLASS_NAME);
  }
}
