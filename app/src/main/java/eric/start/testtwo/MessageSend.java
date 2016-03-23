package eric.start.testtwo;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;


public class MessageSend extends ActionBarActivity {

    TextView tvMessageSend;
    EditText etNickName;
    String iD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMessageSend);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tvMessageSend=(TextView) findViewById(R.id.tvMessageSend);


        tvMessageSend.setText(getIntent().getStringExtra("text"));

        //Log.d("myLogs","333333333333333333333  " + getIntent().getStringExtra("id"));

    }


    public void onStart(final View v){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(MessageSend.this, R.style.AppCompatAlertDialogStyle);
        etNickName =(EditText) getLayoutInflater().inflate(R.layout.edittext,null);
        builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //All of the fun happens inside the CustomListener now.
                //I had to move it to enable data validation.


                VKRequest request = new VKRequest("messages.send", VKParameters.from("user_id", getIntent().getStringExtra("id"), "message", etNickName.getText()));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        Snackbar.make(v, "Сообщение отправлено", Snackbar.LENGTH_LONG).show();

                    }
                });

            }
        }).setNegativeButton("Отмена", null);


        builder.setView(etNickName);
        builder.setTitle("Сообщение");



        /* Show the dialog */
        builder.show();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_send, menu);
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
}
