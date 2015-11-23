package eventhorizon.horizonsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.squareup.otto.Bus;

/**
 * Created by marcusmotill on 11/22/15.
 */
public class SmsListener extends BroadcastReceiver {

    Bus bus;

    @Override
    public void onReceive(Context context, Intent intent) {
        bus = Application.bus;
        bus.register(this);
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                bus.post(new MessageRecievedEvent());
            }
        }
        bus.unregister(this);
    }

}