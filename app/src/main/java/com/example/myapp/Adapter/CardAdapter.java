package com.example.myapp.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Database.CardDatabase;
import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;
import com.example.myapp.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.TopicViewHolder>{
    private Context mContext;
    private List<Integer> cards;

    public CardAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<Integer>list){
        this.cards = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_card, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Card card = CardDatabase.getInstance(mContext).cardDAO().getCardById(cards.get(position));
        if(card == null) return;
        if(card.getImageCard() != null)
            holder.imgView.setImageBitmap(BitmapFactory.decodeFile(card.getImageCard()));
        else
            holder.imgView.setImageResource(card.getIdImage());
        holder.tvName.setText(card.getNameCard());

    }

    @Override
    public int getItemCount() {
        if(cards != null) return cards.size();
        return 0;
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgView;
        private TextView tvName;
        private TextView tvDescribe;
        public TopicViewHolder(@NonNull View TopicView) {
            super(TopicView);
            imgView = TopicView.findViewById(R.id.imgCard);
            tvName = TopicView.findViewById(R.id.nameCard);
        }
    }
}