package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MusicController {

    ImageView imageSong, imageMiniSong;
    ImageButton vertical_dots_btn, btn_down,checked_button, add_id_btn;
    Button showArtist, shareSong;
    TextView nameSong, nameArtist, startTime, endTime, nameMiniSong, nameMiniArtist;
    ImageView btn_play, btn_pause, btn_previous, btn_next;
    SeekBar sBar;
    MediaPlayer mediaPlayer;
    int songLength, currentTime;
    View popupView;
    PopupWindow popupWindow;
    View view_under_popup;
    ViewGroup mainLayout;
    View musicLayout;
    LinearLayout LayoutContentMusic, miniPlayer;
    private Handler hdlr = new Handler();

    public void addViewPlayMusic(Activity activity)
    {
        if(musicLayout != null && musicLayout.getParent() != null)
        {
            fullPlayer();
        }
        else{LayoutInflater inflater = LayoutInflater.from(activity);
            musicLayout = inflater.inflate(R.layout.play_music, null);
            LayoutContentMusic = musicLayout.findViewById(R.id.LayoutContentMusic);
            miniPlayer = musicLayout.findViewById(R.id.miniPlayer);
            fullPlayer();
            mainLayout = activity.findViewById(R.id.mainActivity);
            mainLayout.addView(musicLayout);

            addControls(activity);
            addEvents(activity);
            //getData(activity);

            // xóa sau
            Picasso.get().load("https://www.nonstop2k.com/midi-files/covers/12092.jpg").into(imageSong);
            Picasso.get().load("https://www.nonstop2k.com/midi-files/covers/12092.jpg").into(imageMiniSong);
        }
    }


    private void addControls(Activity activity)
    {
        // popupWindow
        popupView = LayoutInflater.from(activity).inflate(R.layout.popup_menu_play_music, null);
        popupWindow = new PopupWindow(popupView);
        popupWindow.setWidth(400);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);


        view_under_popup = activity.findViewById(R.id.view_under_popup);
        imageSong = activity.findViewById(R.id.imageSong);
        vertical_dots_btn = activity.findViewById(R.id.vertical_dots_btn);
        showArtist = popupView.findViewById(R.id.showArtist);
        shareSong = popupView.findViewById(R.id.shareSong);
        btn_down = activity.findViewById(R.id.btn_down);
        btn_play = activity.findViewById(R.id.btn_play);
        btn_pause = activity.findViewById(R.id.btn_pause);
        btn_previous = activity.findViewById(R.id.btn_previous);
        btn_next = activity.findViewById(R.id.btn_next);
        nameSong = activity.findViewById(R.id.nameSong);
        nameArtist = activity.findViewById(R.id.nameArtist);
        sBar = activity.findViewById(R.id.sBar);
        startTime = activity.findViewById(R.id.startTime);
        endTime = activity.findViewById(R.id.endTime);
        imageMiniSong = activity.findViewById(R.id.imageMiniSong);
        nameMiniSong = activity.findViewById(R.id.nameMiniSong);
        nameMiniArtist = activity.findViewById(R.id.nameMiniArtist);
        add_id_btn = activity.findViewById(R.id.add_id_btn);
        checked_button = activity.findViewById(R.id.checked_button);
        mediaPlayer= new MediaPlayer();
    }

    private void addEvents(Activity activity)
    {
        imageSong.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
                view_under_popup.setVisibility(View.GONE);
            }
        });

        miniPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullPlayer();
            }
        });

        view_under_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                view_under_popup.setVisibility(View.GONE);
            }
        });

        vertical_dots_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_under_popup.setVisibility(View.VISIBLE);
                popupWindow.showAtLocation(popupView, Gravity.TOP | Gravity.END, 30, 80);
            }
        });

        showArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                view_under_popup.setVisibility(View.GONE);
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.popup_artist);
                dialog.getWindow().setBackgroundDrawable(activity.getDrawable(R.drawable.background_popup_artist));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setCancelable(true);
                dialog.show();
                ((ViewGroup)dialog.getWindow().getDecorView())
                        .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                                activity,R.anim.show_popup_artist));
                Picasso.get()
                        .load("https://www.nonstop2k.com/midi-files/covers/12092.jpg")
                        .into((ImageView) dialog.findViewById(R.id.imgArtist));
            }
        });

        shareSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_under_popup.setVisibility(View.GONE);
                popupWindow.dismiss();
            }
        });

        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miniPlayer();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                btn_play.setVisibility(View.GONE);
                btn_pause.setVisibility(View.VISIBLE);
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                btn_pause.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        add_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_id_btn.setVisibility(View.GONE);
                checked_button.setVisibility(View.VISIBLE);
                Toast.makeText(activity, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_id_btn.setVisibility(View.VISIBLE);
                checked_button.setVisibility(View.GONE);
                Toast.makeText(activity, "Đã bỏ khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });


        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Xử lý khi bắt đầu vuốt SeekBar
                hdlr.removeCallbacks(UpdateSongTime);
                btn_pause.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Xử lý khi ngừng vuốt SeekBar
                mediaPlayer.seekTo(sBar.getProgress());
                hdlr.postDelayed(UpdateSongTime,100);
                btn_pause.setVisibility(View.VISIBLE);
                btn_play.setVisibility(View.GONE);
                mediaPlayer.start();
            }
        });
    }

    private void miniPlayer()
    {
        final AnimationSet animationSet = (AnimationSet) AnimationUtils.loadAnimation(musicLayout.getContext(), R.anim.show_mini_player);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        140);
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                params.setMargins(0,0,0,140);
                musicLayout.setLayoutParams(params);

                LayoutContentMusic.setVisibility(View.GONE);
                miniPlayer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        musicLayout.startAnimation(animationSet);
    }

    private void fullPlayer()
    {
        musicLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT));

        LayoutContentMusic.setVisibility(View.VISIBLE);
        miniPlayer.setVisibility(View.GONE);
        musicLayout.startAnimation(AnimationUtils.loadAnimation(musicLayout.getContext(), R.anim.show_full_player));
    }


    private void getData(Activity activity)
    {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "https://v1.nocodeapi.com/dangkhoa/spotify/lvTawhkqIEhjcdsm/tracks?ids=58wyJLv6yH1La9NIZPl3ne";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseJsonData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    void parseJsonData (String jsonString)
    {
        try {
            JSONObject object= new JSONObject(jsonString);
            JSONArray tracksArray = object.getJSONArray("tracks");
            JSONObject albumObject = tracksArray.getJSONObject(0).getJSONObject("album");
            nameSong.setText(tracksArray.getJSONObject(0).getString("name"));
            nameMiniSong.setText(albumObject.getJSONArray("artists").getJSONObject(0).getString("name"));
            nameArtist.setText(albumObject.getJSONArray("artists").getJSONObject(0).getString("name"));
            nameMiniArtist.setText(tracksArray.getJSONObject(0).getString("name"));
            String urlImg = albumObject.getJSONArray("images").getJSONObject(0).getString("url");
            Picasso.get().load(urlImg).into(imageSong);
            Picasso.get().load(urlImg).into(imageMiniSong);
            getAudio(tracksArray.getJSONObject(0).getString("preview_url"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAudio(String audioUrl)
    {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            songLength = mediaPlayer.getDuration();
            currentTime = mediaPlayer.getCurrentPosition();
            sBar.setMax(songLength);

            long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(songLength);
            long remainingMilliseconds = songLength - TimeUnit.MINUTES.toMillis(totalMinutes);
            long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(remainingMilliseconds);
            endTime.setText(String.valueOf(totalMinutes)+":"+String.valueOf(totalSeconds));

            mediaPlayer.start();

            hdlr.postDelayed(UpdateSongTime, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            long currentTime = mediaPlayer.getCurrentPosition();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime);
            long second = TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime));
            if(second<10)
            {
                startTime.setText(minutes+":0"+second);
            }
            else{
                startTime.setText(minutes+":"+second);
            }

            if(sBar.getProgress() == mediaPlayer.getDuration())
            {
                btn_pause.setVisibility(View.GONE);
                btn_play.setVisibility(View.VISIBLE);
            }
            sBar.setProgress((int)currentTime);
            hdlr.postDelayed(this, 100);
        }
    };
}
