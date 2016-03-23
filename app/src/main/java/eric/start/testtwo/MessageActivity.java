package eric.start.testtwo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MessageActivity extends ActionBarActivity {

    static ImageLoader imageLoader;
    RecyclerView rv;
    Intent intentService;
    BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "eric.start.testtwo.message";
    public final static String PARAM_RESULT = "result";
    String[] messageBody;
    String user_id;
    static String[] photo;
    String[] as;
    int[] out;
    String[] iD;
    DesignDemoRecyclerAdapter dp;
    IntentFilter intFilt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMessage);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rv = (RecyclerView)findViewById(R.id.rvMessage);


        intentService=new Intent(MessageActivity.this,MyIntentService.class);





        br = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String answer = intent.getStringExtra("Answer");


                switch(answer){

                    case "Message":

                        messageBody=intent.getStringArrayExtra("MessageBody");

                        dp=new DesignDemoRecyclerAdapter(messageBody);

                        user_id=intent.getStringExtra("user_id");

                        iD=intent.getStringArrayExtra("MessageSendId");

                        out=intent.getIntArrayExtra("out");


                            startService(intentService.putExtra("Task", "UrlMessageFriend").putExtra("id",user_id));



                    break;

                    case "UrlMessageFriend":

                        as=intent.getStringArrayExtra(PARAM_RESULT);

                        rv.setAdapter(dp);
                        LinearLayoutManager llm = new LinearLayoutManager(MessageActivity.this);
                        rv.setLayoutManager(llm);

                    break;

                }









            }};

        intFilt = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(br, intFilt);



        startService(intentService.putExtra("Task", "Message"));


    }

    protected void onRestart(){
        super.onRestart();
        startService(intentService.putExtra("Task", "Message"));
        dp.notifyDataSetChanged();

    }



    public class DesignDemoRecyclerAdapter extends RecyclerView.Adapter<DesignDemoRecyclerAdapter.ViewHolder> {

        String[] mItems;


        DesignDemoRecyclerAdapter(String[] items) {
            this.mItems = items;

        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {


            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);


            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder( ViewHolder viewHolder, int i) {
            String item="";
            if(out[i]==1) item ="Исх.: " + mItems[i];
            else if(out[i]==0) item ="Вх.: " + mItems[i];

            final String j=mItems[i];
            final String l=iD[i];

            viewHolder.mTextView.setText(item);

            viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MessageActivity.this, MessageSend.class);

                    startActivity(intent.putExtra("text", j).putExtra("id", l));

                }
            });

            final DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.ic_action_emo_evil)
                    .cacheInMemory(true)
                    .build();



            imageLoader.displayImage(as[i], viewHolder.mImageView, options);





        }

        @Override
        public int getItemCount() {
            return mItems.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTextView;
            private  final CircularImageView mImageView;

            ViewHolder(View v) {
                super(v);


                mTextView = (TextView)v.findViewById(R.id.list_item);

                mImageView=(CircularImageView)v.findViewById(R.id.IVL);



            }
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
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
