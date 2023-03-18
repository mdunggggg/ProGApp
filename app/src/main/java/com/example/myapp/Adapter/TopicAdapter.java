package com.example.myapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.Model.Topic;
import com.example.myapp.R;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    private Context mContext;
    private List<Topic> topics;

    public TopicAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<Topic>list){
        this.topics = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        if(topic == null) return;
        if(topic.getImageTopic() != null)
            holder.imgView.setImageBitmap(BitmapFactory.decodeFile(topic.getImageTopic()));
        else
            holder.imgView.setImageResource(topic.getIdImage());
        holder.tvName.setText(topic.getNameTopic());
        holder.tvDescribe.setText(topic.getDescribeTopic());
    }

    @Override
    public int getItemCount() {
        if(topics != null) return topics.size();
        return 0;
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgView;
        private TextView tvName;
        private TextView tvDescribe;
        public TopicViewHolder(@NonNull View TopicView) {
            super(TopicView);
            imgView = TopicView.findViewById(R.id.imageTopic);
            tvName = TopicView.findViewById(R.id.nameTopic);
            tvDescribe = TopicView.findViewById(R.id.describeTopic);
        }
    }
}
