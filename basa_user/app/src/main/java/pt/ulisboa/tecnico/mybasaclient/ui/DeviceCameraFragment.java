package pt.ulisboa.tecnico.mybasaclient.ui;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.TreeMap;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.FirebaseFileLink;
import pt.ulisboa.tecnico.mybasaclient.util.DateHelper;
import pt.ulisboa.tecnico.mybasaclient.util.DemoPlayer;
import pt.ulisboa.tecnico.mybasaclient.util.ExtractorRendererBuilder;
import pt.ulisboa.tecnico.mybasaclient.util.FirebaseHelper;
import pt.ulisboa.tecnico.mybasaclient.util.GenericCommunicationToFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceCameraFragment extends DialogFragment implements View.OnClickListener, SurfaceHolder.Callback,DemoPlayer.Listener, CompoundButton.OnCheckedChangeListener {
    View rootView;
    Toolbar toolbar;
    private BasaDevice device;
    private ImageView imageCamera;

    private static final int MENU_GROUP_TRACKS = 1;
    private static final int ID_OFFSET = 2;
    private View root, loadingScreen, textViewLive;
    private SwitchCompat switchLive;
    private FirebaseFileLink videoLink = null;
    private long recentPhoto = 0;
    private TreeMap<String, FirebaseFileLink> firebaseLive;
    private GenericCommunicationToFragment listener;
    private TextView currentTime, totalTime, time, date;
    private long playerPosition;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    private MediaController mediaController;
    private DemoPlayer player;
    private boolean playerNeedsPrepare;
    private Uri contentUri;
    private View mediaLayout;
    private SeekBar mSeekBar;
    private boolean ready = false;
    private Handler mHandler = new Handler();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Runnable videoTime = new Runnable() {
        @Override
        public void run() {

            if(player != null && player.getPlayerControl() != null) {
                if (player.getPlayerControl().isPlaying()){

                    int mCurrentPosition = player.getPlayerControl().getCurrentPosition() / 1000;
                    int duration = player.getPlayerControl().getDuration()/ 1000;
                    mSeekBar.setMax(duration);
                    mSeekBar.setProgress(mCurrentPosition);
                    int minutes = (mCurrentPosition) / 60;
                    int seconds = mCurrentPosition % 60;
                    currentTime.setText(String.format("%02d:%02d", minutes, seconds));
                    minutes = (duration) / 60;
                    seconds = duration % 60;
                    totalTime.setText(String.format("%02d:%02d", minutes, seconds));
                }
            }

            mHandler.postDelayed(this, 700);
        }
    };

    private ValueEventListener liveVideoListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.d("ddd", "liveVideoListener:onDataChange");
            GenericTypeIndicator<HashMap<String, FirebaseFileLink>> t = new GenericTypeIndicator<HashMap<String, FirebaseFileLink>>() {
            };
            ready = false;
            HashMap<String, FirebaseFileLink> videos = dataSnapshot.getValue(t);
            if (!ready && videos != null && getActivity() != null) {
                Log.d("ddd", "liveVideoListener:onDataChange1");
                firebaseLive.clear();
                firebaseLive.putAll(videos);
                String nextVideoKey = getRecentVideoKey();
                String urlVideo = videos.get(nextVideoKey).getUrl();
//            final long photoTime = Long.parseLong(firebaseLive.lastEntry().getKey())/10 ;
                final long photoTime = videos.get(nextVideoKey).getCreatedAt() / 10000;

                Log.d("ddd", "liveVideoListener:photoTime:"+photoTime);
//                if(System.currentTimeMillis() / 1000 - photoTime > 20){
//                    Log.d("ddd", "return:"+(System.currentTimeMillis() / 1000 - photoTime));
//                    return;
//                }

                ready = true;
                Log.d("ddd", "liveVideoListener:urlVideo-------------"+urlVideo);
                Glide.with(DeviceCameraFragment.this).load(urlVideo)
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .dontAnimate()
                        .into(new SimpleTarget<Bitmap>() {

                            @Override
                            public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                                // TODO Auto-generated method stub

                                Log.d("ddd", "liveVideoListener:onDataChange3");
                                if (photoTime > recentPhoto) {
                                    recentPhoto = photoTime;
                                    imageCamera.setImageBitmap(arg0);
                                    long timeAgo = System.currentTimeMillis() / 1000 - photoTime;
//                                                    long minutes = (currentTime) / 60;
//                                                    long seconds = currentTime % 60;
                                    time.setText(timeAgo + " seconds ago");
//                                                    time.setText(String.format("%02d:%02d", minutes, seconds));
                                    ready = false;
                                }
                            }
                        });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("ddd", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };



    public DeviceCameraFragment() {
        // Required empty public constructor
    }


    public static DeviceCameraFragment newInstance() {
        DeviceCameraFragment fragment = new DeviceCameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_device_camera, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        device = AppController.getInstance().getCurrentDevice();
        if (toolbar!=null) {

            toolbar.setTitle(device.getName());
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(getDialog() != null)
                        getDialog().dismiss();
                }
            });
        }
        init();
        return rootView;
    }

    private String getRecentVideoKey(){
        return this.firebaseLive.lastEntry().getKey();
    }



    private void links(String url, OnSuccessListener<Uri> listener){
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://basa-2a0c9.appspot.com");

        storageRef.child(url).getDownloadUrl()
                .addOnSuccessListener(listener)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }



    private void init(){
        View settings = rootView.findViewById(R.id.action_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).openPage(Global.DIALOG_DEVICE_SETTINGS);
            }
        });
//imageCamera
        imageCamera = (ImageView)rootView.findViewById(R.id.imageCamera);
        Glide.with(this).load(R.drawable.ic_device_camera_large)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageCamera);

        textViewLive = rootView.findViewById(R.id.textViewLive);
        currentTime = (TextView) rootView.findViewById(R.id.currentTime);
        totalTime = (TextView) rootView.findViewById(R.id.totalTime);
        time = (TextView) rootView.findViewById(R.id.time);
        date = (TextView) rootView.findViewById(R.id.date);
        loadingScreen = rootView.findViewById(R.id.loadingScreen);
        switchLive = (SwitchCompat)  rootView.findViewById(R.id.switchLive);
        switchLive.setChecked(true);
        switchLive.setOnCheckedChangeListener(this);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(player != null && player.getPlayerControl() != null && fromUser) {
                    if (player.getPlayerControl().isPlaying()) {
                        player.getPlayerControl().seekTo(progress * 1000);
                    }
                }
            }
        });
        mediaLayout = rootView.findViewById(R.id.mediaLayout);
        root = rootView.findViewById(R.id.root);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mediaController.dispatchKeyEvent(event);
            }
        });

        rootView.findViewById(R.id.b1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlsVisibility();
            }
        });

        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);

        rootView.findViewById(R.id.action_play).setOnClickListener(this);

        mediaController = new KeyCompatibleMediaController(getActivity());
        mediaController.setAnchorView(surfaceView);
//        mediaController.setAnchorView(root);
        this.firebaseLive = new TreeMap<>();


        rootView.findViewById(R.id.action_history).setOnClickListener(this);
        showVideoLayout(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            //onShown();
        }
        mHandler.post(videoTime);
        mDatabase.child("live").child(device.getId()).addValueEventListener(liveVideoListener);
        new FirebaseHelper().enableLiveStream(device.getId(), true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            onHidden();
        }
        new FirebaseHelper().enableLiveStream(device.getId(), false);
        mDatabase.removeEventListener(liveVideoListener);
        mHandler.removeCallbacks(videoTime);
    }

    private void onHidden() {
        releasePlayer();
    }

    private void onShown() {

        if(videoLink != null) {
            contentUri = Uri.parse(videoLink.getUrl());

            date.setVisibility(View.VISIBLE);
            date.setText(DateHelper.getDateAgo(videoLink.getCreatedAt()));
            time.setText(DateHelper.getTime(videoLink.getCreatedAt()));
//        contentUri = Uri.parse("http://www.sample-videos.com/video/mp4/240/big_buck_bunny_240p_5mb.mp4");

            if (player == null) {
                if (!maybeRequestPermission()) {
                    preparePlayer(true);
                }
            } else {
                player.setBackgrounded(false);
            }
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());

//            mediaController.setEnabled(true);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
        loadingScreen.setVisibility(View.GONE);
        root.setVisibility(View.VISIBLE);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    private void changeVideo(){
        player.stop();
        player.seekTo(0L);
        //you must change your contentUri before invoke getRendererBuilder();
        player.setRendererBuilder(getRendererBuilder());
        player.prepare();
        playerNeedsPrepare = false;
    }


    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
        return new ExtractorRendererBuilder(getActivity(), userAgent, contentUri);
    }


    private void toggleControlsVisibility()  {
        if (mediaLayout.isShown()) {
            Log.d("exop", "mediaController.isShowing() -> gonna hide");
            mediaController.hide();
            mediaLayout.setVisibility(View.GONE);
        } else {
            Log.d("exop", "!mediaController.isShowing() -> gonna show");
            showControls();
        }
    }

    private void showControls() {
        mediaLayout.setVisibility(View.VISIBLE);
//        mediaController.show(0);
        //hide
        mediaController.show(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zone_info:
                ((MainActivity)getActivity()).openPage(Global.DIALOG_SETTINGS_ZONE_INFO);
                break;
            case R.id.action_play:
                if(player != null && player.getPlayerControl() != null){
                    if(player.getPlayerControl().isPlaying())
                        player.getPlayerControl().pause();
                    else{
                        player.getPlayerControl().start();
                    }
                }

                break;

            case R.id.action_history:
                openHistory();

                break;

        }
    }

    private void showVideoLayout(boolean show){
        root.setVisibility(show ? View.VISIBLE : View.GONE);
        imageCamera.setVisibility(!show ? View.VISIBLE : View.GONE);
        textViewLive.setVisibility(!show ? View.VISIBLE : View.GONE);
        date.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void openHistory(){
        DeviceVideoHistoryFragment newFragment = DeviceVideoHistoryFragment.newInstance();;
        String tag = "DeviceVideoHistoryFragment";
        if(newFragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            newFragment.setPlayVideoHistory(new DeviceVideoHistoryFragment.PlayVideoHistory() {
                @Override
                public void onVideoSelected(FirebaseFileLink selected) {

                    videoLink = selected;
                    new FirebaseHelper().enableLiveStream(device.getId(), false);
                    onHidden();
                    showVideoLayout(true);
                    loadingScreen.setVisibility(View.VISIBLE);
                    root.setVisibility(View.INVISIBLE);
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onShown();
//                            loadingScreen.bringToFront();
//                        }
//                    },3000);
                    onShown();
                    switchLive.setOnCheckedChangeListener(null);
                    switchLive.setChecked(false);
                    switchLive.setOnCheckedChangeListener(DeviceCameraFragment.this);
                }
            });

            ft.addToBackStack(null);
            newFragment.show(ft, tag);
        }
    }


    @TargetApi(23)
    private boolean maybeRequestPermission() {
        if (requiresPermission(contentUri)) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            preparePlayer(true);
        } else {
            Toast.makeText(AppController.getAppContext(), "Need storage permission",
                    Toast.LENGTH_LONG).show();
        }
    }

    @TargetApi(23)
    private boolean requiresPermission(Uri uri) {
        return Util.SDK_INT >= 23
                && Util.isLocalFileUri(uri)
                && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
    }

    @Override
    public void onError(Exception e) {
        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = getString(Util.SDK_INT < 18 ? R.string.error_drm_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
        } else if (e instanceof ExoPlaybackException
                && e.getCause() instanceof MediaCodecTrackRenderer.DecoderInitializationException) {
            // Special case for decoder initialization failures.
            MediaCodecTrackRenderer.DecoderInitializationException decoderInitializationException =
                    (MediaCodecTrackRenderer.DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = getString(R.string.error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = getString(R.string.error_no_secure_decoder,
                            decoderInitializationException.mimeType);
                } else {
                    errorString = getString(R.string.error_no_decoder,
                            decoderInitializationException.mimeType);
                }
            } else {
                errorString = getString(R.string.error_instantiating_decoder,
                        decoderInitializationException.decoderName);
            }
        }
        if (errorString != null) {
            Toast.makeText(AppController.getAppContext(), errorString, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//        shutterView.setVisibility(View.GONE);
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
    }







    private void configurePopupWithTracks(PopupMenu popup,
                                          final OnMenuItemClickListener customActionClickListener,
                                          final int trackType) {
        if (player == null) {
            return;
        }
        int trackCount = player.getTrackCount(trackType);
        if (trackCount == 0) {
            return;
        }
        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return (customActionClickListener != null
                        && customActionClickListener.onMenuItemClick(item))
                        || onTrackItemClick(item, trackType);
            }
        });
        Menu menu = popup.getMenu();
        // ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0).
        menu.add(MENU_GROUP_TRACKS, DemoPlayer.TRACK_DISABLED + ID_OFFSET, Menu.NONE, R.string.off);
        for (int i = 0; i < trackCount; i++) {
            menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE,
                    buildTrackName(player.getTrackFormat(trackType, i)));
        }
        menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
        menu.findItem(player.getSelectedTrack(trackType) + ID_OFFSET).setChecked(true);
    }

    private boolean onTrackItemClick(MenuItem item, int type) {
        if (player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
            return false;
        }
        player.setSelectedTrack(type, item.getItemId() - ID_OFFSET);
        return true;
    }

    private static String buildTrackName(MediaFormat format) {
        if (format.adaptive) {
            return "auto";
        }
        String trackName = "";
        if (MimeTypes.isVideo(format.mimeType)) {
            trackName = "um video qq";
        }
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        new FirebaseHelper().enableLiveStream(device.getId(), isChecked);
        if(isChecked){

            onHidden();
            showVideoLayout(false);
        }
    }


    private static final class KeyCompatibleMediaController extends MediaController {

        private MediaController.MediaPlayerControl playerControl;

        public KeyCompatibleMediaController(Context context) {
            super(context);
        }

        @Override
        public void setMediaPlayer(MediaController.MediaPlayerControl playerControl) {
            super.setMediaPlayer(playerControl);
            this.playerControl = playerControl;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (playerControl.canSeekForward() && (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                    || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() + 15000); // milliseconds
                    show();
                }
                return true;
            } else if (playerControl.canSeekBackward() && (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                    || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    playerControl.seekTo(playerControl.getCurrentPosition() - 5000); // milliseconds
                    show();
                }
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
    }

}
