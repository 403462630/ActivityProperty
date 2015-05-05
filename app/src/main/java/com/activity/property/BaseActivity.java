package com.activity.property;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stander);
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

    public static class TaskAdapter extends ArrayAdapter{

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
            return convertView;
        }

        public static class Holder{
            public TextView idView;
            public TextView baseView;
            public TextView topView;
            public TextView numberView;

            public Holder(View view) {
                idView = (TextView) view.findViewById(R.id.id);
                baseView = (TextView) view.findViewById(R.id.base_activity);
                topView = (TextView) view.findViewById(R.id.top_activity);
                numberView = (TextView) view.findViewById(R.id.number);
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
}
