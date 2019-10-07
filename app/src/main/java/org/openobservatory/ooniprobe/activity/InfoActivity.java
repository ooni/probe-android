package org.openobservatory.ooniprobe.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import org.openobservatory.ooniprobe.BuildConfig;

public class InfoActivity extends Activity implements DefaultHardwareBackBtnHandler {
	private ReactRootView mReactRootView;
	private ReactInstanceManager mReactInstanceManager;
	private final int OVERLAY_PERMISSION_REQ_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SoLoader.init(this, false);
		mReactRootView = new ReactRootView(this);
		mReactInstanceManager = ReactInstanceManager.builder()
				.setApplication(getApplication())
				.setCurrentActivity(this)
				.setBundleAssetName("index.android.bundle")
				.setJSMainModulePath("index")
				.addPackage(new MainReactPackage())
				.setUseDeveloperSupport(BuildConfig.DEBUG)
				.setInitialLifecycleState(LifecycleState.RESUMED)
				.build();
		// The string here (e.g. "MyReactNativeApp") has to match
		// the string in AppRegistry.registerComponent() in index.js
		mReactRootView.startReactApplication(mReactInstanceManager, "HelloWorld", null);

		setContentView(mReactRootView);

	}

	@Override
	public void invokeDefaultOnBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mReactInstanceManager != null) {
			mReactInstanceManager.onHostPause(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mReactInstanceManager != null) {
			mReactInstanceManager.onHostResume(this, this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mReactInstanceManager != null) {
			mReactInstanceManager.onHostDestroy(this);
		}
		if (mReactRootView != null) {
			mReactRootView.unmountReactApplication();
		}
	}

	@Override
	public void onBackPressed() {
		if (mReactInstanceManager != null) {
			mReactInstanceManager.onBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
			mReactInstanceManager.showDevOptionsDialog();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}