package cz.boris.demo;

import android.app.Activity;
import android.os.Bundle;

import cz.boris.demo.fragment.NetworkFragment;

/**
 * Created by Boris Musatov on 6.3.14.
 */
public class NetworkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        getFragmentManager().beginTransaction().add(R.id.fragment_container, NetworkFragment.newInstance()).commit();
    }
}
