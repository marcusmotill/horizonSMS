package eventhorizon.horizonsms;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by marcusmotill on 10/7/15.
 */
public class MessageListAdapter extends ArrayAdapter {


    public MessageListAdapter(Context context, List messages) {
        super(context, R.layout.message_list_item_recieved, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        MessageItem item = (MessageItem) getItem(position);
        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (item.isSent()) {
            convertView = mInflater.inflate(R.layout.message_list_item_sent, null);
        } else {
            convertView = mInflater.inflate(R.layout.message_list_item_recieved, null);
        }
        convertView.setTag(holder);

        holder.tvMessageBody = (TextView) convertView.findViewById(R.id.tvMessageBody);
        holder.frameLayout = (RelativeLayout) convertView.findViewById(R.id.frameLayout);
        shouldHide(position, holder.frameLayout);
        holder.tvMessageBody.setText(item.getMessageBody());
        Linkify.addLinks(holder.tvMessageBody, Linkify.ALL);
        return convertView;
    }

    private void shouldHide(int position, View view) {
        if(position != 0){
            if(((MessageItem)getItem(position-1)).isSent() && ((MessageItem)getItem(position)).isSent()){
                view.setVisibility(View.INVISIBLE);
            }

            if(!((MessageItem)getItem(position-1)).isSent() && !((MessageItem)getItem(position)).isSent()){
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class ViewHolder {
        TextView tvMessageBody;
        RelativeLayout frameLayout;
    }
}