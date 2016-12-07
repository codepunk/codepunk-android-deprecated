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

package com.codepunk.codepunkshell.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.text.TextUtils;

import java.util.Arrays;

/**
 * Utility class that provides useful {@link AccountManager}-related methods.
 */
@SuppressWarnings("WeakerAccess")
public class AccountManagerUtils {

  /**
   * Finds an {@link Account} by name from an array of Accounts.
   * @param accounts An array of Accounts.
   * @param accountName The name of the Account you wish to find.
   * @return The matching {@link Account}, or null if none was found.
   */
  public static Account findAccountByName(Account[] accounts, final String accountName) {
    if (accounts == null || accountName == null) {
      return null;
    }

    final Predicate<Account> predicate =
        new Predicate<Account>() {
          @Override
          public boolean apply(Account account) {
            return TextUtils.equals(account.name, accountName);
          }
        };
    return Iterables.tryFind(Arrays.asList(accounts), predicate).orNull();
  }

  /**
   * Returns the {@link Account} that matches the supplied name and account type.
   * @param accountManager An {@link AccountManager} instance.
   * @param accountName The name of the {@link Account} you are searching for.
   * @param accountType The account type of the {@link Account} you are searching for.
   * @return The {@link Account} that matches the supplied name and account type, or null if
   * no account matched the supplied name and account type.
   */
  @SuppressWarnings({"MissingPermission"})
  public static Account getAccountByNameAndType(
      AccountManager accountManager,
      String accountName,
      String accountType) {
    // Regarding GET_ACCOUNTS permission: According to the documentation,
    // 'Note: Beginning with Android 6.0 (API level 23), if an app shares the signature of the
    // authenticator that manages an account, it does not need "GET_ACCOUNTS" permission to read
    // information about that account. On Android 5.1 and lower, all apps need "GET_ACCOUNTS"
    // permission to read information about any account.'
    // We should never need to check for this permission, because it is required for all versions
    // < API 23 as per the manifest, and for any version >= API 23 (in which the user can turn
    // individual runtime permissions on/off), it is no longer required.
    return findAccountByName(
        accountManager.getAccountsByType(accountType),
        accountName);
  }

  /**
   * Private constructor.
   */
  private AccountManagerUtils() {
  }
}
