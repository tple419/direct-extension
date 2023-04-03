package myview.android.library.extensions;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AppService extends Service {

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
        //here you will get call when app close.
        try {
            if (!new AppPreference(this).isFound()) {
                VpnActivity.stopvpn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (!new AppPreference(this).isFound()) {
                VpnActivity.stopvpn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
