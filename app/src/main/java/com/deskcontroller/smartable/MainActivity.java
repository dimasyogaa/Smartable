package com.deskcontroller.smartable;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.graphics.Color.parseColor;


public class MainActivity extends AppCompatActivity {
    public static final String DEVICE_EXTRA = "com.example.lightcontrol.SOCKET";
    public static final String DEVICE_UUID = "com.example.lightcontrol.uuid";
    public static final String BUFFER_SIZE = "com.example.lightcontrol.buffersize";
    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    private static final int SETTINGS = 20;
    private static final String DEVICE_LIST = "com.example.lightcontrol.devicelist";
    private static final String DEVICE_LIST_SELECTED = "com.example.lightcontrol.devicelistselected";
    private static final String TAG = "BlueTest5-MainActivity";
    private ImageButton btn_search, btn_connect;
    private LinearLayout connect, listviewLayout;
    private TextView tvTurnOnBt, title, tv_search, again, select;
    private ImageView turn, turnBt, ivAbout;
    private Animation btt, ttb;
    private ListView listView;
    private BluetoothAdapter mBTAdapter;
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mBufferSize = 50000; //Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rev);


        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_connect = (ImageButton) findViewById(R.id.btn_connect);
        connect = (LinearLayout) findViewById(R.id.connect);
        again = (TextView) findViewById(R.id.again);
        tv_search = (TextView) findViewById(R.id.tv_search);
        turn = (ImageView) findViewById(R.id.turn);
        turnBt = (ImageView) findViewById(R.id.turn_bt);
        ivAbout = (ImageView) findViewById(R.id.about);
        select = (TextView) findViewById(R.id.select);
        tvTurnOnBt = (TextView) findViewById(R.id.tv_turnon_bt);
        listView = (ListView) findViewById(R.id.list_item);
        listviewLayout = (LinearLayout) findViewById(R.id.listview_layout);
        btt = AnimationUtils.loadAnimation(this, R.anim.btt);
        ttb = AnimationUtils.loadAnimation(this, R.anim.ttb);
        connect.animate().alpha(0).translationY(100).setDuration(1).start();
        btn_connect.setEnabled(false);
        btn_search.animate().setDuration(1).start();
        listviewLayout.animate().alpha(0).translationX(-400).setDuration(1).start();
        again.animate().alpha(0).translationY(50).setDuration(1).start();
        tvTurnOnBt.animate().alpha(0).translationY(50).setDuration(1).start();
        turnBt.animate().alpha(0).rotationX(90).setDuration(1).start();
        select.animate().alpha(0).translationY(20).setDuration(1).start();
        turn.animate().alpha(0).rotationX(90).setDuration(1).start();
        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
            if (list != null) {
                initList(list);
                MyAdapter adapter = (MyAdapter) listView.getAdapter();
                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    btn_connect.setEnabled(true);
                }
            } else {
                initList(new ArrayList<BluetoothDevice>());
            }

        } else {
            initList(new ArrayList<BluetoothDevice>());
        }
        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mBTAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBTAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
                } else if (!mBTAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);
                } else {
                    new SearchDevices().execute();
                    btn_search.setColorFilter(Color.parseColor("#281F1B"));
                    btn_search.animate().scaleX((float) 1.2).scaleY((float) 1.2).setDuration(800).start();
                    tv_search.setTextColor(Color.parseColor("#281F1B"));
                    connect.animate().alpha(1).translationY(0).setDuration(800).start();
                    listviewLayout.animate().alpha(1).translationX(0).setDuration(800).start();
                    select.animate().alpha(1).translationY(0).setDuration(2000).start();
                }
            }
        });

        btn_connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                BluetoothDevice device = ((MyAdapter) (listView.getAdapter())).getSelectedItem();
                Intent intent = new Intent(getApplicationContext(), Controlling.class);
                intent.putExtra(DEVICE_EXTRA, device);
                intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
                intent.putExtra(BUFFER_SIZE, mBufferSize);
                startActivity(intent);
            }
        });


        ivAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoabout = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(gotoabout);
            }
        });


    }

    protected void onPause() {
// TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    msg("Bluetooth Enabled successfully");
                    again.animate().alpha(1).translationY(0).setDuration(800).start();
                    turn.animate().alpha(1).rotationX(0).setDuration(1000).start();
                    tvTurnOnBt.animate().alpha(0).start();
                    turnBt.animate().alpha(0).start();
                    new SearchDevices().execute();
                } else {
                    msg("Bluetooth couldn't be enabled");
                    again.animate().alpha(0).start();
                    turn.animate().alpha(0).start();
                    tvTurnOnBt.animate().alpha(1).translationY(0).setDuration(800).start();
                    turnBt.animate().alpha(1).rotationX(0).setDuration(1000).start();
                }

                break;
            case SETTINGS: //If the settings have been updated
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String uuid = prefs.getString("prefUuid", "Null");
                mDeviceUUID = UUID.fromString(uuid);
                Log.d(TAG, "UUID: " + uuid);
                String bufSize = prefs.getString("prefTextBuffer", "Null");
                mBufferSize = Integer.parseInt(bufSize);

                String orientation = prefs.getString("prefOrientation", "Null");
                Log.d(TAG, "Orientation: " + orientation);
                if (orientation.equals("Landscape")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation.equals("Portrait")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (orientation.equals("Auto")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Quick way to call the Toast
     *
     * @param str
     */
    private void msg(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize the List adapter
     *
     * @param objects
     */
    private void initList(List<BluetoothDevice> objects) {
        final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_item, R.id.lstContent, objects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                btn_connect.setEnabled(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.homescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivityForResult(intent, SETTINGS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
     * will show up with this. I didn't see any need to re-build the wheel over here
     *
     * @author ryder
     */
    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                listDevices.add(device);
            }
            return listDevices;

        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                MyAdapter adapter = (MyAdapter) listView.getAdapter();
                adapter.replaceItems(listDevices);
            } else {
                msg("No paired devices found, please pair your serial BT device and try again");
            }
        }

    }

    /**
     * Custom adapter to show the current devices in the list. This is a bit of an overkill for this
     * project, but I figured it would be good learning
     * Most of the code is lifted from somewhere but I can't find the link anymore
     *
     * @author ryder
     */
    private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
        private int selectedIndex;
        private Context context;
        private int selectedColor = parseColor("#EFCFAB");
        private List<BluetoothDevice> myList;

        public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(ctx, resource, textViewResourceId, objects);
            context = ctx;
            myList = objects;
            selectedIndex = -1;
        }

        public void setSelectedIndex(int position) {
            selectedIndex = position;
            notifyDataSetChanged();
        }

        public BluetoothDevice getSelectedItem() {
            return myList.get(selectedIndex);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void replaceItems(List<BluetoothDevice> list) {
            myList = list;
            notifyDataSetChanged();
        }

        public List<BluetoothDevice> getEntireList() {
            return myList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;
            if (convertView == null) {
                vi = LayoutInflater.from(context).inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.tv = (TextView) vi.findViewById(R.id.lstContent);

                vi.setTag(holder);

            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                holder.tv.setBackgroundColor(selectedColor);
                again.animate().alpha(0).translationY(50).setDuration(800).start();
                turn.animate().alpha(0).rotationX(90).setDuration(800).start();
                tvTurnOnBt.animate().alpha(0).translationY(50).setDuration(800).start();
                turnBt.animate().alpha(0).rotationX(90).setDuration(800).start();
                btn_connect.setEnabled(true);

            } else {
                holder.tv.setBackgroundColor(Color.WHITE);
                btn_connect.setEnabled(false);


            }
            BluetoothDevice device = myList.get(position);
            holder.tv.setText(device.getName() + "\n " + device.getAddress());

            return vi;
        }

        private class ViewHolder {
            TextView tv;
        }

    }

}