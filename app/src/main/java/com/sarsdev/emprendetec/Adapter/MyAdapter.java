package com.sarsdev.emprendetec.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.sarsdev.emprendetec.Custom.Detalles;
import com.sarsdev.emprendetec.Model.DataClass;
import com.sarsdev.emprendetec.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final Context context;
    private final List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proyect, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(dataList.get(position).getDataImage())
                .transform(new CenterCrop(), new RoundedCorners(16))
                .into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        String fullDesc = dataList.get(position).getDataDesc();
        int maxWords = 5;

        String[] words = fullDesc.split("\\s+");
        StringBuilder truncatedDesc = new StringBuilder();

        for (int i = 0; i < Math.min(maxWords, words.length); i++) {
            truncatedDesc.append(words[i]).append(" ");
        }
        holder.recDesc.setText(truncatedDesc.toString().trim());
        holder.recArea.setText(dataList.get(position).getDataArea());
        holder.recArea2.setText(dataList.get(position).getDataArea());
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Detalles.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Category", dataList.get(holder.getAdapterPosition()).getDataArea());
                intent.putExtra("Requirements", dataList.get(holder.getAdapterPosition()).getDataArea2());

                Pair<View, String> imagePair = Pair.create((View) holder.recImage, "projectImageTN");
                Pair<View, String> titlePair = Pair.create((View) holder.recImage, "projectTitleTN");
                Pair<View, String> catPair = Pair.create((View) holder.recImage, "projectCatTN");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, imagePair, titlePair, catPair);

                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recDesc, recArea, recArea2;
    ConstraintLayout recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recArea = itemView.findViewById(R.id.cat);
        recDesc = itemView.findViewById(R.id.desc);
        recArea2 = itemView.findViewById(R.id.req);
        recTitle = itemView.findViewById(R.id.Title);
    }

}
