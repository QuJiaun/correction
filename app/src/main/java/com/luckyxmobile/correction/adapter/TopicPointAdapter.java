package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.Target;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.ui.activity.TopicInfoActivity;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.utils.FastJsonUtil;
import com.luckyxmobile.correction.utils.OpenCVUtil;
import com.luckyxmobile.correction.utils.ImageUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class TopicPointAdapter extends RecyclerView.Adapter<TopicPointAdapter.ViewHolder> {

    private Context context;

    private Topic topic;

    private List<String> imagesPath;

    private TopicImagesHighlighter topicImagesHighlighter;

    private boolean isPrimitiveImage = false;

    public TopicPointAdapter(Context context, Topic topic, LinearLayout linearLayout){

        this.context = context;
        this.topic = topic;

        imagesPath = getImagesPath();

        if (imagesPath.size()>0){
            linearLayout.setVisibility(View.VISIBLE);
        }

        String text = topic.getTopic_knowledge_point_text();

        if (text != null){
            text = text.trim();
            if (!text.isEmpty()){
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<String> getImagesPath(){
        topic = LitePal.find(Topic.class,topic.getId());
        topicImagesHighlighter = FastJsonUtil.jsonToObject(topic.getTopic_knowledge_point_picture(), TopicImagesHighlighter.class);

        if (topicImagesHighlighter == null){
            return new ArrayList<>();
        }else{
            return topicImagesHighlighter.getPrimitiveImagePathList();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_topic_images, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (!TopicInfoActivity.isEditing){
            holder.deleteImage.setVisibility(View.GONE);
        }else{
            holder.deleteImage.setVisibility(View.VISIBLE);
        }

        String contrastRadio;
        try {
            contrastRadio = topicImagesHighlighter.getImageContrastRadioList().get(position);
        }catch (Exception e){
            contrastRadio = Constants.CONTRAST_RADIO_COMMON;
        }

        Bitmap bitmap = OpenCVUtil.setImageContrastRadioByPath(contrastRadio,imagesPath.get(position));

        if (isPrimitiveImage){
            bitmap = ImageUtil.getBitmapByImagePath(imagesPath.get(position));
        }

        holder.imageView.setPadding(16,8,16,8);
        Glide.with(context)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(bitmap)
                .fitCenter()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .thumbnail(0.5f)
                .into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        return imagesPath.size();
    }

    public void flashImagePath(){
        imagesPath = getImagesPath();
        notifyDataSetChanged();
    }

    public void showPrimitiveImages(boolean isPrimitiveImage){
        this.isPrimitiveImage = isPrimitiveImage;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout frameLayout;
        private ImageView imageView;
        private ImageView deleteImage;


        public ViewHolder(View itemView) {
            super(itemView);
            frameLayout = itemView.findViewById(R.id.item_topic_image_layout);
            imageView = itemView.findViewById(R.id.item_topic_image);
            deleteImage = itemView.findViewById(R.id.item_remove_topic_image);


            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TopicInfoActivity.showImageBiggerDialog(imagesPath,context,getAdapterPosition());
                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    assert topicImagesHighlighter != null;
                    topicImagesHighlighter.removeWhichImage(getAdapterPosition(),context);
                    topic.setTopic_knowledge_point_picture(FastJsonUtil.objectToJson(topicImagesHighlighter));
                    topic.save();
                    imagesPath = getImagesPath();
                    notifyItemRemoved(getAdapterPosition());

                }
            });
        }
    }
}
