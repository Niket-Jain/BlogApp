package com.example.blogapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlogAdapterRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<PostBlog> blog_list;
    private View mView;
    private TextView descriptionView;

    public BlogAdapterRecyclerView(List<PostBlog> blog_list){
        this.blog_list= blog_list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_listview,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }


    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView= itemView;

        }

        public void setDescriptionText (String descriptionText){

            // If thing doesn't load, up try catching this blogDescriptionTextView.
            descriptionView= itemView.findViewById(R.id.blogDescriptionTextView);
            descriptionView.setText(descriptionText);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
