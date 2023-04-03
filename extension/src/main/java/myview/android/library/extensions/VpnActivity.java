package myview.android.library.extensions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import myview.android.library.extensions.R;
import unified.vpn.sdk.AuthMethod;
import unified.vpn.sdk.Callback;
import unified.vpn.sdk.ClientInfo;
import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.HydraTransport;
import unified.vpn.sdk.OpenVpnTransport;
import unified.vpn.sdk.SessionConfig;
import unified.vpn.sdk.TrackingConstants;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.User;
import unified.vpn.sdk.VpnException;
import unified.vpn.sdk.VpnState;

public class VpnActivity {
    static InstallReferrerClient referrerClient;
    public static String referrerUrl = "NA";
    private static Dialog dialog;
    private static boolean isDialogShow = false;

    public static boolean checkReferrer(String url, String medium) {
        String[] splitParts = medium.split(",");
        if (TextUtils.isEmpty(url)) {
            return true;
        }
        for (String abc : splitParts) {
            if (url.toLowerCase(Locale.getDefault()).contains(abc.toLowerCase(Locale.getDefault())))
                return true;
        }
        return false;
    }

    public static boolean checkIsOrganic(Activity activity) {
        AppPreference preference = new AppPreference(activity);
        String[] splitParts = preference.getMedium().split(",");
        if (TextUtils.isEmpty(preference.getReferrerUrl())) {
            return true;
        }
        for (String abc : splitParts) {
            if (preference.getReferrerUrl().toLowerCase(Locale.getDefault()).contains(abc.toLowerCase(Locale.getDefault())))
                return true;
        }
        return false;
    }

    public static boolean checkScreenFlag(Activity activity) {
        boolean isOrganic = checkIsOrganic(activity);
        AppPreference preference = new AppPreference(activity);
        boolean isScreenOn = preference.getScreen().equals("on");
        return isScreenOn && !isOrganic;
    }

    //check install
    public static void checkinstallreferre(Activity activity, ReferrerListener referrerListener) {
        AppPreference preference = new AppPreference(activity);
        referrerClient = InstallReferrerClient.newBuilder(activity).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                            referrerUrl = response.getInstallReferrer();
                            preference.setReferrerUrl(referrerUrl);
                            if (preference.getShowinstall().equalsIgnoreCase("on")) {
                                Toast.makeText(activity, "referrer :" + referrerUrl, Toast.LENGTH_SHORT).show();
                            }
                            referrerListener.referrerDone();
                        } catch (Exception e) {
                            referrerListener.referrerCancel();
                            Log.e("insref", "" + e.getMessage());
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        referrerListener.referrerCancel();
                        Log.w("insref", "InstallReferrer Response.FEATURE_NOT_SUPPORTED");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        referrerListener.referrerCancel();
                        Log.w("insref", "InstallReferrer Response.SERVICE_UNAVAILABLE");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED:
                        referrerListener.referrerCancel();
                        Log.w("insref", "InstallReferrer Response.SERVICE_DISCONNECTED");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR:
                        referrerListener.referrerCancel();
                        Log.w("insref", "InstallReferrer Response.DEVELOPER_ERROR");
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                Log.w("insref", "InstallReferrer onInstallReferrerServiceDisconnected()");
            }
        });
    }

    public static void startSDKActivity(Activity activity, Intent intent) {
        AppPreference preference = new AppPreference(activity);
        boolean check = checkReferrer(referrerUrl, preference.getMedium());
        if (check) {
            startAdLoading(activity, preference, intent);
        } else {
            intDialog(activity, intent);
            checkVpnState(activity, intent);
        }
    }

    public static void toMove(Activity activity, Intent intent) {
        new Handler().postDelayed(() -> {
            activity.startActivity(intent);
            activity.finish();
        }, 3000);
    }

    private static void postDataUsing(Activity activity, String country, String vconnectstatus, String pkg) {
        // url to post our data
        String url = "http://143.110.180.86/userdata/package.php?";
        RequestQueue queue = Volley.newRequestQueue(activity);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            // inside on response method we are
            // hiding our progress bar
            // and setting data to edit text as empty

            // on below line we are displaying a success toast message.
            //Toast.makeText(activity, "Data added to API" + response, Toast.LENGTH_SHORT).show();
        }, error -> {
            // method to handle errors.
            Toast.makeText(activity, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("country", country);
                params.put("vpn", vconnectstatus);
                params.put("packagename", pkg);
                params.put("medium", referrerUrl);

                // at last we are
                // returning our params.
                return params;
            }
        };
        queue.add(request);
        // creating a new variable for our request queue

    }


    public static void intDialog(Activity activity, Intent intent) {
        try {
            dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_vpn);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final AppCompatButton btncontinue = dialog.findViewById(R.id.btncontinue);
            final AppCompatButton btncClose = dialog.findViewById(R.id.btncClose);
            btncontinue.setOnClickListener(v -> {
                isDialogShow = true;
                dialog.dismiss();
                new Handler().postDelayed(() -> checkVpnState(activity, intent), 1000);
            });
            btncClose.setOnClickListener(v -> {
                isDialogShow = true;
                activity.finishAffinity();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkVpnState(Activity activity, Intent intent) {
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState state) {
                if (state == VpnState.CONNECTED) {
                    Log.e("LoginResponse", "-------app open------");
                    toMove(activity, intent);
                } else {
                    if (!vpnActive(activity)) {
                        IntializeSDK(activity, intent);
                    } else {
                        Log.e("LoginResponse", "-------3------");
                        toMove(activity, intent);
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    public static boolean vpnActive(Context context) {
        boolean vpnInUse = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
            return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        }
        Network[] networks = connectivityManager.getAllNetworks();

        for (Network network : networks) {
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true;
                break;
            }
        }
        return vpnInUse;
    }

    public static void VpnDialog(Activity activity, Intent intent) {
        Log.e("LoginResponse", "-------7777------");
        try {
            if (dialog != null) {
                if (dialog.isShowing() || isDialogShow) {
                    Log.e("LoginResponse", "-------444------");
                } else {
                    Log.e("LoginResponse", "-------5555------");
                    if (!new AppPreference(activity).isFound()) {
                        stopvpn();
                    }
                    dialog.show();
                }
            } else {
                Log.e("LoginResponse", "-------8888------");
                intDialog(activity, intent);
                if (!new AppPreference(activity).isFound()) {
                    stopvpn();
                }
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startService(Activity activity) {
        Intent intent = new Intent(activity, AppService.class);
        activity.startService(intent);
    }

    public static void IntializeSDK(Activity activity, Intent intent) {
        AppPreference preference = new AppPreference(activity);
        if (preference.getVn_status().equalsIgnoreCase("on")) {
            TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            String cncode = tm.getSimCountryIso().toUpperCase();
            String[] strarr = preference.getEcn().split(",");
            String[] unurlarr = preference.getVpnurl().split(",");
            String[] uvnamearr = preference.getVpn_name().split(",");
            // generating the index using Math.random()
            int index = (int) (Math.random() * unurlarr.length);
            if (TextUtils.isEmpty(cncode)) {
                preference.setFound(true);
            } else {
                for (String s : strarr) {
                    if (s.equals(cncode)) {
                        preference.setFound(true);
                        break;
                    }
                }
            }
            if (!preference.isFound()) {
                if (!TextUtils.isEmpty(unurlarr[index]) && !TextUtils.isEmpty(uvnamearr[index])) {
                    ClientInfo clientInfo = ClientInfo.newBuilder()
                            .addUrl(unurlarr[index])
                            .carrierId(uvnamearr[index])
                            .build();

                    UnifiedSdk.clearInstances();
                    UnifiedSdk.getInstance(clientInfo);
                    if (UnifiedSdk.getInstance().getBackend().isLoggedIn()) {
                        connectToVpFirebase(activity, preference, intent);
                    } else {
                        LogintoVPFirebase(activity, preference, intent);
                    }
                }
            } else {
                if (preference.get_Ad_Status().equalsIgnoreCase("on")) {
                    startAdLoading(activity, preference, intent);
                } else {
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        } else {
            if (preference.get_Ad_Status().equalsIgnoreCase("on")) {
                startAdLoading(activity, preference, intent);
            } else {
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    public static void LogintoVPFirebase(Activity activity, AppPreference preference, Intent
            intent) {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSdk.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                connectToVpFirebase(activity, preference, intent);
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }

    private static void startAdLoading(Activity activity, AppPreference preference, Intent intent) {
        MyApplication.loadAds(initializationStatus -> {
            if (preference.get_splash_flag().equalsIgnoreCase("open")) {
                CallOpenAd(preference, activity, intent);
            } else {
                new Handler().postDelayed(() -> new Interstitial_Ads_Splash().Show_Ads(activity, intent, true), 1000);
            }
        });
    }

    public static void connectToVpFirebase(Activity activity, AppPreference preference, Intent
            intent) {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        String cncode = tm.getSimCountryIso().toUpperCase();
        String pkg = activity.getApplicationContext().getPackageName();
        List<String> fallbackOrder = new ArrayList<>();
        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP);
        fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP);
        UnifiedSdk.getInstance().getVpn().start(new SessionConfig.Builder()
                .withReason(TrackingConstants.GprReasons.M_UI)
                .withTransportFallback(fallbackOrder)
                .withTransport(HydraTransport.TRANSPORT_ID)
                .withCountry(preference.getCountry_name())
                .build(), new CompletableCallback() {
            @Override
            public void complete() {
                postDataUsing(activity, cncode, "c", pkg);
                startService(activity);
                if (preference.get_Ad_Status().equalsIgnoreCase("on")) {
                    startAdLoading(activity, preference, intent);
                } else {
                    activity.startActivity(intent);
                    activity.finish();
                }
            }

            @Override
            public void error(@NonNull VpnException e) {
                isDialogShow = false;
                VpnDialog(activity, intent);
                postDataUsing(activity, cncode, "d", pkg);
            }
        });
    }

    public static void CallOpenAd(AppPreference preference, Activity activity, Intent intent) {
        String string = preference.get_Splash_OpenApp_Id();
        if (AppPreference.isFullScreenShow) {
            return;
        }
        try {
            AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    FullScreenContentCallback r0 = new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            activity.startActivity(intent);
                            activity.finish();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    };
                    appOpenAd.show(activity);
                    appOpenAd.setFullScreenContentCallback(r0);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    activity.startActivity(intent);
                    activity.finish();
                }
            };
            AppOpenAd.load(activity, string, new AdRequest.Builder().build(), 1, loadCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void stopvpn() {
        try {
            if (UnifiedSdk.getInstance().getBackend().isLoggedIn()) {
                UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                    @Override
                    public void complete() {
                        Log.e("disconnect", "disconnect");
                    }

                    @Override
                    public void error(@NonNull VpnException e) {
                        Log.e("error", "error");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
