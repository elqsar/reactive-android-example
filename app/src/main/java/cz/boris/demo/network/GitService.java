package cz.boris.demo.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.net.URL;

import cz.boris.demo.model.Repo;
import cz.boris.demo.model.User;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Boris Musatov on 6.3.14.
 */
public class GitService {

    private static UserService userService;

    interface UserService {
        @GET("/users/{user}")
        public User getUser(@Path("user") String user);
        @GET("/users/{user}/repos")
        public Repo[] getRepos(@Path("user") String user);
    }

    static {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://api.github.com").build();
        userService = restAdapter.create(UserService.class);
    }

    public static Observable<User> user(String name) {
        Log.d("User service called: ", "user()");
        return Observable.create((Subscriber<? super User> subscriber) -> {
            try {
                User user = userService.getUser(name);
                subscriber.onNext(user);
                subscriber.onCompleted();
                // handle error here
            } catch (Exception e) {
                subscriber.onError(e);
                e.printStackTrace();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Repo[]> repos(String user) {
        Log.d("User service called: ", "repos()");
        return Observable.create((Subscriber<? super Repo[]> subscriber) -> {
            Repo[] repos = userService.getRepos(user);
            subscriber.onNext(repos);
            subscriber.onCompleted();
            // handle error here
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Bitmap> bitmapImage(String url) {
        Log.d("User service called: ", "bitmapImage()");
        return Observable.create((Subscriber<? super Bitmap> subscriber) -> {
            try {
                URL avatarUrl = new URL(url);
                subscriber.onNext(BitmapFactory.decodeStream(avatarUrl.openStream()));
                subscriber.onCompleted();
            } catch (Exception e) {

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Repo[] dummy() {
        return new Repo[0];
    }
}
