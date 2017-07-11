package com.aviv_pos.olgats.avivinventory.async;

/**
 * Created by olgats on 08/12/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.aviv_pos.olgats.avivinventory.ItemSearchCriteria;
import com.aviv_pos.olgats.avivinventory.MainActivity;
import com.aviv_pos.olgats.avivinventory.R;
import com.aviv_pos.olgats.avivinventory.Utilities;
import com.aviv_pos.olgats.avivinventory.WSConstants;
import com.aviv_pos.olgats.avivinventory.beans.DataSetPromo;
import com.aviv_pos.olgats.avivinventory.beans.DataSetStatusInfo;
import com.aviv_pos.olgats.avivinventory.beans.Item;
import com.aviv_pos.olgats.avivinventory.beans.ItemImage;
import com.aviv_pos.olgats.avivinventory.beans.ItemSet;
import com.aviv_pos.olgats.avivinventory.beans.Promo;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Android ItemSearchTask (REST) from the Android Recipes book.
 */
public class ItemSearchTask extends AsyncTask<ItemSearchCriteria, String, ItemSet> {
    private static final String TAG = "AASearchTask";
    public static final String RESPONSE = "response";
    public static final String RESPONSE_SIZE = "responseSize";
    public static final String RESPONSE_FIRST = "responseFirst";
    public static final String RESPONSE_LOGIN = "responseLogin";

    private MainActivity mContext;
    private String mActionTag;

    public ItemSearchTask(MainActivity context, String action) {
        mContext = context;
        mActionTag = action;
    }


    @Override
    protected ItemSet doInBackground(ItemSearchCriteria... params) {
        ItemSearchCriteria criteria = params.length > 0 ? params[0] : null;
        ItemSet itemSet = null;
        if (criteria != null) {
            String code = criteria.getCode();
            String name = criteria.getName();
            try {
                DatabaseHandler.Settings settings = new DatabaseHandler.Settings(mContext);
                String str = settings.getValue("account");
                if (str != null) {
                    int accountId = Integer.parseInt(str);
                    String avivId = settings.getValue("avivId");
                    String session = settings.getValue("session");
                    HttpClient client = new DefaultHttpClient();
                    String szUrl;
                    List<NameValuePair> httpParams = new LinkedList<>();
                    if (name != null && !name.isEmpty()) {
                        szUrl = WSConstants.URL + "/session/findByName/" + accountId + "/" + session + "?";
                        httpParams.add(new BasicNameValuePair("name", name));
                    } else {
                        szUrl = WSConstants.URL + "/session/findByCode/" + accountId + "/" + session + "?";
                        httpParams.add(new BasicNameValuePair("code", code));
                    }
                    publishProgress(mContext.getString(R.string.waiting_for_result));
                    httpParams.add(new BasicNameValuePair("avivId", avivId));
                    httpParams.add(new BasicNameValuePair("extra", "true"));
                    String paramString = URLEncodedUtils.format(httpParams, "UTF-8");
                    szUrl += paramString;
                    HttpGet request = new HttpGet(szUrl);
                    request.getParams().setParameter("http.socket.timeout", WSConstants.HTTP_TIMEOUT);

                    publishProgress(mContext.getString(R.string.init_items));
                    HttpResponse httpResponse = client.execute(request);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    StatusLine sl = httpResponse.getStatusLine();
                    int sc = sl.getStatusCode();
                    if (sc == 200 && !isCancelled()) {
                        String response = EntityUtils.toString(httpEntity, "UTF-8");
                        itemSet = new Gson().fromJson(response, ItemSet.class);
                        DatabaseHandler.Items items = new DatabaseHandler.Items(mContext);
                        items.init(itemSet.getItems());
                        Log.d(TAG, itemSet.toString());
                        getImages(itemSet.getItems(), client, accountId, session, avivId);
                        getPromo(itemSet.getItems(), client, accountId, session, avivId);
                    }
                } else {
                    itemSet = new ItemSet();
                    itemSet.setStatus(WSConstants.ACCOUNT_NOT_EXISTS);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return itemSet;
    }

    private void getPromo(Item[] arr, HttpClient client, int accountId, String session, String avivId) {
        String szRequest = WSConstants.URL + "/session/getPromo/" + accountId + "/" + session + "?";
        List<NameValuePair> httpParams = new LinkedList<>();
        publishProgress(mContext.getString(R.string.init_promo));
        Gson gson = new Gson();
        DatabaseHandler.ItemPromos promoDao = new DatabaseHandler.ItemPromos(mContext);
        for (Item item : arr) {
            String code = item.getCode();
            publishProgress(mContext.getString(R.string.init_promo) + " " + code);
            httpParams.clear();
            httpParams.add(new BasicNameValuePair("code", code));
            httpParams.add(new BasicNameValuePair("avivId", avivId));
            String paramString = URLEncodedUtils.format(httpParams, "UTF-8");
            String szUrl = szRequest + paramString;
            HttpGet request = new HttpGet(szUrl);
            request.getParams().setParameter("http.socket.timeout", WSConstants.HTTP_TIMEOUT);
            try {
                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                StatusLine sl = httpResponse.getStatusLine();
                int sc = sl.getStatusCode();
                if (sc == 200 && !isCancelled()) {
                    String response = EntityUtils.toString(httpEntity, "UTF-8");
                    DataSetPromo set = new Gson().fromJson(response, DataSetPromo.class);
                    int status = set.getStatus();
                    if (status == WSConstants.SUCCESS) {
                        ArrayList<Promo> arrP = new ArrayList<>();
                        promoDao.clear(code);
                        for (Promo promo : set.getSet()) {
                            promo.setBarcode(code);
                            arrP.add(promoDao.create(promo));
                        }
                        item.setPromotions(arrP);
                    }

                }
            } catch (Exception ex) {
                Log.e(TAG, "Cant get the promo info for item #" + code, ex);
            }
        }
    }

    private void getImages(Item[] arr, HttpClient client, int accountId, String session, String avivId) throws IOException {
        String szRequest = WSConstants.URL + "/session/getImage/" + accountId + "/" + session + "?";
        List<NameValuePair> httpParams = new LinkedList<>();
        publishProgress(mContext.getString(R.string.init_images));
        for (Item item : arr) {
            String code = item.getCode();
            if (!Utilities.isImageExists(mContext, code)) {
                publishProgress(mContext.getString(R.string.init_image) + " " + code);
                httpParams.clear();
                httpParams.add(new BasicNameValuePair("code", code));
                httpParams.add(new BasicNameValuePair("avivId", avivId));
                String paramString = URLEncodedUtils.format(httpParams, "UTF-8");
                String szUrl = szRequest + paramString;
                HttpGet request = new HttpGet(szUrl);
                request.getParams().setParameter("http.socket.timeout", WSConstants.HTTP_TIMEOUT);

                HttpResponse httpResponse = client.execute(request);
                HttpEntity httpEntity = httpResponse.getEntity();
                StatusLine sl = httpResponse.getStatusLine();
                int sc = sl.getStatusCode();
                if (sc == 200 && !isCancelled()) {
                    String response = EntityUtils.toString(httpEntity, "UTF-8");
                    ItemImage ii = customGson.fromJson(response, ItemImage.class);
                    if (ii != null && ii.getStatus() == WSConstants.SUCCESS) {
                        Utilities.saveImage(mContext, ii);
                    }
                }
            }
        }
    }

    public static final Gson customGson = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class,
            new ByteArrayToBase64TypeAdapter()).create();

    // Using Android's base64 libraries. This can be replaced with any base64 library.
    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
        }
    }

    @Override
    protected void onPostExecute(ItemSet result) {
        Log.d(TAG, "RESULT = " + result);
        if (!isCancelled()) {
            Intent intent = new Intent(mActionTag);
            if (result != null) {
                intent.putExtra(RESPONSE, result.getStatus());
                intent.putExtra(RESPONSE_SIZE, result.getItems().length);
                if (result.getItems().length > 0) {
                    intent.putExtra(RESPONSE_FIRST, result.getItems()[0].getCode());
                }
            } else
                intent.putExtra(RESPONSE, WSConstants.APP_ERROR);
            // broadcast the completion
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (mContext != null && values != null && values.length > 0) {
            mContext.setProgressMessage(values[0]);
        }
    }
}
