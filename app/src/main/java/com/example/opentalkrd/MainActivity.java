package com.example.opentalkrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements  Session.SessionListener,
        PublisherKit.PublisherListener{


    private static String API_KEY = "46484962";
    private static String SESSION_ID = "2_MX40NjQ4NDk2Mn5-MTU3Nzc3MDY4MzkxNX40ZVZlL2QrTVIzb2x2Rk1XT05jTFJXRkF-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjQ4NDk2MiZzaWc9MmQwMjJjOWVhM2FlMTcyZTA0NmM4NzdhNTE5N2Y3ZTIxYzZiYjRlYzpzZXNzaW9uX2lkPTJfTVg0ME5qUTRORGsyTW41LU1UVTNOemMzTURZNE16a3hOWDQwWlZabEwyUXJUVkl6YjJ4MlJrMVhUMDVqVEZKWFJrRi1mZyZjcmVhdGVfdGltZT0xNTc3NzcwNzQ1Jm5vbmNlPTAuNDE5MDI2Njk0MzU4MDA4MyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTgwMzYyNzQ0JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;


    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    private Publisher mPublisher;

    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout


            // initialize and connect to the session

            setSession();


        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    private void setSession(){
        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);
        mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
        mSession.setSessionListener(this);
        mSession.connect(TOKEN);
    }


    @Override
    public void onConnected(Session session) {

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }


    @Override
    public void onDisconnected(Session session) {

        Log.i(LOG_TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(LOG_TAG, "Stream Received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());
    }




    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

        Log.i(LOG_TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());
    }






}
