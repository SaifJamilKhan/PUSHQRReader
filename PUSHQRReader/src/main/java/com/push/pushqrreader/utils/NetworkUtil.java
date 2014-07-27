package com.push.pushqrreader.utils;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NetworkUtil {


    private static class RequestTask extends AsyncTask<String, String, String> {
        NetworkRequestListener listener;
        JSONObject body;

        public RequestTask(NetworkRequestListener listener, JSONObject body) {
            this.listener = listener;
            this.body = body;
        }

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                HttpGet get = new HttpGet(uri[0]);

                response = httpclient.execute(get);
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    responseString = getResponseString(response);
                    try {
                        JSONObject responseJSON = new JSONObject(responseString);
                        listener.requestSucceededWithJSON(responseJSON);
                    } catch (JSONException e) {
                        listener.requestFailed(e);
                        e.printStackTrace();
                    }
                } else if (statusLine.getStatusCode() == HttpStatus.SC_UNPROCESSABLE_ENTITY
                        || statusLine.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                    responseString = getResponseString(response);
                    try {
                        JSONObject responseJSON = new JSONObject(responseString);
                        listener.requestFailedWithJSON(responseJSON);
                    } catch (JSONException e) {
                        listener.requestFailed(e);
                        e.printStackTrace();
                    }
                } else {
                    listener.requestFailed(null);
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                // TODO Handle problems..
            } catch (IOException e) {
                // TODO Handle problems..
            }
            return responseString;
        }

        private String getResponseString(HttpResponse response)
                throws IOException {
            String responseString;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            responseString = out.toString();
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Do anything with response..
        }
    }


    public static interface NetworkRequestListener {

        public abstract void requestSucceededWithJSON(JSONObject object);

        public abstract void requestFailedWithJSON(JSONObject object);

        public abstract void requestFailed(Exception e);
    }

    public static void makeGetRequest(String url_path,
                                       NetworkRequestListener listener, JSONObject body) {
        RequestTask task = new RequestTask(listener, body);
        task.execute(url_path);
    }

}