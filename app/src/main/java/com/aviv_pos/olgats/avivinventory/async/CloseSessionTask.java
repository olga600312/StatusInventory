package com.aviv_pos.olgats.avivinventory.async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.aviv_pos.olgats.avivinventory.ItemSearchCriteria;
import com.aviv_pos.olgats.avivinventory.WSConstants;
import com.aviv_pos.olgats.avivinventory.beans.ItemSet;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by olgats on 12/01/2016.
 */
public class CloseSessionTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "AACloseSessionTask";


    private Context mContext;

    public CloseSessionTask(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
            try {
                DatabaseHandler.Settings settings = new DatabaseHandler.Settings(mContext);
                String str = settings.getValue("account");
                if (str != null) {
                    int accountId = Integer.parseInt(str);
                    String avivId = settings.getValue("avivId");
                    String session = settings.getValue("session");
                    HttpClient client = new DefaultHttpClient();
                    String szUrl= WSConstants.URL + "/account/closeSession/" + accountId + "?";
                    List<NameValuePair> httpParams = new LinkedList<>();
                    httpParams.add(new BasicNameValuePair("avivId", avivId));
                    httpParams.add(new BasicNameValuePair("sessionId", session));
                    String paramString = URLEncodedUtils.format(httpParams, "UTF-8");
                    szUrl += paramString;
                    HttpGet request = new HttpGet(szUrl);
                    request.getParams().setParameter("http.socket.timeout", WSConstants.HTTP_TIMEOUT);
                    client.execute(request);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

        return null;
    }




}
