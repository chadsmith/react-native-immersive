package com.rnimmersive;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * {@link NativeModule} that allows changing the appearance of the menu bar.
 */
public class RNImmersiveModule extends ReactContextBaseJavaModule {
  private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
  private static final String ERROR_NO_ACTIVITY_MESSAGE = "Tried to set immersive while not attached to an Activity";
  private static final int UI_FLAG_IMMERSIVE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

  private boolean _isImmersiveOn = false;

  public RNImmersiveModule(ReactApplicationContext reactContext) {
    super(reactContext);
    Activity activity = getCurrentActivity();
    if(activity != null) {
      activity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
        boolean isImmersiveOn = 0 != (visibility & UI_FLAG_IMMERSIVE);
        if (isImmersiveOn != _isImmersiveOn) {
          _isImmersiveOn = isImmersiveOn;
          emitImmersiveStateChangeEvent(isImmersiveOn);
        }
      });
    }
  }

  @Override
  public String getName() {
    return "RNImmersive";
  }

  @ReactMethod
  public void setImmersive(final boolean isOn, final Promise res) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      res.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(() -> {
        _isImmersiveOn = isOn;
        activity.getWindow().getDecorView().setSystemUiVisibility(isOn ? UI_FLAG_IMMERSIVE : View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        res.resolve(null);
      });
    }
  }
  @ReactMethod
  public void getImmersive(final Promise res) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      res.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(() -> {
        int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        boolean isImmersiveOn = 0 != (visibility & UI_FLAG_IMMERSIVE);
        res.resolve(isImmersiveOn);
      });
    }
  }

  public void emitImmersiveStateChangeEvent(Boolean state) {
    ReactContext context = getReactApplicationContext();
    if (context != null) {
      context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("@@IMMERSIVE_STATE_CHANGED", state);
    }
  }
}
