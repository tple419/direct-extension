package myview.android.library.extensions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

import myview.android.library.extensions.R;
import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.HydraTransportConfig;
import unified.vpn.sdk.OpenVpnTransportConfig;
import unified.vpn.sdk.SdkNotificationConfig;
import unified.vpn.sdk.TransportConfig;
import unified.vpn.sdk.UnifiedSdk;

public class MyApplication extends MultiDexApplication {
    private static final String CHANNELid = "vpn";
    private static MyApplication instance;

    public static void loadAds(OnInitializationCompleteListener listener) {
        MobileAds.initialize(
                instance,
                initializationStatus -> {
                    listener.onInitializationComplete(initializationStatus);
                    new AppOpenManager(instance);
                    new Native_Ads_Load(instance);
                });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AudienceNetworkAds.initialize(this);
        inithydra_Sdk();
    }

    private void inithydra_Sdk() {
        CreateNotification_Channel();
        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());
        UnifiedSdk.update(transportConfigList, CompletableCallback.EMPTY);

        SdkNotificationConfig notificationConfig = SdkNotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNELid)
                .build();
        UnifiedSdk.update(notificationConfig);

        UnifiedSdk.setLoggingLevel(Log.VERBOSE);
    }

    private void CreateNotification_Channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int Importance = NotificationManager.IMPORTANCE_DEFAULT;
            String desc = "VPN Notification";
            CharSequence notification_name = "Sample VPN";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNELid, notification_name, Importance);
            notificationChannel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}
