package com.geoai.msdk_ai;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.secneo.sdk.Helper;

import dji.v5.common.error.IDJIError;
import dji.v5.common.register.DJISDKInitEvent;
import dji.v5.manager.SDKManager;
import dji.v5.manager.interfaces.SDKManagerCallback;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        Helper.install(getApplication());

        SDKManager.getInstance().init(this, new SDKManagerCallback() {
            @Override
            public void onRegisterSuccess() {
                Log.i("Ronny", "register success");
                if (SDKManager.getInstance().isRegistered()) {
                    return;
                }
                SDKManager.getInstance().registerApp();
            }

            @Override
            public void onRegisterFailure(IDJIError error) {
                Log.i("Ronny", "register failure: " + error.description());
            }

            @Override
            public void onProductDisconnect(int productId) {

            }

            @Override
            public void onProductConnect(int productId) {

            }

            @Override
            public void onProductChanged(int productId) {

            }

            @Override
            public void onInitProcess(DJISDKInitEvent event, int totalProcess) {
                if (event == DJISDKInitEvent.INITIALIZE_COMPLETE) {
                    SDKManager.getInstance().registerApp();
                }
            }

            @Override
            public void onDatabaseDownloadProgress(long current, long total) {
            }
        });
    }

    public abstract int getContentView();
}
