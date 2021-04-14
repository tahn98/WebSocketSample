package com.example.websocketsample;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tahn";
    private static final String WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com";
    WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initWebSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSocketClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webSocketClient.close();
    }

    public void initWebSocket() throws URISyntaxException {
        URI uri = new URI(WEB_SOCKET_URL);
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "onOpen");
                subscribe();
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose");
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "onError");
            }
        };
    }

    public void subscribe() {
        webSocketClient.send(
                "{\n" +
                        "    \"type\": \"subscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                        "}"
        );
    }
}