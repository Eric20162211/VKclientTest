package eric.start.testtwo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;


public class MusicActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Intent intentService;
    BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "eric.start.testtwo.music";
    public final static String PARAM_RESULT_1 = "result_1";
    private static final int LOADER_ID_1 = 1;
    static String[] audio_trek;
    static ArrayList<String> items_audio;
    RecyclerView rv;
    static Snackbar snackbar;
    private static MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        br = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String answer = intent.getStringExtra("Answer");



                switch(answer){

                    case "MusicTable":

                        startService(intentService.putExtra("Task", "UrlMusic"));

                        break;
                    case "UrlMusic":
                        audio_trek = intent.getStringArrayExtra(PARAM_RESULT_1);
                        getSupportLoaderManager().initLoader(LOADER_ID_1, null, MusicActivity.this);

                        break;

                }


            }};

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(br, intFilt);




        intentService=new Intent(MusicActivity.this,MyIntentService.class);

        startService(intentService.putExtra("Task", "MusicTable"));// заполнение таблицы аудио с url для списка музыки

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMusic);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rv = (RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri=null;
        String[] sl=null;

        if(id == LOADER_ID_1){

            uri=Uri.parse("content://eric.start.provider.TestTwo/audio");
            sl=new String[]{"artist","title"};

        }



        return new CursorLoader(this, uri, sl, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        items_audio=new ArrayList<>();

        if(LOADER_ID_1==loader.getId()){


            if(data.moveToLast()){

                int artistIndex=data.getColumnIndex("artist");
                int titleIndex=data.getColumnIndex("title");


                do{

                    items_audio.add(data.getString(artistIndex)+" "+data.getString(titleIndex));

                }while(data.moveToPrevious());


            }


        }

        rv.setAdapter(new DesignDemoRecyclerAdapter(items_audio));


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    public static class DesignDemoRecyclerAdapter extends RecyclerView.Adapter<DesignDemoRecyclerAdapter.ViewHolder> {

        List<String> mItems;


        DesignDemoRecyclerAdapter(List<String> items) {
            this.mItems = items;

        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

            View v = null;//LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);;

                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_audio, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder( ViewHolder viewHolder, int i) {
            String item = mItems.get(i);
            viewHolder.mTextView.setText(item);
            final int s=i;
            viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context context = view.getContext();

                    if (mediaPlayer != null) {
                        try {


                            snackbar = Snackbar.make(view, audio_trek[s], Snackbar.LENGTH_INDEFINITE).setAction("Stop", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mediaPlayer.release();
                                    mediaPlayer = null;

                                }
                            });
                            snackbar.show();




                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {

                        snackbar = Snackbar.make(view, audio_trek[s], Snackbar.LENGTH_INDEFINITE).setAction("Play", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Thread t1=new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mediaPlayer = new MediaPlayer();
                                        try {

                                            mediaPlayer.setDataSource(audio_trek[s]);

                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                            mediaPlayer.prepare();
                                            mediaPlayer.start();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                t1.start();
                            }
                        });
                        snackbar.show();

                    }






                }
            });







        }

        @Override
        public int getItemCount() {


            return mItems == null ? 0 : mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private   TextView mTextView;


            ViewHolder(View v) {
                super(v);

                mTextView = (TextView)v.findViewById(R.id.list_item1);



            }
        }

    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:

                finish();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }


}
