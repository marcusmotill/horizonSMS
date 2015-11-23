package eventhorizon.horizonsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.lvMessages)
    ListView lvMessages;

    final int READ_SMS_PERMISSION = 1;
    List<String> permissionsNeeded;
    List<ConversationItem> conversations;
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bus = Application.bus;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvMessages.setDividerHeight(0);
        lvMessages.setDivider(null);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                Bundle b = new Bundle();
                b.putString("name", conversations.get(position).getContactName());
                b.putString("address", conversations.get(position).getAddress());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        checkPermissions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public void checkPermissions() {

        permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    READ_SMS_PERMISSION);
        } else {
            conversations = getAllSmsFromProvider();
            lvMessages.setAdapter(new ConversationListAdapter(getApplicationContext(), conversations));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == permissionsNeeded.size()) {
            conversations = getAllSmsFromProvider();
            lvMessages.setAdapter(new ConversationListAdapter(getApplicationContext(), conversations));
        }
        /*switch (requestCode) {
            case READ_SMS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case READ_CONTACTS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }*/
    }

    public List<ConversationItem> getAllSmsFromProvider() {
        List<ConversationItem> lstSms = new ArrayList<>();

        /*Cursor c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY}, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order*/

        Cursor c = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[]{"DISTINCT" + " " + Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY}, //DISTINCT
                Telephony.Sms.Inbox.ADDRESS + " " + "IS NOT NULL) GROUP BY (" + Telephony.Sms.Inbox.ADDRESS, //GROUP BY
                null, null);

        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(new ConversationItem(getContactName(c.getString(0)), c.getString(1), c.getString(0)));
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        return new ArrayList<>(new LinkedHashSet<>(lstSms));
    }

    public String getContactName(String phoneNumber) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        } else {
            contactName = phoneNumber;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    @Subscribe
    public void onMessageReceivedEvent(MessageRecievedEvent event) {
        conversations = getAllSmsFromProvider();
        lvMessages.setAdapter(new ConversationListAdapter(getApplicationContext(), conversations));
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, MyPreferenceActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
