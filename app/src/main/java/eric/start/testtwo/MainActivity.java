package eric.start.testtwo;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.ArrayList;
import java.util.List;




public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {


    static  String titleOfTab1 = "";
    static  String titleOfTab2 = "";

    private static final int idOfLoader = 1;

    static ArrayList<String> itemsFriends,itemsFriendsOnline;

    public final static String BROADCAST_ACTION = "eric.start.testtwo";

    public final static String PARAM_RESULT_1 = "result_1";
    public final static String PARAM_RESULT_2 = "result_2";

    static String[] photoFriends, photoAccountFriend, idFriends;

    //static String[] photoFriendsOnline = new String[5000];

    static List<String> photoFriendsOnline = new ArrayList<String>();

    //static String[] photoAccountFriendOnline = new String[5000];

    static List<String> photoAccountFriendOnline = new ArrayList<String>();

    //static String[] idFriendsOnline = new String[5000];

    static List<String> idFriendsOnline = new ArrayList<String>();

    static  int countTabs = 0;


    Toolbar toolbar;
    IntentFilter intFilt;
    ViewPager viewPager;
    TabLayout tabLayout;
    String answer;
    ProgressBar progress;
    ActionBar actionBar;
    CircularImageView IV;
    Intent intentService;
    NavigationView navigationView;
    static ImageLoader imageLoader;
    static LayoutInflater inflater;
    BroadcastReceiver br;
    private DrawerLayout mDrawerLayout;

    String titleOfTab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        inflater = getLayoutInflater();


        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        IV=(CircularImageView) findViewById(R.id.IV);


        intentService=new Intent(MainActivity.this,MyIntentService.class);

        br = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                answer = intent.getStringExtra("Answer");


                switch (answer) {

                    case "ImageHeaderOfNavigationView":
                        imageLoader.displayImage(intent.getStringExtra(PARAM_RESULT_1), IV);
                        break;

                    case "FriendsTable":
                        startService(intentService.putExtra("Task", "UrlPhotoOfFriendsForList"));//получаем url фоток друзей для списка
                        break;

                    case "UrlPhotoOfFriendsForList":
                        photoFriends = intent.getStringArrayExtra(PARAM_RESULT_1);
                        startService(intentService.putExtra("Task", "DataFotFriendActivity"));
                        break;

                    case "DataFotFriendActivity":
                        photoAccountFriend = intent.getStringArrayExtra(PARAM_RESULT_1);
                        idFriends = intent.getStringArrayExtra(PARAM_RESULT_2);
                        getSupportLoaderManager().initLoader(idOfLoader, null, MainActivity.this);
                        break;

                }


            }

        };



        intFilt = new IntentFilter(BROADCAST_ACTION);

        registerReceiver(br, intFilt);





        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        progress=(ProgressBar) findViewById(R.id.progress);


        viewPager = (ViewPager)findViewById(R.id.viewpager);

        viewPager.getLayoutParams().height=0;
        tabLayout = (TabLayout)findViewById(R.id.tablayout);

        tabLayout.getLayoutParams().height=0;

        startService(intentService.putExtra("Task", "ImageHeaderOfNavigationView"));

        startService(intentService.putExtra("Task", "FriendsTable"));

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();


                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_messages:

                        startActivity(new Intent(MainActivity.this, MessageActivity.class));

                        break;
                    case R.id.navigation_item_friends:

                        startService(intentService.putExtra("Task", "FriendsTable"));// зачем вызывать??

                        break;
                    case R.id.navigation_item_audio:

                        startActivity(new Intent(MainActivity.this, MusicActivity.class));

                        break;
                    case R.id.navigation_item_foto:

                        startActivity(new Intent(MainActivity.this,CollageActivity.class).putExtra("User","I'am"));

                        break;

                }

                return true;
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {getMenuInflater().inflate(R.menu.menu_main, menu);return true;}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

                mDrawerLayout.openDrawer(GravityCompat.START);

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        Uri uri=Uri.parse("content://eric.start.provider.TestTwo/friends");
        String[] sl=new String[]{"firstName","secondName"};


        return new CursorLoader(this, uri, sl, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        itemsFriends = new ArrayList<>();
        itemsFriendsOnline = new ArrayList<>();


            if (data.moveToFirst()) {

                int nameColIndex = data.getColumnIndex("firstName");
                int emailColIndex = data.getColumnIndex("secondName");

                int j = 0;
                int jj = 0;

                do {

                    if (data.getString(emailColIndex).contains("1")) {

                        itemsFriendsOnline.add(data.getString(nameColIndex));
                        //photoFriendsOnline[jj] = photoFriends[j];
                        photoFriendsOnline.add(photoFriends[j]);
                        //photoAccountFriendOnline[jj] = photoAccountFriend[j];
                        photoAccountFriendOnline.add(photoAccountFriend[j]);
                        //idFriendsOnline[jj] = idFriends[j];
                        idFriendsOnline.add(idFriends[j]);
                        jj++;
                    }
                    itemsFriends.add(data.getString(nameColIndex));

                    j++;
                } while (data.moveToNext());

            }

            progress.getLayoutParams().height = 0;
            tabLayout.getLayoutParams().height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;
            viewPager.getLayoutParams().height = android.app.ActionBar.LayoutParams.WRAP_CONTENT;



            titleOfTab1 = "Все Друзья";
            titleOfTab2 = "Онлайн";
            countTabs = 2;
            DesignDemoPagerAdapter adapter1 = new DesignDemoPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter1);
            tabLayout.setupWithViewPager(viewPager);





    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }



    class DesignDemoPagerAdapter extends FragmentStatePagerAdapter {

        public DesignDemoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return DesignDemoFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return countTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            //String titleOfTab = null;

            if( position == 0 ) titleOfTab = titleOfTab1;
            if( position == 1 ) titleOfTab = titleOfTab2;

            return titleOfTab;
        }
    }


    public static class DesignDemoFragment extends Fragment {

        private static final String TAB_POSITION = "tab_position";

        public DesignDemoFragment() {}

        public static DesignDemoFragment newInstance(int tabPosition) {
            DesignDemoFragment fragment = new DesignDemoFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_list_view, container, false);
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            switch(getArguments().getInt(TAB_POSITION)){

                case 1:
                    recyclerView.setAdapter(new DesignDemoRecyclerAdapter(itemsFriendsOnline, 1));
                break;

                case 0:
                    recyclerView.setAdapter(new DesignDemoRecyclerAdapter(itemsFriends, 0));
                break;

            }


            return v;

        }
    }


    public static class DesignDemoRecyclerAdapter extends RecyclerView.Adapter<DesignDemoRecyclerAdapter.ViewHolder> {

        List<String> mItems;
        int mflag;

        DesignDemoRecyclerAdapter(List<String> items,int flag) {
            this.mItems = items;
            this.mflag = flag;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {

               View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);

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


                    switch(mflag){

                        case 0:
                            context.startActivity(new Intent(context,FriendActivity.class).putExtra("Url",photoAccountFriend[s]).putExtra("id_friend", idFriends[s]));
                        break;

                        case 1:
                            //context.startActivity(new Intent(context,FriendActivity.class).putExtra("Url",photoAccountFriendOnline[s]).putExtra("id_friend", idFriendsOnline[s]));
                            context.startActivity(new Intent(context,FriendActivity.class).putExtra("Url", photoAccountFriendOnline.get(s)).putExtra("id_friend", idFriendsOnline.get(s)));
                        break;

                    }


                }
            });



            final DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showStubImage(R.drawable.ic_action_emo_evil)
                    .cacheInMemory(true)
                    .build();


            switch(mflag){

                case 0:
                    imageLoader.displayImage(photoFriends[i], viewHolder.mImageView, options);
                    break;

                case 1:
                    //imageLoader.displayImage(photoFriendsOnline[i], viewHolder.mImageView, options);
                    imageLoader.displayImage(photoFriendsOnline.get(i), viewHolder.mImageView, options);
                    break;

            }


        }

        @Override
        public int getItemCount() {


            return mItems == null ? 0 : mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private  TextView mTextView;
            private  final CircularImageView mImageView;

            ViewHolder(View v) {
                super(v);

                mTextView = (TextView)v.findViewById(R.id.list_item);
                mImageView = (CircularImageView)v.findViewById(R.id.IVL);


            }
        }

    }



    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }




}
