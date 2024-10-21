package ci.progbandama.mobile.tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import ci.progbandama.mobile.models.MissVersion;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FetchData {

    public static class AppVersion extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<Context> contextRef;
        private LibraryListener listener;
        private String link;
        private MissVersion mVers;

        public AppVersion(Context context, String link, LibraryListener listener) {
            this.contextRef = new WeakReference<>(context);
            this.listener = listener;
            this.link = link;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = contextRef.get();
            if (context == null || listener == null) {
                cancel(true);
            } else {

            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Context context = contextRef.get();
                if (context != null) {
                    mVers = getAppVers(context, link);
                    boolean verdict = false;
                    Long localVersion = Commons.Companion.getAppCuVesCod(context);
//                    LogUtils.d(link, mVers);
                    if(Commons.Companion.getAppCuVesCod(context) == Long.valueOf(mVers.getLatestVersionCode())) verdict = false;
                    else verdict = true;

                    LogUtils.d(mVers.getIgnoreVersionCode(), Commons.Companion.getAppCuVesCod(context));

                    for(int vers : mVers.getIgnoreVersionCode()){
                        if(localVersion == vers) verdict = false;
                    }
                    //if(.contains(Commons.Companion.getAppCuVesCod(context))) verdict = false;

                    return verdict;
                } else {
                    cancel(true);
                    return null;
                }
            } catch (Exception ex) {
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean update) {
            super.onPostExecute(update);
//            LogUtils.d(link, update);
            if (update != null) {
                if (update) {
                    listener.onCompleted(mVers, true);
                } else {
                    listener.onCompleted(null, false);
                }
            }else listener.onCompleted(null, false);
        }
    }

    public static MissVersion getAppVers(Context context, String link) {
        OkHttpClient client = new OkHttpClient();
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        ResponseBody body = null;

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                MissVersion missVersion = gson.fromJson(response.body().string(), MissVersion.class);
                // Traiter l'objet MissVersion ici
                return missVersion;
            }else return null;

        } catch (IOException ignore) {
            Log.e("Error", "App wasn't found");

        } finally {
            if (body != null) {
                body.close();
            }
        }

        return null;
    }

    public interface LibraryListener {
        void onCompleted(MissVersion vers, Boolean iscomplete);

    }

}
