package com.activity.property;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 * Created by rjhy on 15-5-29.
 */
public class GifImageLoad {

    private Context context;
    private static GifImageLoad singleton;
    private LruCache<String, byte[]> cache;

    public static GifImageLoad with(Context context) {
        if (singleton == null) {
            synchronized (GifImageLoad.class) {
                if (singleton == null) {
                    singleton = new GifImageLoad(context.getApplicationContext());
                }
            }
        }
        return singleton;
    }

    private GifImageLoad(final Context context) {
        this.context = context;
        cache = new LruCache<String, byte[]>(calculateMemoryCacheSize(context)) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length;
            }
        };
    }

    public Request load(String url) {
        return new Request(url);
    }

    public Request load(GifDrawable gifDrawable) {
        return new Request(gifDrawable);
    }

    int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();

        if (largeHeap && SDK_INT >= HONEYCOMB) {
            memoryClass = am.getLargeMemoryClass();
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 8;
    }

    public static class Request{
        private GifTarget target;
        private ImageView view;
        private GifDrawable gifDrawable;
        private String url;

        Request(String url) {
            this.url = url;
            target = new GifTarget();
        }

        Request(GifDrawable gifDrawable) {
            this.gifDrawable = gifDrawable;
            target = new GifTarget();
        }

        public void into(ImageView imageView) {
            target.setView(imageView);
            if (gifDrawable != null) {
                target.onGifLoaded(gifDrawable);
                return ;
            }
            if (url == null) {
                target.onGifFailed();
                return ;
            }
            if (singleton.cache.get(url) != null) {
                try {
                    target.onGifLoaded(new GifDrawable(singleton.cache.get(url)));
                } catch (IOException e) {
                    e.printStackTrace();
                    target.onGifFailed();
                }
            } else {
                target.onPrepareLoad();
                new DownLoadImageTask(target).execute(url);
            }
        }

        public Request placeholder(Drawable placeholderDrawable) {
            if (placeholderDrawable == null) {
                throw new IllegalArgumentException("Error image may not be null.");
            }
            target.setPlaceholderDrawable(placeholderDrawable);
            return this;
        }

        public Request error(Drawable errorDrawable) {
            if (errorDrawable == null) {
                throw new IllegalArgumentException("Error image may not be null.");
            }
            target.setErrorDrawable(errorDrawable);
            return this;
        }

        public static class GifTarget{
            private ImageView view;
            private Drawable placeholderDrawable;
            private Drawable errorDrawable;
            public GifTarget() {
            }

            final void setView(ImageView view) {
                this.view = view;
            }

            final void setPlaceholderDrawable(Drawable placeholderDrawable) {
                this.placeholderDrawable = placeholderDrawable;
            }

            final void setErrorDrawable(Drawable errorDrawable) {
                this.errorDrawable = errorDrawable;
            }

            public void onGifLoaded(GifDrawable data) {
                view.setImageDrawable(data);
            }

            public void onGifFailed() {
                if (errorDrawable != null) {
                    view.setImageDrawable(errorDrawable);
                }
            }

            public void onPrepareLoad() {
                if (placeholderDrawable != null) {
                    view.setImageDrawable(placeholderDrawable);
                }
            }
        }
    }

    private static class DownLoadImageTask extends AsyncTask<String, Void, byte[]> {
        private Request.GifTarget gifTarget;
        private String urlStr;

        public DownLoadImageTask(Request.GifTarget gifTarget) {
            this.gifTarget = gifTarget;
        }

        protected byte[] doInBackground(String... urls) {
            byte[] result = new byte[0];
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            HttpURLConnection urlConnection = null;
            try {
                urlStr = urls[0];
                URL url = new URL(urlStr);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10 * 1000);
                urlConnection.setConnectTimeout(10 * 1000);
                inputStream = urlConnection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
                result = outputStream.toByteArray();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        protected void onPostExecute(byte[] result) {
            try {
                if (result != null && result.length > 0) {
                    singleton.cache.put(urlStr, result);
                    gifTarget.onGifLoaded(new GifDrawable(result));
                } else {
                    gifTarget.onGifFailed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                gifTarget.onGifFailed();
            }
        }
    }
}
