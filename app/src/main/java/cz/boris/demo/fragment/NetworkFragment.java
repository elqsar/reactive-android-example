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

import cz.boris.demo.R;
import cz.boris.demo.model.Repo;
import cz.boris.demo.model.User;
import cz.boris.demo.network.GitService;
import rx.Observable;
import rx.Subscription;

/**
 * Created by Boris Musatov on 6.3.14.
 */
public class NetworkFragment extends Fragment implements View.OnClickListener {

    private Observable<User> user;
    private Subscription userSubscription;
    private ListView repoList;
    private Button done;
    private EditText search;

    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.network_fragment, container, false);
        setupUI(root);
        return root;
    }

    private void setupUI(View root) {
        done = (Button) root.findViewById(R.id.done);
        search = (EditText) root.findViewById(R.id.search);
        done.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        repoList = (ListView) getView().findViewById(R.id.repos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(isSubscribed()) userSubscription.unsubscribe();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                if (!TextUtils.isEmpty(search.getText().toString())) {
                    user = GitService.user(search.getText().toString()).cache();
                    userSubscription = user.subscribe(result -> processUser(result));
                    user.flatMap(result -> GitService.repos(result.login)).subscribe(result -> processRepos(result));
                    user.flatMap(result -> GitService.bitmapImage(result.avatar_url)).subscribe(result -> processImage(result));
                }
                break;
        }
    }

    private void processUser(User result) {
        ((TextView)getView().findViewById(R.id.name)).setText(result.name);
        ((TextView)getView().findViewById(R.id.company)).setText(result.company);
        ((TextView)getView().findViewById(R.id.email)).setText(result.email);
    }

    private void processImage(Bitmap result) {
        ((ImageView)getView().findViewById(R.id.avatar)).setImageBitmap(result);
    }

    private void processRepos(Repo[] result) {
        ArrayAdapter<Repo> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, result);
        repoList.setAdapter(adapter);
    }

    private boolean isSubscribed() {
        return userSubscription != null && userSubscription.isUnsubscribed();
    }
}
