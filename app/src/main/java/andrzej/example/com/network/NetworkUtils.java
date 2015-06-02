package andrzej.example.com.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkUtils {
    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
