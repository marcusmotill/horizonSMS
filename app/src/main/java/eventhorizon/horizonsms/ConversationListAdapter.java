package eventhorizon.horizonsms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by marcusmotill on 10/7/15.
 */
public class ConversationListAdapter extends ArrayAdapter {


    public ConversationListAdapter(Context context, List messages) {
        super(context, R.layout.conversation_list_item, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ConversationItem item = (ConversationItem) getItem(position);
        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();

        convertView = mInflater.inflate(R.layout.conversation_list_item, null);
        convertView.setTag(holder);

        holder.tvContactName = (TextView) convertView.findViewById(R.id.tvContactName);
        holder.tvPreviousMessage = (TextView) convertView.findViewById(R.id.tvPreviousMessage);

        holder.tvContactName.setText(item.getContactName());
        holder.tvPreviousMessage.setText(item.getPreviousMessage());

        return convertView;
    }

    private class ViewHolder {
        TextView tvContactName;
        TextView tvPreviousMessage;
    }
}