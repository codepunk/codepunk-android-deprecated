package com.codepunk.codepunkshell;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
  public AuthenticatorService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    AccountAuthenticator authenticator = new AccountAuthenticator(this);
    return authenticator.getIBinder();
  }
}
