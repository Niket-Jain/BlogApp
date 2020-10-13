package Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blogapp.PostBlog;
import com.example.blogapp.R;
import com.example.blogapp.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    // For Const
    public List<PostBlog> blog_list;
    public Context context;

    public HomeAdapter(List<PostBlog> blog_list, Context context) {
        this.blog_list = blog_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // To inflate the layouts.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_listview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        PostBlog post = blog_list.get(position);

        holder.description.setText(post.getDescription());

        holder.date.setText(post.getTimestamp());

        // To set Image.
        Glide.with(context).load(post.getImage_URL()).into(holder.post);

        // To load the personal Info.
        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    ArrayList<Users> users = (ArrayList<Users>) queryDocumentSnapshots.toObjects(Users.class);
                    final Users user = users.get(position);

                    String name = user.getName();
                    String image = user.getImage();

                    Glide.with(context).load(image).into(holder.dp);
                    holder.username.setText(name);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        // Init.
        public CircleImageView dp;
        public TextView date,username,description;
        public ImageView post;
        public CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dpCircleImageView);
            date = itemView.findViewById(R.id.dateTextView);
            username = itemView.findViewById(R.id.UserNameTextView);
            description = itemView.findViewById(R.id.blogDescriptionTextView);
            post= itemView.findViewById(R.id.blogPostImageView);
            cardView = itemView.findViewById(R.id.blogCardView);
        }
    }
}
