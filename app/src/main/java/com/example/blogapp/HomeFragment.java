package com.example.blogapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blogListView;

   // private List<PostBlog> blog_list;

    private FirebaseFirestore firebaseFirestore;

   // private BlogAdapterRecyclerView blogRecyclerAdapter;

    private FirestoreRecyclerAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mview =inflater.inflate(R.layout.fragment_home, container, false);

       // blogRecyclerAdapter= new BlogAdapterRecyclerView(blog_list);

       // blog_list= new ArrayList<>();

        blogListView= mview.findViewById(R.id.blogListView);

       //  blogListView.setAdapter(blogRecyclerAdapter);
       // blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseFirestore= FirebaseFirestore.getInstance();
       /* firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){

                        PostBlog postBlog= doc.getDocument().toObject(PostBlog.class);
                        blog_list.add(postBlog);

                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
        */

       // Setting up a query first.

        Query query= firebaseFirestore.collection("Posts");

        // FireStore Recycler Adapter is required.

        FirestoreRecyclerOptions<PostBlog> options = new FirestoreRecyclerOptions.Builder<PostBlog>()
                .setQuery(query,PostBlog.class)
                .build();

         adapter= new FirestoreRecyclerAdapter<PostBlog, BlogViewHolder>(options) {
            @NonNull
            @Override
            public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_listview,parent,false);

                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder holder, int position, @NonNull PostBlog model) {
                holder.description.setText(model.getDescription());
            }
        };

         // Setting adapter to the blogListView to represent the data..

         blogListView.setHasFixedSize(true);
         // blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));
         blogListView.setAdapter(adapter);


        return mview;
    }

    // A ViewHolder class is necessary.

    private class BlogViewHolder extends RecyclerView.ViewHolder{

        private TextView description;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);

            description= itemView.findViewById(R.id.blogDescriptionTextView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
