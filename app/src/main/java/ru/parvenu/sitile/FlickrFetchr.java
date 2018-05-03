package ru.parvenu.sitile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "6ad79398d938b82921f2f93cb991826a";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                return out.toByteArray();
            } finally {
                connection.disconnect();
            }
        }

        public String getUrlString(String urlSpec) throws IOException {
            return new String(getUrlBytes(urlSpec));
        }

    public byte[] getUrlPic(Uri urlSpec) throws IOException {
        byte[] b = getUrlBytes(urlSpec.toString());
        Log.i(TAG, "getURlBytes: "+b.length);
        return b;
    }

    public Bitmap fetchItem(Uri url) {
        Bitmap imag11 = null;
        try {
            byte[] b = getUrlPic(url);
            imag11=BitmapFactory.decodeByteArray(b,0,b.length);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch item", ioe);
        }
        return imag11;
    }

    public List<Track> fetchItems() {
        List<Track> items = new ArrayList<>();
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            //Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    private void parseItems(List<Track> items, JSONObject jsonBody) throws IOException, JSONException {
            JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
            JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
            for (int i = 0; i < photoJsonArray.length(); i++) {
                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
                Track item = new Track(photoJsonObject.getString("id"));
                //item.setId();
                item.setTitle(photoJsonObject.getString("title"));
                if (!photoJsonObject.has("url_s")) {
                    continue;
                }
                item.setUrl(photoJsonObject.getString("url_s"));
                item.setOwner(photoJsonObject.getString("owner"));
                item.setServer(photoJsonObject.getString("server"));
                item.setFarm(photoJsonObject.getString("farm"));
                item.setSecret(photoJsonObject.getString("secret"));
                items.add(item);
            }
        }
}
