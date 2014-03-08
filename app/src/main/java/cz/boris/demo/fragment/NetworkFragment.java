package cz.boris.demo.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.boris.demo.R;
import cz.boris.demo.model.Repo;
import cz.boris.demo.model.User;
import cz.boris.demo.network.GitService;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Boris Musatov on 6.3.14.
 */
public class NetworkFragment extends Fragment implements View.OnClickListener {

    private Observable<User> user;
    private Observable<Repo[]> repos;
    private Observable<Bitmap> photo;
    private ListView repoList;
    private Button done;
    private EditText search;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.network_fragment, container, false);
        done = (Button) root.findViewById(R.id.done);
        search = (EditText) root.findViewById(R.id.search);
        user = Observable.create((Subscriber<? super User> subscriber) -> {
            try {
                User result = GitService.user(search.getText().toString());
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        done.setOnClickListener(this);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        repoList = (ListView) getView().findViewById(R.id.repos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                if (!TextUtils.isEmpty(search.getText().toString())) {
                    user.subscribe(result -> {
                        if (result != null) {
                            ((TextView) getView().findViewById(R.id.name)).setText(result.name);
                            ((TextView) getView().findViewById(R.id.email)).setText(result.email);
                            ((TextView) getView().findViewById(R.id.company)).setText(result.company);
                        }
                    });
                    repos = user.map(userResult -> {
                        if (userResult != null) {
                            ExecutorService es = Executors.newCachedThreadPool();
                            Future<Repo[]> data = es.submit(() -> GitService.repos(userResult.login));
                            es.shutdown();
                            try {
                                return data.get();
                            } catch (Exception e) {
                            }
                        }
                        return null;
                    }).observeOn(AndroidSchedulers.mainThread());
                    repos.subscribe(new Action1<Repo[]>() {
                        @Override
                        public void call(Repo[] repos) {
                            if (repos != null) {
                                ArrayAdapter<Repo> repoArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, repos);
                                repoList.setAdapter(repoArrayAdapter);
                            }
                        }
                    });
                    photo = user.map(new Func1<User, Bitmap>() {
                        @Override
                        public Bitmap call(User user) {
                            if (user != null) {
                                ExecutorService es = Executors.newCachedThreadPool();
                                Future<Bitmap> result = es.submit(() -> GitService.bitmapImage(user.avatar_url));
                                es.shutdown();
                                try {
                                    return result.get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }
                    }).observeOn(AndroidSchedulers.mainThread());
                    photo.subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            if (bitmap != null)
                                ((ImageView) getView().findViewById(R.id.avatar)).setImageBitmap(bitmap);
                        }
                    });
                }
                break;
        }
    }
}
