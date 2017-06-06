package com.fantasy.league.fantasyleague.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasy.league.fantasyleague.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharad on 10-May-17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> itemList;
    private String  thisUser;
    private final int VIEW_TYPE_MY_FIRST = 0;
    private final int VIEW_TYPE_MY_NEXT  = 1;
    private final int VIEW_TYPE_THEIR_FIRST = 2;
    private final int VIEW_TYPE_THEIR_NEXT  = 3;

    public ChatRecyclerAdapter(String thisUser) {
        this.itemList = new ArrayList<>();
        this.thisUser = thisUser;
    }

    public void add(Message msg) {
        this.itemList.add(msg);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RecyclerView.ViewHolder rcv = null;
        switch (viewType) {
            case VIEW_TYPE_MY_FIRST:
            case VIEW_TYPE_MY_NEXT:
                rcv = new MyMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_card_mine, parent, false));
                break;
            case VIEW_TYPE_THEIR_FIRST:
            case VIEW_TYPE_THEIR_NEXT:
                rcv = new TheirMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_card_othr, parent, false));
                break;
        }

        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MY_FIRST:
            case VIEW_TYPE_MY_NEXT: {
                MyMessageViewHolder holder = (MyMessageViewHolder) viewHolder;
                holder.message.setText(this.itemList.get(position).msg);
                holder.timestamp.setText(this.itemList.get(position).getTimestamp());
            }
            break;
            case VIEW_TYPE_THEIR_FIRST:
            case VIEW_TYPE_THEIR_NEXT: {
                TheirMessageViewHolder holder = (TheirMessageViewHolder) viewHolder;
                holder.name.setTextColor(this.itemList.get(position).color);
                holder.name.setText(this.itemList.get(position).name);
                holder.message.setText(this.itemList.get(position).msg);
                holder.timestamp.setText(this.itemList.get(position).getTimestamp());

                holder.name.setVisibility((viewHolder.getItemViewType() == VIEW_TYPE_THEIR_FIRST) ? View.VISIBLE : View.GONE);
            }
            break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = this.itemList.get(position).sid.equals(thisUser) ? VIEW_TYPE_MY_FIRST : VIEW_TYPE_THEIR_FIRST;
        if(position > 0) {
            type += this.itemList.get(position).sid.equals(this.itemList.get(position-1).sid) ? 1 : 0;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void clear() {
        this.itemList.clear();
    }

    public List<Message> getItemList() {
        return itemList;
    }

    public int getScrollTo() {
        return (itemList.size() > 0) ? (itemList.size() - 1) : 0;
    }

    private class MyMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public TextView timestamp;

        public MyMessageViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.messageView);
            timestamp = (TextView) itemView.findViewById(R.id.timeView);
        }
    }

    private class TheirMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public TextView timestamp;

        public TheirMessageViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.messageName);
            message = (TextView) itemView.findViewById(R.id.messageView);
            timestamp = (TextView) itemView.findViewById(R.id.timeView);
        }
    }
}
