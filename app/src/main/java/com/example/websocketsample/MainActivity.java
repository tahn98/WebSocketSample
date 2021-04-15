package com.example.websocketsample;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tahn";
    private static final String WEB_SOCKET_URL = "wss://ws-feed.pro.coinbase.com";
    WebSocketClient webSocketClient;
    private Button btAdd;
    private Button btDel;
    private Button btReconnect;
    private TextView tvContent;
    private View vToTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initWebSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        btAdd = findViewById(R.id.btAdd);
        btDel = findViewById(R.id.btDel);
        btReconnect = findViewById(R.id.btReconnect);
        tvContent = findViewById(R.id.tvContent);
        vToTouch = findViewById(R.id.vToTouch);

        btAdd.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "ETH", Toast.LENGTH_LONG).show();
            subscribeEth(webSocketClient);
        });

        btDel.setOnClickListener(v -> {
            unsubscribeEth(webSocketClient);
        });

        btReconnect.setOnClickListener(v -> {
            if(webSocketClient.isClosed()){
                webSocketClient.reconnect();
            }
        });

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, String.valueOf(ev.getX()));
        Log.d(TAG, String.valueOf(ev.getY()));

        //get pos x,y of view
        int[] location = new int[2];
        vToTouch.getLocationOnScreen(location);
        float x = location[0];
        float y = location[1];

        Log.d("ViewToTouch", x + "-" + y);
        float touchX = ev.getX();
        float touchY = ev.getY();
        if(checkTouchInBound(touchX, touchY, x, y)){
            Toast.makeText(this, "in bound", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "out bound", Toast.LENGTH_SHORT).show();

        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * Check the touch of user in bound or not
     * @param touchX the x point user touch
     * @param touchY the y point user touch
     * @param x the position x of view
     * @param y the position y of view
     * @return true if inbound else false
     */
    public Boolean checkTouchInBound(float touchX, float touchY, float x, float y){
        return touchX >= x && touchX <= x + vToTouch.getWidth() && touchY >= y && touchY <= y + vToTouch.getHeight();
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
                runOnUiThread(() -> tvContent.setText(message));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose");
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "onError" + ex.getMessage());
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

    public void subscribeEth(WebSocketClient webSocketClient){
        webSocketClient.send(
                "{\n" +
                        "    \"type\": \"subscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"ETH-EUR\"] }]\n" +
                        "}"
        );
    }

    public void unsubscribeEth(WebSocketClient webSocketClient){
        webSocketClient.send(
                "{\n" +
                        "    \"type\": \"unsubscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"ETH-EUR\"] }]\n" +
                        "}"
        );
    }
}