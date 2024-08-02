package com.pb.settings.fragments.integrity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.util.pb.PixelPropsUtils;
import android.util.Log;

public class PropsDownloader extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = PropsDownloader.class.getSimpleName();

    private Context mContext;
    static boolean isDownloading = false;

    private String[] getCredsArr(Context context) {
        Resources resources = context.getResources();
        return resources.getStringArray(R.array.config_certifiedPropertiesCreds);
    }

    private static void showToast(Context context, String message) {
        new Handler(context.getMainLooper()).post(() ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        );
    }

    private static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = context.getSystemService(ConnectivityManager.class);
        NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if (cap == null) return false;
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return 0;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.playintegrity_settings, rootKey);

        Preference downloadPropertiesPref = findPreference("play_integrity_update");
        Preference playIntegrityPref = findPreference("pintegrity_category");

        getActivity().setTitle(R.string.play_integrity_dashboard_title);

        String[] credsArr = getCredsArrcredsArr(mContext);

        if (downloadPropertiesPref != null && playIntegrityPref != null &&
            credsArr.length > 0) {
            downloadPropertiesPref.setOnPreferenceClickListener(preference -> {
                if (!isDownloading) {
                    if (isInternetAvailable){
                        isDownloading = true;
                        String[] dlProps = downloadProps(mContext, creds);

                        if (PixelPropsUtils.sCertifiedProps.length == 0) {
                            PixelPropsUtils.sCertifiedProps = dlProps;
                        } else if (!Arrays.equals(PixelPropsUtils.sCertifiedProps, dlProps)) {
                            PixelPropsUtils.sCertifiedProps = dlProps;
                        } else {
                            showToast (mContext, "Props are up to date. Nothing to do");
                        }
                    } else {
                        showToast(mContext, "Check your internet connection");
                    }
                } else {
                    showToast(mContext, "Download already in progress");
                }
                return true;
            });
        }
    }

    public static String[] downloadProps(Context context, String[] creds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<String[]> futureTask = new FutureTask<>(new Callable<String[]>() {
            @Override
            public String[] call() {
                try {
                    // Create URL object
                    URL url = new URL(creds[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set timeouts for the socket
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(15000);

                    // Set request method & auth header
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "token " + creds[1]);

                    // Check response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                        String inputLine;
                        StringBuilder content = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine).append("\n");
                        }

                        // Close the streams
                        in.close();
                        connection.disconnect();

                        // Parse the content and extract the string array
                        if (content.length() > 0) {
                            return parseStringArray(content.toString());
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch, HTTP code " + responseCode);
                        showToast(context, "Failed to fetch, got HTTP " + responseCode);
                        return new String[0]; // Return empty on unexpected HTTP response
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Failed to fetch", e);
                    showToast (context, "Failed to fetch, invalid URL");
                } catch (SocketTimeoutException e) {
                    Log.w(TAG, "Failed to fetch, got timeout ", e);
                    showToast (context, "Failed to fetch, got timeout");
                } catch (UnknownHostException e) {
                    Log.w(TAG, "Failed to fetch", e);
                    showToast (context, "Failed to fetch, check your connection");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to fetch, I/O exception! ", e);
                    showToast (context, "Failed to fetch, got I/O exception");
                }
                return new String[0];
            }
        });

        executor.execute(futureTask);

        try {
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Failed to fetch, got execution exception! ", e);
            return new String[0]; // Return an empty array in case of exception
        } finally {
            isDownloading = false;
            shutdownExecutor(executor);
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown(); // Initiates an orderly shutdown
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Forceful shutdown if not terminated in time
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow(); // Forceful shutdown on interruption
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    private static String[] parseStringArray(String xmlContent) {
        List<String> list = new ArrayList<>();
        String[] lines = xmlContent.split("\n");
        boolean isPropsArray = false;

        for (String line : lines) {
            line = line.trim();
            if (line.contains("<string-array name=\"config_certifiedBuildProperties\"")) {
                isPropsArray = true;
            } else if (line.contains("</string-array>")) {
                isPropsArray = false;
            }

            if (isPropsArray && line.contains("<item>")) {
                int start = line.indexOf(">") + 1;
                int end = line.lastIndexOf("<");
                String item = line.substring(start, end).replace("\"", "");
                list.add(item);
            }
        }
        return list.toArray(new String[0]);
    }
}
