package com.aviv_pos.olgats.avivinventory.async;

import android.os.AsyncTask;
import android.util.Log;

import com.aviv_pos.olgats.avivinventory.R;
import com.aviv_pos.olgats.avivinventory.StatusActivity;
import com.aviv_pos.olgats.avivinventory.WSConstants;
import com.aviv_pos.olgats.avivinventory.beans.DataSet;
import com.aviv_pos.olgats.avivinventory.beans.DataSetStatusInfo;
import com.aviv_pos.olgats.avivinventory.beans.Result;
import com.aviv_pos.olgats.avivinventory.beans.StatusInfo;
import com.aviv_pos.olgats.avivinventory.beans.Store;
import com.aviv_pos.olgats.avivinventory.beans.StoreSet;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olgats on 07/02/2016.
 */
public class StatusTask extends AsyncTask<String, String, Result<StatusInfo>> {
    private static final String TAG = "AAStatusTask";
    private StatusActivity mContext;
    private String mActionTag;

    public StatusTask(StatusActivity mContext, String mActionTag) {
        this.mContext = mContext;
        this.mActionTag = mActionTag;
    }

    @Override
    protected Result<StatusInfo> doInBackground(String... params) {
        Result<StatusInfo> result = new Result<>();
        DatabaseHandler.Settings settings = new DatabaseHandler.Settings(mContext);
        String account = settings.getValue("account");
        publishProgress("Loading the status info ...");
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(WSConstants.URL + "/account/getStatusInfo/" + account);
            request.getParams().setParameter("http.socket.timeout", WSConstants.HTTP_TIMEOUT);
            HttpResponse httpResponse = client.execute(request);
            StatusLine sl = httpResponse.getStatusLine();
            int sc = sl.getStatusCode();
            if (sc == 200 && !isCancelled()) {
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity, "UTF-8");
                DataSetStatusInfo set = new Gson().fromJson(response, DataSetStatusInfo.class);
                int status = set.getStatus();
                if (status == WSConstants.SUCCESS) {
                    List<StatusInfo> arr = new ArrayList<>();
                    for (StatusInfo i : set.getSet()) {
                        arr.add(i);
                    }
                    result.setData(arr);
                } else {
                    result.setCode(status);
                }
            } else {
                result.setCode(WSConstants.APP_ERROR);
            }
        } catch (java.net.SocketTimeoutException e) {
            Log.e(TAG, e.getMessage(), e);
            result.setCode(WSConstants.HTTP_TIMEOUT);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            result.setCode(WSConstants.APP_ERROR);
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (mContext != null && values != null && values.length > 0) {
            mContext.setProgressMessage(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Result<StatusInfo> result) {
        super.onPostExecute(result);
        if (mContext != null) {
            mContext.onChartData(result);
        }
    }
}
