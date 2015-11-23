package eventhorizon.horizonsms;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by marcusmotill on 11/22/15.
 */
public class ConversationActivity extends AppCompatActivity {

    @Bind(R.id.lvMessages)
    ListView lvMessages;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.bSend)
    Button bSend;
    @Bind(R.id.etMessage)
    EditText etMessage;
    ConversationItem conversationItem;
    List<MessageItem> messages;
    Bus bus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);
        ButterKnife.bind(this);
        bus = Application.bus;
        Bundle bundle = getIntent().getExtras();
        conversationItem = new ConversationItem(bundle.getString("name"), null, bundle.getString("address"));
        lvMessages.setDividerHeight(0);
        lvMessages.setDivider(null);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(conversationItem.getContactName());
        toolbar.setTitle(conversationItem.getContactName());
        messages = getAllSmsFromProvider();
        lvMessages.setAdapter(new MessageListAdapter(getApplicationContext(), messages));
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

    @OnClick({R.id.bSend})
    public void onSendButtonClick(View view) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(conversationItem.getAddress(), null, etMessage.getText().toString(), null, null);
        etMessage.getText().clear();
    }

    @Subscribe
    public void onMessageReceivedEvent(MessageRecievedEvent event) {
        messages = getAllSmsFromProvider();
        lvMessages.setAdapter(new MessageListAdapter(getApplicationContext(), messages));
    }

    public List<MessageItem> getAllSmsFromProvider() {
        List<MessageItem> lstSms = new ArrayList<>();

        Cursor c = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE}, // Select body text
                Telephony.Sms.Inbox.ADDRESS + " " + "like " + "'%" + conversationItem.getAddress() + "%'",
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order


        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(new MessageItem(c.getString(0), c.getString(1), false));
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Inbox");
        }
        c.close();

        c = getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Sms.Sent.BODY, Telephony.Sms.Sent.DATE}, // Select body text
                Telephony.Sms.Sent.ADDRESS + " " + "like " + "'%" + conversationItem.getAddress() + "%'",
                null,
                Telephony.Sms.Sent.DEFAULT_SORT_ORDER); // Default sort order


        totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(new MessageItem(c.getString(0), c.getString(1), true));
                c.moveToNext();
            }
        } else {
            throw new RuntimeException("You have no SMS in Outbox");
        }
        c.close();
        Collections.sort(lstSms, new CustomComparator());

        return lstSms;
    }

    public class CustomComparator implements Comparator<MessageItem> {
        @Override
        public int compare(MessageItem o1, MessageItem o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
}
