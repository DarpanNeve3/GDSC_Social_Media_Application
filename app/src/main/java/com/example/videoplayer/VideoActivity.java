package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class VideoActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BackgroundMYSQL backgroundMYSQL;
    int videoStatus;
    ImageView like, dislike, likeHeartImg;
    boolean likeStatus=false, dislikeStatus=false;
    Animation scale,rotate;
    VideoView videoView;
    DisplayMetrics displayMetrics;
    SeekBar seekBar;
    Handler handler = new Handler();
    String uname,vname,vlike,vdislike,des,location,vid,lid;
    String unameA[],vnameA[],vlikeA[],vdislikeA[],desA[],locationA[],vidA[],lidA[],liked[],disliked[],followed[];
    StorageReference storageReference;
    Uri videoUri;
    int scroll=0;
    TextView likeNo,dislikeNo,vnameText,unameText;
    Random rand;
    Button follow;
    BottomNavigationView bmv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Objects.requireNonNull(getSupportActionBar()).hide();

        rand = new Random();

        bmv= findViewById(R.id.bmv);
        bmv.setSelectedItemId(R.id.videos);
        bmv.setOnNavigationItemSelectedListener(this);




        
        //Getting the intent
        Intent intent = getIntent();
        uname=intent.getStringExtra("Name");
        vname=intent.getStringExtra("VName");
        vlike=intent.getStringExtra("like");
        vdislike=intent.getStringExtra("dislike");
        des=intent.getStringExtra("des");
        location=intent.getStringExtra("Location");
        vid=intent.getStringExtra("vid");
//        lid=intent.getStringExtra("lid");

        likeNo=findViewById(R.id.textView4);
        dislikeNo = findViewById(R.id.textView);
        vnameText=findViewById(R.id.textView14);
        unameText=findViewById(R.id.textView7);
        follow = findViewById(R.id.button6);

        unameA = uname.split(",");
        vnameA = vname.split(",");
        vlikeA = vlike.split(",");
        vdislikeA = vdislike.split(",");
        desA = des.split(",");
        locationA = location.split(",");
        vidA = vid.split(",");
        liked=new String[vidA.length];
        disliked = new String[vidA.length];
        followed=new String[vidA.length];
        for(int k=0;k<vidA.length;k++)
        {
            liked[k]="-1";
            disliked[k]="-1";
            followed[k]="-1";
        }
//        lidA = lid.split(",");

//        scroll= rand.nextInt(locationA.length);

        //Creating objects
        dislike = findViewById(R.id.imageView3);
        likeHeartImg = findViewById(R.id.imageView9);
        scale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.small_rotate);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        seekBar = findViewById(R.id.seekBar);

        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        storageReference = FirebaseStorage.getInstance().getReference("images/"+locationA[scroll]);
        String path1 = "https://firebasestorage.googleapis.com/v0/b/cpe-project-ddf59.appspot.com/o/videos%2F"+locationA[scroll]+"?alt=media";
        videoUri = Uri.parse(path1);
        likeNo.setText(vlikeA[scroll]);
        dislikeNo.setText(vdislikeA[scroll]);
        vnameText.setText(vnameA[scroll]);
        unameText.setText(unameA[scroll]);

//        for(int k=0;k<lidA.length;k++)
//        {
//            if(lidA[k].equals(vidA[scroll]))
//            {
//                likeStatus=true;
//                like.setTag("1");
//                break;
//            }
//            else
//            {
//                likeStatus=false;
//                like.setTag("0");
//            }
//        }
//        checkLike(likeStatus);


        //Getting video height and width
        MediaPlayer mp ;
//        mp = MediaPlayer.create(this,R.raw.newtest);
//        int width = mp.getVideoWidth();
//        int height = mp.getVideoHeight();
//        Toast.makeText(this, "Width:"+width, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Height:"+height, Toast.LENGTH_SHORT).show();


        //Adding video to the VideoView
        videoView = findViewById(R.id.videoView);
//        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.newtest);
        videoView.setVideoURI(videoUri);
        //Starting the video
        videoView.start();
        videoStatus= 1;

        //Adding the onPreparedListener
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                //setting the maximum length of seekBar
                seekBar.setMax(videoView.getDuration());
                //creating the thread that will run on background for every 1 seconds
                //to move the seekbar with the video
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //setting the progress to the seekbar
                        seekBar.setProgress(videoView.getCurrentPosition());
                        handler.postDelayed(this,1000);
                    }
                };
                handler.post(runnable);
            }
        });

        //Adding onChangeListener to the seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
            }
        });

        //Adding onclick listener to VideoView
        videoView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(videoStatus==1)
                {
                    videoView.pause();
                    videoStatus=0;
                }
                else {
                    videoView.start();
                    videoStatus = 1;
                }
            }
        });


        //Adding onTouch and double tap listener
        videoView.setOnTouchListener(new View.OnTouchListener()
        {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener()
            {


                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float diff = e2.getY() - e1.getY();
                    if(diff  > 0 && scroll>0)
                    {
                        scroll--;
//                        Toast.makeText(VideoActivity.this, "Swipe down...", Toast.LENGTH_SHORT).show();
                        //take the link of next video from the database and set it
                        //this is temporary

                        String path1 = "https://firebasestorage.googleapis.com/v0/b/cpe-project-ddf59.appspot.com/o/videos%2F"+locationA[scroll]+"?alt=media";
                        videoUri = Uri.parse(path1);
                        videoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        videoView.setVideoURI(videoUri);
                        likeNo.setText(vlikeA[scroll]);
                        dislikeNo.setText(vdislikeA[scroll]);
                        vnameText.setText(vnameA[scroll]);
                        unameText.setText(unameA[scroll]);
//                        for(int k=0;k<lidA.length;k++)
//                        {
//                            if(lidA[k].equals(vidA[scroll]))
//                            {
//                                likeStatus=true;
//                                like.setTag("1");
//                                break;
//                            }
//                            else
//                            {
//                                like.setTag("0");
//                                likeStatus=false;
//                            }
//                        }
//                        checkLike(likeStatus);
                        videoView.start();
                        videoStatus= 0;
                        //get the like status for the video from the database
                        //this is temporary
                        like.setTag("0");
                        like.setImageResource(R.drawable.new_like_thumb_white);
                        likeStatus=false;
                        //dislike status
                        dislike.setImageResource(R.drawable.ic_baseline_thumb_down_white);
                        dislike.setTag("0");
                        dislikeStatus=false;
                    }
                    else if(diff < 0 && scroll < locationA.length-1)
                    {
                        scroll++;
//                        Toast.makeText(VideoActivity.this, "Swipe up...", Toast.LENGTH_SHORT).show();
                        //take the link of next video from the database and set it
                        //this is temporary
                        String path1 = "https://firebasestorage.googleapis.com/v0/b/cpe-project-ddf59.appspot.com/o/videos%2F"+locationA[scroll]+"?alt=media";
                        videoUri = Uri.parse(path1);
                        videoView.setVideoURI(videoUri);
                        videoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        likeNo.setText(vlikeA[scroll]);
                        dislikeNo.setText(vdislikeA[scroll]);
                        vnameText.setText(vnameA[scroll]);
                        unameText.setText(unameA[scroll]);
//                        for(int k=0;k<lidA.length;k++)
//                        {
//                            if(lidA[k].equals(vidA[scroll]))
//                            {
//                                likeStatus=true;
//                                break;
//                            }
//                            else
//                            {
//                                likeStatus=false;
//                            }
//                        }
//                        checkLike(likeStatus);
                        videoView.start();
                        videoStatus= 0;
                        //get the like status for the video from the database
                        //this is temporary
                        like.setTag("0");
                        like.setImageResource(R.drawable.new_like_thumb_white);
                        likeStatus=false;
                        //dislike status
                        dislike.setImageResource(R.drawable.ic_baseline_thumb_down_white);
                        dislike.setTag("0");
                        dislikeStatus=false;

                    }

                    return super.onFling(e1, e2, velocityX, velocityY);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    //Adding animation to like image

                    likeHeartImg.setVisibility(View.VISIBLE);
                    likeHeartImg.startAnimation(scale);
                    likeHeartImg.setVisibility(View.INVISIBLE);
                    //Changing the upThumb (like button)
                    if(!disliked[scroll].equals("-1"))
                    {
                        dislike.setImageResource(R.drawable.ic_baseline_thumb_down_white);
                        dislike.setTag("0");
                        disliked[scroll]="-1";
                        minusDisLike();
                        backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
                        backgroundMYSQL.execute("minusDislike",vidA[scroll]);
                        backgroundMYSQL=null;
                        dislikeStatus=false;
                    }
                    like.setTag("1");
                    like.setImageResource(R.drawable.new_like_thumb);
                    like.startAnimation(rotate);
                    likeStatus=true;
                    if(liked[scroll].equals("-1"))
                    {
                        liked[scroll]=vidA[scroll];
                        addLike();
                        backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
                        backgroundMYSQL.execute("addlike",vidA[scroll]);
                        backgroundMYSQL=null;
                    }


                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) 
            {
                gestureDetector.onTouchEvent(motionEvent);

                return false;
            }
        });
        
        //Adding onclick listener to the like image
        like = findViewById(R.id.imageView);
        like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkLike(view);
            }
        });

    }
    public void checkLike(boolean b)
    {
        if(b){
            like.setImageResource(R.drawable.new_like_thumb);
        }
        else
            like.setImageResource(R.drawable.ic_baseline_thumb_up_white);
    }
    public void checkLike(View view)
    {
        ImageView like = (ImageView) findViewById(R.id.imageView);
        String check = like.getTag().toString();


        if(liked[scroll].equals("-1"))
        {
            if(!disliked[scroll].equals("-1"))
            {
                dislike.setImageResource(R.drawable.ic_baseline_thumb_down_white);
                dislike.setTag("0");
                dislikeStatus=false;
                minusDisLike();
                backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
                backgroundMYSQL.execute("minusDislike",vidA[scroll]);
                backgroundMYSQL=null;
                disliked[scroll]="-1";
            }
            like.setTag("1");
            like.setImageResource(R.drawable.new_like_thumb);
            like.startAnimation(rotate);
            liked[scroll]=vidA[scroll];
            addLike();
            backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
            backgroundMYSQL.execute("addlike",vidA[scroll]);
            backgroundMYSQL=null;

//            ldb.addLike(Integer.parseInt(vidA[scroll]));
            likeStatus=true;
        }
        else
        {
            like.setTag("0");
            like.setImageResource(R.drawable.new_like_thumb_white);
//            ldb.removeLike(Integer.parseInt(vidA[scroll]));
            minusLike();
            backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
            backgroundMYSQL.execute("minuslike",vidA[scroll]);
            backgroundMYSQL=null;
            liked[scroll]="-1";
            likeStatus=false;
        }

    }

    public void checkDislike(View view)
    {
        String tag = dislike.getTag().toString();
        if(disliked[scroll].equals("-1"))
        {
            if(!liked[scroll].equals("-1"))
            {
                like.setTag("0");
                like.setImageResource(R.drawable.new_like_thumb_white);
                likeStatus=false;
                liked[scroll]="-1";
                minusLike();
                backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
                backgroundMYSQL.execute("minuslike",vidA[scroll]);
                backgroundMYSQL=null;
            }
            dislike.setImageResource(R.drawable.ic_baseline_thumb_down_24);
            dislike.setTag("1");
            dislikeStatus=true;
            disliked[scroll]=vidA[scroll];
            addDisLike();
            backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
            backgroundMYSQL.execute("addDislike",vidA[scroll]);
            backgroundMYSQL=null;
        }
        else
        {
            dislike.setImageResource(R.drawable.ic_baseline_thumb_down_white);
            dislike.setTag("0");
            dislikeStatus=false;
            disliked[scroll]="-1";
            minusDisLike();
            backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
            backgroundMYSQL.execute("minusDislike",vidA[scroll]);
            backgroundMYSQL=null;
        }

    }

    //showMore function
    public void showMore(View view)
    {
        Intent popUpWindowIntent = new Intent(this,more_options.class);
        popUpWindowIntent.putExtra("des",desA[scroll]);
        popUpWindowIntent.putExtra("vid",vidA[scroll]);
        startActivity(popUpWindowIntent);
    }


    public void goToParent(View view)
    {
        Intent parentIntent = new Intent(this,MainActivity.class);
        startActivity(parentIntent);
    }
    public void followButton(View view)
    {
        if(followed[scroll].equals("-1"))
        {
            followed[scroll]=unameA[scroll];
            SQLiteDatabaseClass sql = new SQLiteDatabaseClass(this);
            int id=sql.getLogin();
            sql=null;
            backgroundMYSQL = new BackgroundMYSQL(getApplicationContext());
            backgroundMYSQL.execute("follow", ""+id,unameA[scroll]);
            backgroundMYSQL=null;
            follow.setText("Unfollow");
        }
        else
        {
            followed[scroll]="-1";
            follow.setText("Follow");
        }
    }

    public void download(View view) {
        String path1 = "https://firebasestorage.googleapis.com/v0/b/cpe-project-ddf59.appspot.com/o/videos%2F"+locationA[scroll]+"?alt=media&token=842e3c95-0e66-4988-b840-cfd48d9a2020";
        downloadManager(path1);
    }
    private void downloadManager(String url) {

        Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT).show();
        try{
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            long reference = manager.enqueue(request);
        }catch (Exception e)
        {
            Log.d("DownloadError",""+e);
        }

    }
    public void addLike()
    {
        int no = Integer.parseInt(likeNo.getText().toString());
        no=no+1;
        likeNo.setText(""+no);
    }
    public void minusLike()
    {
        int no = Integer.parseInt(likeNo.getText().toString());
        no--;
        likeNo.setText(""+no);
    }
    public void addDisLike()
    {
        int no = Integer.parseInt(dislikeNo.getText().toString());
        no++;
        dislikeNo.setText(""+no);
    }
    public void minusDisLike()
    {
        int no = Integer.parseInt(dislikeNo.getText().toString());
        no--;
        dislikeNo.setText(""+no);
    }

    public void sendIt(View view) {
        String path1 = "https://firebasestorage.googleapis.com/v0/b/cpe-project-ddf59.appspot.com/o/videos%2F"+locationA[scroll]+"?alt=media";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Video");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,path1);
        startActivity(Intent.createChooser(shareIntent, "Share via"));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.profile:
                Intent intent =new Intent(this,PreUserProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.videos:
                Intent i = new Intent(this,PreVideoActivity.class);
                startActivity(i);
                return true;

            case R.id.images:
                Intent i1 = new Intent(this,PreImageActivity.class);
                startActivity(i1);
                return true;

            case R.id.audios:
                Intent i2 = new Intent(this,PreAudioActivity.class);
                startActivity(i2);
                return true;
        }
        return false;
    }
}