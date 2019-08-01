package com.adhityakuncoro.wordpress.squidscan.Adapter;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.adhityakuncoro.wordpress.squidscan.R;
import java.util.List;

public class AdapterViewHolder extends RecyclerView.Adapter<AdapterViewHolder.ViewHolder> {
    List<Listitem> listItems;
    Context context;

    public AdapterViewHolder(List<Listitem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Listitem listItem = listItems.get(position);
        holder.textView_Id.setText(String.valueOf(listItem.get_Id()));
        holder.textViewCode.setText(listItem.getCode());
        holder.textViewType.setText(listItem.getType());
        Linkify.addLinks(holder.textViewCode, Linkify.ALL);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView_Id;
        public TextView textViewCode;
        public TextView textViewType;

        public TextView getTextView_Id() {
            return textView_Id;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            textView_Id = (TextView)itemView.findViewById(R.id.textView_Id);
            textViewCode = (TextView)itemView.findViewById(R.id.textViewCode);
            textViewType = (TextView)itemView.findViewById(R.id.textViewType);
        }
    }
}

