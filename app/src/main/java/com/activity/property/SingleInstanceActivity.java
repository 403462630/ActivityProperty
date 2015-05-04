package com.activity.property;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SingleInstanceActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_instance);
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
