package eric.start.testtwo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;


public class CollageActivity extends ActionBarActivity {

    public final static String PARAM_RESULT = "result";
    public final static String BROADCAST_ACTION = "eric.start.testtwo.collage";
    BroadcastReceiver br;
    ArrayList<String> items=null;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    Toolbar toolbar;
    ActionBar actionBar;
    GridView gridView;
    MyGridAdapter adapter;
    String[] iv1;
    IntentFilter intFilt;
    Intent intentS2;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);

        items = new ArrayList<String>();

        toolbar = (Toolbar) findViewById(R.id.toolbarCollage);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        gridView=(GridView) findViewById(R.id.GV);

        br=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                iv1=intent.getStringArrayExtra(PARAM_RESULT);

                for(int i=0;i<intent.getIntExtra("length",0);i++){

                    items.add(iv1[i]);

                }

                adapter = new MyGridAdapter(CollageActivity.this, items);
                gridView.setNumColumns(3);
                gridView.setAdapter(adapter);

            }
        };

        intFilt = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(br, intFilt);
        intentS2=new Intent(this,MyIntentService.class);


        if((getIntent().getStringExtra("User")).contentEquals("I'am")){

            startService(intentS2.putExtra("Task", "collage").putExtra("User","I'am"));

        }
        else if((getIntent().getStringExtra("User")).contentEquals("Friend")){

            startService(intentS2.putExtra("Task", "collage").putExtra("User", "Friend").putExtra("idFriend", getIntent().getStringExtra("idFriend")));

        }


        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_action_music_1)
                .cacheInMemory(true)
                .build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_collage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }


    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(br);
    }


    class MyGridAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> items;

        MyGridAdapter(Context context, ArrayList<String> items) {
            this.context=context;
            this.items=items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int arg0) {
            return items.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                imageView = new ImageView(CollageActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(10,10,10,10);
            } else imageView=(ImageView) convertView;


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {startActivity(new Intent(CollageActivity.this,ActivityZoom.class).putExtra("UrlPhoto", items.get(position)));
                }
            });


            imageLoader.displayImage(items.get(position), imageView,options);

            return imageView;
        }

    }


}
