package cz.boris.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

public class MainActivity extends Activity {

    ImageView imageView;
    Observable<Bitmap> imageObservable;
    ProgressBar progressBar;
    TextView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        next = (TextView) findViewById(R.id.hello);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NetworkActivity.class);
                startActivity(intent);
            }
        });
        imageObservable = Async.start(() -> longOperation(), Schedulers.io()).cache().observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        imageObservable.subscribe(image -> imageView.setImageBitmap(image));
        imageObservable.first().subscribe(any -> progressBar.setVisibility(View.GONE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageObservable = null;
    }

    private Bitmap longOperation() {
        Bitmap bitmap = null;
        try {
            // simulate long blocking operation here
            TimeUnit.SECONDS.sleep(5);
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        } catch (Exception e) {

        } finally {

        }
        return bitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
