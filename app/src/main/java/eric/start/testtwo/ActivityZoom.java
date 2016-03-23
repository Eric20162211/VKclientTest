package eric.start.testtwo;


import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.imagezoom.ImageViewTouch;
import com.imagezoom.ImageViewTouchBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



public class ActivityZoom extends ActionBarActivity {

    String urlPhoto;
    Toolbar toolbar;
    ActionBar actionBar;
    ImageViewTouch mImageView;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);


        urlPhoto = getIntent().getStringExtra("UrlPhoto");

        toolbar = (Toolbar) findViewById(R.id.toolbarZoom);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mImageView = (ImageViewTouch) findViewById(R.id.imageViewTouch);

        mImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        imageLoader.displayImage(urlPhoto, mImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_zoom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finish();

        return super.onOptionsItemSelected(item);
    }
}
