package uk.gov.boss.rulers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
//
import android.support.v4.view.MenuItemCompat;
//
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView mainListView;
    ArrayList mNameList = new ArrayList();
    ShareActionProvider mShareActionProvider;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    String jsonRaw;

    public class customListItem {
        String title;
        String description;
        String piccie;
    }
    customListItem CustomListItem;
    tomsCustomAdapter mArrayAdapter;
    String selectedView = "england";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        jsonRaw = readJsonRulers();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomListItem = new customListItem();
        // Access the ListView
        mainListView = (ListView) findViewById(R.id.main_listview);
        //Log.w("point a: ", selectedView);
        if (savedInstanceState != null) {
            selectedView = savedInstanceState.getString("myView");
            if (selectedView != null) {
                //default England
                mArrayAdapter = new tomsCustomAdapter(selectedView);
                mainListView.setAdapter(mArrayAdapter);
            }
        }
        else {
            //default England
            mArrayAdapter = new tomsCustomAdapter("england");
            mainListView.setAdapter(mArrayAdapter);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Sharing
        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        // Access the object responsible for putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }
        // Create an Intent to share your content
        setShareIntent();
        return super.onCreateOptionsMenu(menu);
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {
            // create an Intent with the contents of the TextView
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The Rulers app is quite nice - https://play.google.com/store/apps/details?id=uk.gov.boss.rulers");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "The Rulers app is reasonably good - https://play.google.com/store/apps/details?id=uk.gov.boss.rulers");
            // Make sure the provider knows it should work with that Intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_england:
                openEngland();
                return true;
            case R.id.action_france:
                openFrance();
                return true;
            case R.id.action_usa:
                openUsa();
                return true;
            case R.id.action_rome:
                openRome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openUsa() {
        selectedView = "usa";
        mArrayAdapter = new tomsCustomAdapter(selectedView);
        mainListView.setAdapter(mArrayAdapter);
    }
    private void openFrance() {
        selectedView = "france";
        mArrayAdapter = new tomsCustomAdapter(selectedView);
        mainListView.setAdapter(mArrayAdapter);
    }
    private void openEngland() {
        selectedView = "england";
        mArrayAdapter = new tomsCustomAdapter(selectedView);
        mainListView.setAdapter(mArrayAdapter);
    }
    private void openRome() {
        selectedView = "rome";
        mArrayAdapter = new tomsCustomAdapter(selectedView);
        mainListView.setAdapter(mArrayAdapter);
    }
    public void onSaveInstanceState(Bundle savedState) {
        //persists view when device is rotated
        super.onSaveInstanceState(savedState);
        savedState.putString("myView", selectedView);
    }
    public class tomsCustomAdapter extends BaseAdapter {
        //See http://www.codelearn.org/android-tutorial/android-listview
        List<customListItem> customList;

        public tomsCustomAdapter() {
            //no parameters, default england
            customList = getDataForListView("england");
        }

        public tomsCustomAdapter(String filter) {
            //with parameters
            customList = getDataForListView(filter);
        }

        public List<customListItem> getDataForListView(String filter) {
            //String country
            List<customListItem> itemsList = new ArrayList<customListItem>();
            try {
                JSONObject obj = new JSONObject(jsonRaw);

                JSONArray m_jArray = obj.getJSONArray(filter);
                for (int i = 0; i < m_jArray.length(); i++) {
                    JSONObject jo_inside = m_jArray.getJSONObject(i);
                    customListItem item = new customListItem();
                    item.title = jo_inside.getString("startYear") + " " + jo_inside.getString("ruler");
                    item.description = jo_inside.getString("description");
                    item.piccie = jo_inside.getString("icon");
                    itemsList.add(item);
                }
            }
            catch(JSONException je){
                je.printStackTrace();
            }
            return itemsList;
        }

        @Override
        public int getCount() {
            return customList.size();
        }
        @Override
        public Object getItem(int position) {
            return customList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            if (arg1 == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.listitem, arg2, false);
            }
            TextView itemTitle = (TextView) arg1.findViewById(R.id.textView1);
            TextView itemDescription = (TextView) arg1.findViewById(R.id.textView2);
            ImageView itemIcon = (ImageView) arg1.findViewById(R.id.imageView1);
            customListItem item = customList.get(arg0);
            itemTitle.setText(item.title);
            itemDescription.setText(item.description);
            String itemIconLabel = item.piccie;
            int id;
            switch (itemIconLabel) {
                case "crown":
                    id = getResources().getIdentifier("crown", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "bloodycrown":
                    id = getResources().getIdentifier("bloodycrown", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "puritan":
                    id = getResources().getIdentifier("puritan", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "president":
                    id = getResources().getIdentifier("president", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "frenchFlag":
                    id = getResources().getIdentifier("frenchflag", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "unionjack":
                    id = getResources().getIdentifier("unionjack", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "frenchRoyalists":
                    id = getResources().getIdentifier("frenchroyalistflag", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "swords":
                    id = getResources().getIdentifier("swords", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "presidentdead":
                    id = getResources().getIdentifier("presidentdead", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "presidentsurvived":
                    id = getResources().getIdentifier("presidentsurvived", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "frenchpresident":
                    id = getResources().getIdentifier("frenchpresident", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "jester":
                    id = getResources().getIdentifier("jester", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "Emperor":
                    id = getResources().getIdentifier("laurels", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "Emperor_blood":
                    id = getResources().getIdentifier("laurelsblood", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "Shared_Empire":
                    id = getResources().getIdentifier("laurels2", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                case "Shared_Empire_blood":
                    id = getResources().getIdentifier("laurels2blood", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
                    break;
                default:
                    id = getResources().getIdentifier("crown", "drawable", getPackageName());
                    itemIcon.setImageResource(id);
            }
            return arg1;
        }
        public customListItem getThing(int position) {
            return customList.get(position);
        }
    }
    public String readJsonRulers() {
        InputStream is = this.getResources().openRawResource(R.raw.rulers);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException ue){
            ue.printStackTrace();
        } catch (IOException ie){
            ie.printStackTrace();
        }
        finally {
            closeQuietly(is);
        }
        String jsonString = writer.toString();
        Log.w("jsonString: ", jsonString);
        return jsonString;
    }
    protected void closeQuietly( InputStream is ) {
        try {
            if (is != null) {
                is.close();
            }
        } catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
}


