package org.medianik.lendyou.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.medianik.lendyou.ui.MainActivity;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

public class ServerConnection {
    private static final String url = "https://192.168.1.4:4041/auth";
    private final RequestQueue queue;

    public ServerConnection(Context context) {
        this.queue = Volley.newRequestQueue(context);
    }

    public void request(String firebaseToken, Response.Listener<Response0> listener) {
        Log.d("Lendyou", "FirebaseToken: " + firebaseToken);
        var request = new GsonRequest<>(
                Response0.class,
                url,
                "{\n" +
                        "  \"" + MainActivity.firebaseKey + "\": \"" + firebaseToken + "\"\n" +
                        "}",
                listener
        );
        queue.add(request);
    }

    public void request(String firebaseToken, Response.Listener<Response0> listener, Response.ErrorListener errorListener) {
        Log.d("Lendyou", "FirebaseToken: " + firebaseToken);
        var request = new GsonRequest<>(
                Response0.class,
                url,
                "{\n" +
                        "  \"" + MainActivity.firebaseKey + "\": \"" + firebaseToken + "\"\n" +
                        "}",
                listener,
                errorListener
        );
        queue.add(request);
    }

    public enum RequestType {
        AuthWithUUID;


        RequestType() {

        }
    }

    public static class Response0 {
        private final String result;

        Response0(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }

        @Override
        public String toString() {
            return "Response0{" +
                    "result='" + result + '\'' +
                    '}';
        }
    }

    static class GsonRequest<T> extends JsonRequest<T> {
        private static final Gson gson = new Gson();
        private static final Response.ErrorListener ERROR_LISTENER = error -> Log.e("Lendyou", "Response was unsuccessful", error);

        private final Class<T> klass;

        public GsonRequest(
                Class<T> klass,
                String url,
                @Nullable String requestBody,
                Response.Listener<T> listener
        ) {
            super(Method.POST, url, requestBody, listener, ERROR_LISTENER);
            this.klass = klass;
        }

        public GsonRequest(
                Class<T> klass,
                String url,
                @Nullable String requestBody,
                Response.Listener<T> listener,
                Response.ErrorListener errorListener
        ) {
            super(Method.POST, url, requestBody, listener, errorListener);
            this.klass = klass;
        }

        @Override
        public Map<String, String> getHeaders() {
            return Collections.singletonMap("Content-type", "application/json");
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            try {
                String json = new String(
                        response.data,
                        HttpHeaderParser.parseCharset(response.headers)
                );
                return Response.success(
                        gson.fromJson(json, klass),
                        HttpHeaderParser.parseCacheHeaders(response)
                );
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }
    }
}
