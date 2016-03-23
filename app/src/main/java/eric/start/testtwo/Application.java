package eric.start.testtwo;

import com.vk.sdk.VKSdk;

/**
 * Created by Я on 10.01.2016.
 */
public class Application extends android.app.Application {



    @Override
    public void onCreate() {
        super.onCreate();

        VKSdk.initialize(this);
    }
}
