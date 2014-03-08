package cz.boris.demo.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

import cz.boris.demo.model.Repo;
import cz.boris.demo.model.User;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

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

    public static User user(String name) {
        User user = userService.getUser(name);
        return user;
    }

    public static Repo[] repos(String user) {
        return userService.getRepos(user);
    }

    public static Bitmap bitmapImage(String url) {
        Bitmap bitmap = null;
        try {
            URL avatar = new URL(url);
            bitmap = BitmapFactory.decodeStream(avatar.openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
