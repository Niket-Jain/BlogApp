package com.example.blogapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import Adapters.HomeAdapter;


public class HomeFragment extends Fragment {

    // Init.
    private RecyclerView blogListView;
    private List<PostBlog> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference ref;
    private HomeAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView =inflater.inflate(R.layout.fragment_home, container, false);

        // Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        ref = firebaseFirestore.collection("Posts");

        blog_list = new ArrayList<>();

        // Init RecyclerView
        blogListView = mView.findViewById(R.id.blogListView);
        blogListView.setLayoutManager(new LinearLayoutManager(getContext()));
        blogListView.setHasFixedSize(false);

        // Calling Functions.
        gettingData();

        adapter = new HomeAdapter(blog_list,getContext());
        blogListView.setAdapter(adapter);

        return mView;
    }

    private void gettingData() {

        Query query = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                    PostBlog post = snapshot.toObject(PostBlog.class);
                    blog_list.add(post);
                }

                adapter.notifyDataSetChanged();
            }
        });

    }
}
