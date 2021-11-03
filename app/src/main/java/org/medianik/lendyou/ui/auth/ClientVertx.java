package org.medianik.lendyou.ui.auth;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;


public class ClientVertx extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.createHttpClient()
                .webSocket(new WebSocketConnectOptions().setHost("192.168.1.4").setPort(4040),
                        event -> {
                            if (event.succeeded()) {
                                Log.d("Lendyou", "Successfully connected to a socket.");
                                WebSocket webSocket = event.result();
                                webSocket.writeTextMessage("How're you doing, man???");
                                AtomicInteger integer = new AtomicInteger(0);
                                Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> webSocket.writeTextMessage("Done for now " + integer.getAndIncrement()), 1000L, 2000L, TimeUnit.MILLISECONDS);
                                webSocket.textMessageHandler(inputText -> Log.i("Lendyou", "Input text from " + webSocket.remoteAddress() + ", with '" + inputText + "'"));
                            } else {
                                Log.e("Lendyou", "Oopsie!", event.cause());
                            }
                        });
    }
}
