package eventhorizon.horizonsms;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by marcusmotill on 11/22/15.
 */
public class Application extends android.app.Application {

    public static Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new Bus(ThreadEnforcer.ANY);
    }
}
