package Controls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import Models.Music;
import project.lon.R;

public class MusicController {
    ImageView imageSong, imageMiniSong;
    ImageButton vertical_dots_btn, btn_down,checked_button, add_id_btn;
    Button showArtist, shareSong;
    TextView nameSong, nameArtist, startTime, endTime, nameMiniSong, nameMiniArtist;
    ProgressBar progress_bar;
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

    public void addViewPlayMusic(Activity activity, Music musicObject)
    {
        SharedPreferences currentIdTrack = activity.getSharedPreferences("currentIdTrack", activity.MODE_PRIVATE);
        if(musicLayout != null && musicLayout.getParent() != null)
        {
            loadFunctionInControl(activity,musicObject);
            if(currentIdTrack.getString("idCurrentMusic","default").equals(musicObject.getIdMusic())) {
                fullPlayer();
            }
            else{
                fullPlayer();
                loadData(activity,musicObject,currentIdTrack);
            }
        }
        else{
            LayoutInflater inflater = LayoutInflater.from(activity);
            musicLayout = inflater.inflate(R.layout.play_music, null);
            LayoutContentMusic = musicLayout.findViewById(R.id.LayoutContentMusic);
            miniPlayer = musicLayout.findViewById(R.id.miniPlayer);
            fullPlayer();
            mainLayout = activity.findViewById(R.id.MainDesignMusic);
            mainLayout.addView(musicLayout);
            // để trống vì mục đích là để hủy sự kiện click truyền lên Main_DesignMusic, không nên xóa hàm này
            musicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {}
            });

            addControls(activity);
            addEvents(activity,musicObject);
            loadFunctionInControl(activity,musicObject);
            loadData(activity,musicObject,currentIdTrack);
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
        progress_bar = activity.findViewById(R.id.progress_bar);
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

    private void addEvents(Activity activity,Music musicObject)
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
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
            }
        });


        checked_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checked_button.setVisibility(View.GONE);
                progress_bar.setVisibility(View.VISIBLE);
                SharedPreferences user = activity.getSharedPreferences("idUser", activity.MODE_PRIVATE);
                String id = user.getString("id","default ID");
                SharedPreferences track = activity.getSharedPreferences("currentIdTrack", activity.MODE_PRIVATE);

                // Lấy instance của FirebaseFirestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Lấy tham chiếu đến tài liệu cần xóa
                DocumentReference docRef = db.collection(id).document(track.getString("idCurrentMusic","default"));

                // Xóa tài liệu
                docRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            progress_bar.setVisibility(View.GONE);
                            add_id_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(activity, "Đã bỏ khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progress_bar.setVisibility(View.GONE);
                            checked_button.setVisibility(View.VISIBLE);
                            Toast.makeText(activity, "Bỏ khỏi danh sách yêu thích thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
        });


        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

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
            public void onAnimationStart(Animation animation) {}

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
            public void onAnimationRepeat(Animation animation) {}
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


    private void loadData(Activity activity, Music musicObject, SharedPreferences currentIdTrack)
    {
        // kiểm tra nhạc có trong danh sách yêu thích không?
        SharedPreferences sharedPreferencesLibrary = activity.getSharedPreferences("idUser", activity.MODE_PRIVATE);
        String id = sharedPreferencesLibrary.getString("id","default ID");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(id).document(musicObject.getIdMusic()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // nhạc yêu thích tồn tại
                            progress_bar.setVisibility(View.GONE);
                            add_id_btn.setVisibility(View.GONE);
                            checked_button.setVisibility(View.VISIBLE);

                        } else {
                            // nhạc yêu thích ko tồn tại
                            progress_bar.setVisibility(View.GONE);
                            checked_button.setVisibility(View.GONE);
                            add_id_btn.setVisibility(View.VISIBLE);
                        }
                    }
                });

        // load lại thông tin
        nameSong.setText(musicObject.getTrack_name());
        nameMiniSong.setText(musicObject.getTrack_name());
        nameArtist.setText(musicObject.getArtist_name());
        nameMiniArtist.setText(musicObject.getArtist_name());
        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        String urlImg = musicObject.getImages();
        Picasso.get().load(urlImg).into(imageSong);
        Picasso.get().load(urlImg).into(imageMiniSong);
        getAudio(musicObject.getPreview_url());

        // lưu thông tin nhạc để tránh tải lại nhạc khi click vào listview nhạc
        SharedPreferences.Editor editor = currentIdTrack.edit();
        editor.putString("idCurrentMusic", musicObject.getIdMusic());
        editor.apply();
    }

    private void getAudio(String audioUrl)
    {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.reset();
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

    // khi khởi tạo thì musicObject đã là cũ trong bộ nhớ nên cần load lại
    private void loadFunctionInControl(Activity activity, Music musicObject)
    {
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

                RequestQueue queue = Volley.newRequestQueue(activity);
                String url = "https://v1.nocodeapi.com/doan3/spotify/MEJELqesYYBQryjG/artists?id="+musicObject.getId_artist();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                    {
                        try {
                            JSONObject newResponse = new JSONObject(response);
                            String urlImgArtist = newResponse.getJSONArray("images").getJSONObject(0).getString("url");
                            String followers = newResponse.getJSONObject("followers").getString("total");
                            Picasso.get()
                                    .load(urlImgArtist)
                                    .into((ImageView) dialog.findViewById(R.id.imgArtist));
                            TextView tv = dialog.findViewById(R.id.nameArtistDialog);
                            tv.setText(musicObject.getArtist_name());
                            TextView tv1 = dialog.findViewById(R.id.followersDialog);
                            tv1.setText(followers);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "Lỗi data", Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);

                dialog.show();
                ((ViewGroup)dialog.getWindow().getDecorView())
                        .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                                activity,R.anim.show_popup_artist));
            }
        });

        add_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_id_btn.setVisibility(View.GONE);
                progress_bar.setVisibility(View.VISIBLE);
                SharedPreferences sharedPreferencesLibrary = activity.getSharedPreferences("idUser", activity.MODE_PRIVATE);
                String id = sharedPreferencesLibrary.getString("id","default ID");
                // Lấy đối tượng Firestore Database
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> track = new HashMap<>();
                track.put("idMusic", musicObject.getIdMusic());
                track.put("images", musicObject.getImages());
                track.put("track_name", musicObject.getTrack_name());
                track.put("album_name", musicObject.getAlbum_name());
                track.put("preview_url", musicObject.getPreview_url());
                track.put("artist_name", musicObject.getArtist_name());
                track.put("id_artist", musicObject.getId_artist());

                // Ghi dữ liệu vào Firestore Database
                db.collection(id)
                        .document(musicObject.getIdMusic())
                        .set(track)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                                progress_bar.setVisibility(View.GONE);
                                checked_button.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                progress_bar.setVisibility(View.GONE);
                                add_id_btn.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        shareSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_under_popup.setVisibility(View.GONE);
                popupWindow.dismiss();

                // Lấy nội dung cần chia sẻ
                String content = musicObject.getPreview_url();

                // Tạo Intent chia sẻ
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, content);

                // Khởi chạy Activity chia sẻ
                activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
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
