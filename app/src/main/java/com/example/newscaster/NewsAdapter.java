package com.example.newscaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;

    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.titleTextView.setText(news.getTitle());
        holder.dateTextView.setText(news.getDate());
        holder.timeTextView.setText(news.getTime());
        holder.locationTextView.setText(news.getLocation());
        holder.articleTextView.setText(news.getArticle());

        // Use Glide to load the image, with a placeholder for null or empty imagePath
        Glide.with(context)
                .load(news.getImageUrl())
                .into(holder.imageView);

        // Set initial state for "Read More" functionality
        holder.articleTextView.setMaxLines(3);
        holder.readMoreTextView.setText("Read more");

        holder.readMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.articleTextView.getMaxLines() == 3) {
                    holder.articleTextView.setMaxLines(Integer.MAX_VALUE);
                    holder.readMoreTextView.setText("Read less");
                } else {
                    holder.articleTextView.setMaxLines(3);
                    holder.readMoreTextView.setText("Read more");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    // Method to update the adapter's data
    public void updateList(List<News> newList) {
        this.newsList = newList;
        notifyDataSetChanged();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        TextView dateTextView;
        TextView timeTextView;
        TextView locationTextView;
        TextView articleTextView;
        TextView readMoreTextView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.news_title);
            imageView = itemView.findViewById(R.id.news_image);
            dateTextView = itemView.findViewById(R.id.news_date);
            timeTextView = itemView.findViewById(R.id.news_time);
            locationTextView = itemView.findViewById(R.id.news_location);
            articleTextView = itemView.findViewById(R.id.news_article);
            readMoreTextView = itemView.findViewById(R.id.read_more);
        }
    }
}
