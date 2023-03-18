package com.example.myapp.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Model.Card;
import com.example.myapp.Model.Topic;
import com.example.myapp.R;

import java.util.List;
import java.util.Locale;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.TopicViewHolder>{
    private Context mContext;
    private List<Card> cardsBar;
    private TextToSpeech textToSpeech;

    public BarAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<Card>cards){
        this.cardsBar = cards;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bar, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Card card = cardsBar.get(position);
        if(card == null) return;
        if(card.getImageCard() != null)
            holder.imgView.setImageBitmap(BitmapFactory.decodeFile(card.getImageCard()));
        else
            holder.imgView.setImageResource(card.getIdImage());
        holder.tvName.setText(card.getNameCard());
      //  holder.tvDescribe.setText(topic.getDescribeTopic());
    }

    @Override
    public int getItemCount() {
        if(cardsBar != null) return cardsBar.size();
        return 0;
    }
    public void add(Card card){
        cardsBar.add(card);
        notifyDataSetChanged();
    }
    public void eraseBar(){
        cardsBar.remove(cardsBar.size()-1);
        notifyDataSetChanged();
    }
    public void speakBar(){
        for(int i = 0 ; i < cardsBar.size(); i++){
            textToSpeech.speak(cardsBar.get(i).getNameCard(), TextToSpeech.QUEUE_ADD, null, null);
        }
    }
    public void setCardsBar(List<Card> cardsBar) {
        this.cardsBar = cardsBar;
        notifyDataSetChanged();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgView;
        private TextView tvName;
        public TopicViewHolder(@NonNull View TopicView) {
            super(TopicView);
            imgView = TopicView.findViewById(R.id.imgCardBar);
            tvName = TopicView.findViewById(R.id.nameCardBar);
            textToSpeech = new TextToSpeech(mContext.getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(Locale.ENGLISH);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            //Toast.makeText(PlayActivity.this, "This language is not supported", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(PlayActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
           // tvDescribe = TopicView.findViewById(R.id.describeTopic);
        }
    }
}
