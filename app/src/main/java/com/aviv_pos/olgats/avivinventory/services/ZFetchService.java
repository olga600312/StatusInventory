package com.aviv_pos.olgats.avivinventory.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aviv_pos.olgats.avivinventory.MainActivity;
import com.aviv_pos.olgats.avivinventory.WSConstants;
import com.aviv_pos.olgats.avivinventory.beans.DataSet;
import com.aviv_pos.olgats.avivinventory.beans.ZReport;
import com.aviv_pos.olgats.avivinventory.beans.ZReportDataSet;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZFetchService extends Service {
    final private String TAG = "ServiceZFetch";
    private ExecutorService es;

    public ZFetchService() {
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ZFetchService onCreate");
        es = Executors.newFixedThreadPool(1);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ZFetchService onDestroy");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ZFetchService onStartCommand");
        PendingIntent pi = intent.getParcelableExtra(MainActivity.ZFETCH_PARAM_PINTENT);

        MyRun mr = new MyRun(startId, pi);
        es.execute(mr);

        return super.onStartCommand(intent, flags, startId);
    }

    class MyRun implements Runnable {
        int startId;
        PendingIntent pi;

        public MyRun(int startId, PendingIntent pi) {
            this.startId = startId;
            this.pi = pi;
            Log.d(TAG, "ZFetchService.MyRun#" + startId + " create");
        }

        public void run() {
            Log.d(TAG, "ZFetchService.MyRun#" + startId + " start ");
            try {
                // сообщаем об старте задачи
                if (pi != null)
                    pi.send(MainActivity.ZFETCH_STATUS_START);

                // начинаем выполнение задачи

                DatabaseHandler.Settings settings = new DatabaseHandler.Settings(ZFetchService.this);
                String account = settings.getValue("account");
                DatabaseHandler.ZReports zReports = new DatabaseHandler.ZReports(ZFetchService.this);
                ArrayList<ZReport> max = zReports.getLatestZ(Integer.parseInt(account));
                DataSet set = new DataSet();
                set.setSet(max.toArray());
                String zJson = new Gson().toJson(set);
                List<NameValuePair> httpParams = new LinkedList<>();
                httpParams.add(new BasicNameValuePair("zJson", zJson));
                HttpClient client = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(httpParams, "UTF-8");
                String szRequest = WSConstants.URL + "/account/getZInfo/" + account + "?";
                String szUrl = szRequest + paramString;
                HttpGet request = new HttpGet(szUrl);
                //request.getParams().setParameter("http.socket.timeout", WSConstants.HTTP_TIMEOUT);
                Log.d(TAG, "ZFetchService.MyRun#" + startId + " execute request ");
                boolean fl = true;
                HttpResponse httpResponse = null;
                do {
                    try {
                        httpResponse = client.execute(request);
                        fl = false;
                    } catch (java.net.SocketTimeoutException ex) {
                        Log.d(TAG, "ZFetchService.MyRun#" + startId + " SocketTimeoutException " + ex.getMessage());
                    }
                } while (fl);
                StatusLine sl = httpResponse.getStatusLine();
                int sc = sl.getStatusCode();
                int status = WSConstants.APP_ERROR;
                Log.d(TAG, "ZFetchService.MyRun#" + startId + " get Response statusCode=" + sc);
                if (sc == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "UTF-8");
                    ZReportDataSet zSet = new Gson().fromJson(response, ZReportDataSet.class);
                    status = zSet.getStatus();
                    Log.d(TAG, "ZFetchService.MyRun#" + startId + " from Json " + response);
                    if (status == WSConstants.SUCCESS) {
                        zReports.init(Arrays.asList(zSet.getSet()), true);
                    }
                    Log.d(TAG, "ZFetchService.MyRun#" + startId + " DataSet status " + status);
                }
                // сообщаем об окончании задачи
                Intent intent = new Intent().putExtra(MainActivity.ZFETCH_RESULT, status);
                if (pi != null)
                    pi.send(ZFetchService.this, MainActivity.ZFETCH_STATUS_FINISH, intent);

            } catch (PendingIntent.CanceledException e) {
                Log.d(TAG, "ZFetchService.MyRun#" + startId + " PendingIntent.CanceledException " + e.getMessage());
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                Log.d(TAG, "ZFetchService.MyRun#" + startId + " ClientProtocolException " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "ZFetchService.MyRun#" + startId + " exception " + e.getMessage());
                e.printStackTrace();
            }
            stop();
        }

        void stop() {
            Log.d(TAG, "ZFetchService.MyRun#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
