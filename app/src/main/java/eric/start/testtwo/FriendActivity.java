package eric.start.testtwo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;


public class FriendActivity extends ActionBarActivity {

    ImageLoader imageLoader;
    String iD;
    Intent intentService;
    BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "eric.start.testtwo.friend";
    public final static String homePhone = "homePhone";
    public final static String status = "status";
    public final static String city = "city";
    public final static String mobilePhone = "mobilePhon";
    public final static String firstName = "firstName";
    public final static String lastName = "lastName";
    public final static String universityName = "university_name";
    public final static String about = "about";
    public final static String quotes = "qoutes";
    EditText etNickName;
    Toolbar toolbar;
    ActionBar actionBar;
    ImageView ivFriend;
    TextView statusTV,cityTV,contactsTV,homeTV,universityTV,aboutTV,quotesTV;
    IntentFilter intFilt;
    FloatingActionButton fabPhoto,fabMessage;
    AlertDialog.Builder builder;
    VKRequest request;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        toolbar = (Toolbar) findViewById(R.id.toolbarFriend);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));


        ivFriend=(ImageView) findViewById(R.id.ivFriend);

        imageLoader.displayImage(getIntent().getStringExtra("Url"), ivFriend);
        iD=getIntent().getStringExtra("id_friend");

        statusTV=(TextView) findViewById(R.id.status);
        cityTV=(TextView) findViewById(R.id.city);
        contactsTV=(TextView) findViewById(R.id.contacts);
        homeTV=(TextView) findViewById(R.id.home);
        universityTV=(TextView) findViewById(R.id.university);
        aboutTV=(TextView) findViewById(R.id.about);
        quotesTV=(TextView) findViewById(R.id.quotes);

        intentService=new Intent(this,MyIntentService.class);

        br=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                if(!(intent.getStringExtra(status)).contentEquals("")){statusTV.setText(intent.getStringExtra(status));}
                if(!(intent.getStringExtra(city)).contentEquals("")){cityTV.setText(intent.getStringExtra(city));}
                if(!(intent.getStringExtra(mobilePhone)).contentEquals("")){contactsTV.setText(intent.getStringExtra(mobilePhone));}
                if(!(intent.getStringExtra(firstName)).contentEquals("")&&!(intent.getStringExtra(lastName)).contentEquals("")){toolbar.setTitle(intent.getStringExtra(firstName) + " " + intent.getStringExtra(lastName));}
                if(!(intent.getStringExtra(homePhone)).contentEquals("")){homeTV.setText(intent.getStringExtra(homePhone));}
                if(!(intent.getStringExtra(universityName)).contentEquals("")){universityTV.setText(intent.getStringExtra(universityName));}
                if(!(intent.getStringExtra(about)).contentEquals("")){aboutTV.setText(intent.getStringExtra(about));}
                if(!(intent.getStringExtra(quotes)).contentEquals("")){quotesTV.setText(intent.getStringExtra(quotes));}


            }

        };

        intFilt = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(br, intFilt);

        startService(intentService.putExtra("Task", "countFriendsUser").putExtra("id", iD));


        fabPhoto = (FloatingActionButton) findViewById(R.id.fabPhoto);
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                startActivity(new Intent(FriendActivity.this, CollageActivity.class).putExtra("User","Friend").putExtra("idFriend",iD));

            }
        });


        fabMessage = (FloatingActionButton) findViewById(R.id.fabMessage);
        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                builder = new AlertDialog.Builder(FriendActivity.this, R.style.AppCompatAlertDialogStyle);
                etNickName =(EditText) getLayoutInflater().inflate(R.layout.edittext,null);
                builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        request = new VKRequest("messages.send", VKParameters.from("user_id", iD, "message", etNickName.getText()));
                        request.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);

                                Snackbar.make(v, "Сообщение отправлено", Snackbar.LENGTH_LONG).show();

                            }
                        });

                    }
                });
                builder.setNegativeButton("Отмена", null);

                builder.setView(etNickName);
                builder.setTitle("Сообщение");

                builder.show();

            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend, menu);
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

}
