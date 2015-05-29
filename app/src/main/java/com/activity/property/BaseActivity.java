package com.activity.property;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stander);
        Log.d(getTag(), "--onCreate");
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(getTag(), "--onRetainCustomNonConfigurationInstance");
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public Object getLastCustomNonConfigurationInstance() {
        Log.d(getTag(), "--getLastCustomNonConfigurationInstance");
        return super.getLastCustomNonConfigurationInstance();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();
    }

    public String getTag() {
        return "BaseActivity";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getTag(), "--onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getTag(), "--onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getTag(), "--onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(getTag(), "--onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(getTag(), "--onStart");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(10);
        List<Task> tasks = new ArrayList<>();
        for (ActivityManager.RunningTaskInfo runningTaskInfo : list) {
            Task task = new Task(runningTaskInfo.id, runningTaskInfo.baseActivity.getClassName(), runningTaskInfo.topActivity.getClassName(), runningTaskInfo.numActivities);
            tasks.add(task);
        }
        TaskAdapter adapter = new TaskAdapter(this, R.layout.list_item, tasks);
        ((ListView)findViewById(R.id.list_item)).setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_standard) {
            Intent intent = new Intent(this, StandardActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_single_top) {
            Intent intent = new Intent(this, SingleTopActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_single_task) {
            Intent intent = new Intent(this, SingleTaskActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_single_instance) {
            Intent intent = new Intent(this, SingleInstanceActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_new_task) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_clear_top) {
            Intent intent = new Intent(this, SingleTopActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_single_top) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_brought_to_front) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_reorder_to_front) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_no_history) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_clear_when_task_reset) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_flag_reset_task_if_needed) {
            Intent intent = new Intent(this, StandardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public class TaskAdapter extends ArrayAdapter{

        private int resource;
        public TaskAdapter(Context context, int resource, List<Task> tasks) {
            super(context, resource, tasks);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Task task = (Task) getItem(position);
            holder.idView.setText("id: " + task.id + "");
            holder.baseView.setText("baseActivity: " + task.baseActivity);
            holder.topView.setText("topActivity: " + task.topActivity);
            holder.numberView.setText("number: " + task.number + "");
//            new DownloadImageTask(holder.imageView).execute("http://www.sucaitianxia.com/d/file/20131221/baaed054489f5352139e5077fd8ef4d9.png");
//            if (position == 0) {
//                new DownloadImageTask(holder.imageView).execute("http://www.sucaitianxia.com/d/file/20131221/baaed054489f5352139e5077fd8ef4d9.png");
//            } else {
//                new DownloadImageTask(holder.imageView).execute("https://s-media-cache-ak0.pinimg.com/originals/a9/eb/be/a9ebbe858e0d7906ca149061e89ab1fb.jpg");
//            }
//
//            try {
//                holder.imageView.setImageDrawable(new GifDrawable(getResources(), R.mipmap.anim_flag_england));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            GifImageLoad.with(getContext())
                    .load("https://s-media-cache-ak0.pinimg.com/originals/a9/eb/be/a9ebbe858e0d7906ca149061e89ab1fb.jpg")
                    .placeholder(getResources().getDrawable(R.mipmap.ic_launcher))
                    .error(getResources().getDrawable(R.mipmap.anim_flag_england))
                    .into(holder.imageView);
//            try {
//                GifImageLoad.with(getContext())
//                        .load(new GifDrawable(getResources(), R.raw.anim_flag_hungary))
//                        .placeholder(getResources().getDrawable(R.mipmap.ic_launcher))
//                        .error(getResources().getDrawable(R.mipmap.ic_launcher))
//                        .into(holder.imageView);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            byte[] bytes = Bitmap.
//            Picasso.with(getContext())
//                    .load("https://s-media-cache-ak0.pinimg.com/originals/a9/eb/be/a9ebbe858e0d7906ca149061e89ab1fb.jpg")
//
//                    .transform(new Transformation() {
//                        @Override
//                        public Bitmap transform(Bitmap source) {
////                            GifDrawable gifDrawable = new GifDrawable()
////                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//////                            source.compress(Bitmap.CompressFormat, )
////                            BitmapFactory.Options options = new BitmapFactory.Options();
////                            source.getByteCount()
////                            new GifDrawable(new BitmapDrawable(getResources(), source));
//                            return source;
//                        }
//
//                        @Override
//                        public String key() {
//                            return "aa";
//                        }
//                    })
//                    .into(holder.imageView);

//            final MediaController mc = new MediaController( getContext() );
//            mc.setMediaPlayer( (GifDrawable) holder.imageView.getDrawable() );
//            mc.setAnchorView( holder.imageView );
//            mc.show();
            return convertView;
        }

        public class Holder{
            public TextView idView;
            public TextView baseView;
            public TextView topView;
            public TextView numberView;
            public GifImageView imageView;

            public Holder(View view) {
                idView = (TextView) view.findViewById(R.id.id);
                baseView = (TextView) view.findViewById(R.id.base_activity);
                topView = (TextView) view.findViewById(R.id.top_activity);
                numberView = (TextView) view.findViewById(R.id.number);
                imageView = (GifImageView) view.findViewById(R.id.image);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PictureDialog(getContext()).show("https://s-media-cache-ak0.pinimg.com/originals/a9/eb/be/a9ebbe858e0d7906ca149061e89ab1fb.jpg");
                    }
                });
            }
        }
    }

    public static class Task{
        public int id;
        public String baseActivity;
        public String topActivity;
        public int number;

        public Task(int id, String baseActivity, String topActivity, int number) {
            this.id = id;
            this.baseActivity = baseActivity;
            this.topActivity = topActivity;
            this.number = number;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, GifDrawable> {
        GifImageView bmImage;

        public DownloadImageTask(GifImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected GifDrawable doInBackground(String... urls) {
            String urldisplay = urls[0];
            GifDrawable mIcon11 = null;
            try {
                URL url = new java.net.URL(urldisplay);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(30 * 1000);
                urlConnection.setConnectTimeout(15 * 1000);
                InputStream inputStream = urlConnection.getInputStream();
//                Movie movie = Movie.decodeStream(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }

//                Log.d("movie", movie.duration() + ", " + movie.width() + ", " + movie.height());
//                BufferedInputStream in = new BufferedInputStream(inputStream, 10 * 1024 *1024);
                mIcon11 = new GifDrawable( outputStream.toByteArray() );
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(GifDrawable result) {
            bmImage.setImageDrawable(result);
//            final MediaController mc = new MediaController(BaseActivity.this);
//            mc.setMediaPlayer(result);
//            mc.setAnchorView( bmImage );
//            bmImage.setOnClickListener( new View.OnClickListener()
//            {
//                @Override
//                public void onClick ( View v )
//                {
//                    mc.show();
//                }
//            } );
        }
    }
}
