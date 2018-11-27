package net.spearfischer.roboview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.net.Uri;

import com.github.niqdev.mjpeg.*;

import java.net.*;
import java.io.*;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    TextView status;
    MjpegSurfaceView video;
    Socket socket;
    PrintStream commands;

    final static String ip = "192.168.1.32";
    final static int videoPort = 8000;
    final static int controlPort = 8001;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        status.setText("Initializing");

        video = findViewById(R.id.video);

/*        video.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                MainActivity.this.onError(new RuntimeException("Started:" + url));
            }
            public void onPageFinished(WebView view, String url) {
                MainActivity.this.onError(new RuntimeException("Finished:" + url));
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                MainActivity.this.onError(new RuntimeException("WebView:" + description));
            }
        });
*/
        try {
            Mjpeg.newInstance().open("http://" + ip + ":" + videoPort + "/stream.mjpeg").subscribe(
                new rx.functions.Action1<MjpegInputStream>() {
                    public void call(MjpegInputStream stream) {
                        video.setSource(stream);
                        video.setDisplayMode(DisplayMode.BEST_FIT);
                    }
                }
            );

                    //          socket = new Socket(ip, controlPort);
//            commands = new PrintStream(socket.getOutputStream());
        } catch (Exception ex) {
            onError(ex);
        }
    }

    public void onReady(MjpegInputStream stream) {
    }

    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        commands.println("+fore");
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        commands.println("+left");
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        commands.println("+right");
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        commands.println("+back");
                        break;
                }
                break;

            case KeyEvent.ACTION_UP:
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        commands.println("-fore");
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        commands.println("-left");
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        commands.println("-right");
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        commands.println("-back");
                        break;
                }
                break;
        }
        return true;
    }

    public void onError(Exception ex) {
        status.setText(ex.getMessage());

        ex.printStackTrace();
    }
}
