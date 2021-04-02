package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.activity.EditTopicImageActivity;
import com.luckyxmobile.correction.ui.activity.TopicInfoActivity;
import com.luckyxmobile.correction.utils.BitmapUtils;
import com.luckyxmobile.correction.utils.ImageTask;
import com.luckyxmobile.correction.utils.OpenCVUtil;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    private boolean isShowOriginalImage;
    private final TopicInfoListener listener;

    private final List<Object> typeList = new ArrayList<>();
    private int topicImageSize = 0;
    private boolean removeMode = false;

    public TopicInfoAdapter(Context context, List<TopicImage> imageList) {
        this.context = context;
        this.listener = (TopicInfoListener) context;
        this.topicImageSize = imageList.size();
        size(imageList);

        isShowOriginalImage = MySharedPreferences.getInstance().getBoolean(Constants.SHOW_ORIGINAL, true);
    }

    public void setRemoveMode(boolean removeMode) {
        this.removeMode = removeMode;
    }

    public boolean isRemoveMode() {
        return removeMode;
    }

    public void size(List<TopicImage> imageList) {

        Map<Integer, List<TopicImage>> tmpMap = new LinkedHashMap<>();

        for (TopicImage topicImage : imageList) {
            int type = topicImage.getType();

            if (!tmpMap.containsKey(type)) {
                tmpMap.put(type, new ArrayList<>());
            }
            tmpMap.get(type).add(topicImage);
        }

        for (int type : tmpMap.keySet()) {
            typeList.add(type);
            List<TopicImage> topicImageList = tmpMap.get(type);
            typeList.addAll(topicImageList);
        }
    }

    private int getTextByType(int type){
        switch (type) {
            case Constants.TOPIC_STEM:
               return R.string.stem;

            case Constants.TOPIC_CORRECT:
                return R.string.correct;

            case Constants.TOPIC_INCORRECT:
                return R.string.incorrect;

            case Constants.TOPIC_KEY:
                return R.string.key;

            case Constants.TOPIC_CAUSE:
                return R.string.cause;

            default:
                return -1;
        }
    }

    public void setShowOriginalImage(boolean showOriginalImage) {
        isShowOriginalImage = showOriginalImage;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_topic_images, parent, false);
            return new TopicInfoHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_topic_label, parent, false);
            return new TopicLabelHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (typeList.get(position) instanceof Integer) {
            int currentType = (int) typeList.get(position);
            currentType = getTextByType(currentType);
            TopicLabelHolder viewHolder = (TopicLabelHolder) holder;
            if (currentType != -1) {
                viewHolder.topicLabelTv.setText(context.getText(currentType));
            }

        } else {

            TopicImage topicImage = (TopicImage) typeList.get(position);

            TopicInfoHolder viewHolder = (TopicInfoHolder) holder;
            if (isShowOriginalImage) {
                Glide.with(context).load(topicImage.getPath()).into(viewHolder.topicIv);
            } else {
                Glide.with(context).load(
                        BitmapUtils.getBitmapInTopicInfo(context, topicImage)
                ).into(viewHolder.topicIv);
            }

            viewHolder.editTopicBtn.setVisibility(removeMode?View.VISIBLE:View.INVISIBLE);
            viewHolder.editTopicBtn.setOnClickListener(view -> {
                ImageTask.getInstance().clearTopicImage(topicImage);
                Intent intent = new Intent(context, EditTopicImageActivity.class);
                intent.putExtra(Constants.FROM_ACTIVITY, TopicInfoActivity.TAG);
                intent.putExtra(Constants.TOPIC_IMAGE_ID, topicImage.getId());
                intent.putExtra(Constants.IMAGE_PATH, topicImage.getPath());
                context.startActivity(intent);
            });

            viewHolder.removeTopicBtn.setVisibility(removeMode?View.VISIBLE:View.INVISIBLE);
            viewHolder.removeTopicBtn.setOnClickListener(view -> {
                if (topicImageSize > 1) {
                    topicImageSize--;
                    typeList.remove(position);
                    notifyItemRemoved(position);
                    ImageTask.getInstance().clearTopicImage(topicImage);
                    listener.removeTopicImage(topicImage);
                } else {
                    Toast.makeText(context, R.string.warning_picture, Toast.LENGTH_SHORT).show();
                }
            });

            viewHolder.itemView.setOnClickListener(view ->{
                if (!removeMode) {
                    listener.onClickTopicImage(topicImage);
                }
            } );

            viewHolder.itemView.setOnLongClickListener(v -> {
                removeMode = !removeMode;
                notifyDataSetChanged();
                return true;
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (typeList.get(position) instanceof Integer) {
            return (int) typeList.get(position);
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public static class TopicLabelHolder extends RecyclerView.ViewHolder {

        TextView topicLabelTv;

        TopicLabelHolder(@NonNull View itemView) {
            super(itemView);
            topicLabelTv = itemView.findViewById(R.id.item_topic_label);
        }
    }

    public static class TopicInfoHolder extends RecyclerView.ViewHolder {

        ImageView topicIv;
        ImageView removeTopicBtn;
        ImageView editTopicBtn;

        TopicInfoHolder(@NonNull View itemView) {
            super(itemView);

            topicIv = itemView.findViewById(R.id.item_topic_image);
            removeTopicBtn = itemView.findViewById(R.id.item_remove_topic_image);
            editTopicBtn = itemView.findViewById(R.id.item_edit_topic_image);
        }
    }

    public interface TopicInfoListener{
        void removeTopicImage(TopicImage topicImage);
        void onClickTopicImage(TopicImage topicImage);
    }
}
