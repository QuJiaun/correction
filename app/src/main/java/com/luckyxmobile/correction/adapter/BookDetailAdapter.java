package com.luckyxmobile.correction.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.impl.CorrectionLab;
import com.luckyxmobile.correction.model.impl.TagDaoImpl;
import com.luckyxmobile.correction.model.impl.TopicDaoImpl;
import com.luckyxmobile.correction.ui.activity.TopicViewPageActivity;
import com.luckyxmobile.correction.utils.ConstantsUtil;
import com.luckyxmobile.correction.utils.ImageTask;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.utils.impl.FilesUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import org.litepal.LitePal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author LiuGen
 * @date 2019/7/23
 *
 *@author 修改 qjj
 * */
public class BookDetailAdapter extends RecyclerView.Adapter<BookDetailAdapter.ViewHolder> implements Filterable {

    private String TAG = "BookDetailAdapter";
    private Context mContext;
    /**是否显示删除*/
    public static boolean mShowDelete = false;
    /**该错题本所有的题（数据库内）*/
    private List<Topic> topics;
    /**该错题本的题（过滤）*/
    private List<Topic> topicsFilter;
    /**该错题本要删除的题*/
    private List<Topic> topicsDelete = new ArrayList<>();
    private int book_id;
    private SharedPreferences preferences;
    private ImageTask imageTask;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                onItemListener.finishDeleteTopic(true);
            }else if (msg.what == 0){
                onItemListener.finishDeleteTopic(false);
            }

        }
    };


    public BookDetailAdapter(Context context, List<Topic> topics, int book_id){
        this.mContext = context;
        this.topics = topics;
        this.topicsFilter = topics;
        this.book_id = book_id;
        preferences = context.getSharedPreferences(ConstantsUtil.TABLE_SHARED_CORRECTION,MODE_PRIVATE);
        String s = preferences.getString(ConstantsUtil.TABLE_SHOW_SMEAR,"");
        List<String> whichShowPrint;
        if (TextUtils.isEmpty(s)){
            whichShowPrint = new ArrayList<>();
        }else{
            whichShowPrint = ImageUtil.transformListOnSmear(context, s);
        }

        imageTask = new ImageTask(context, topics, whichShowPrint);
        imageTask.setBookDetailAdapter(this);
        imageTask.execute();
    }

    public void upTopics(List<Topic> topics){
        this.topics = topics;
        this.topicsFilter = topics;
        notifyDataSetChanged();
    }

    public void setTopicsDelete(List<Topic> topicsDelete) {
        this.topicsDelete = topicsDelete;
    }

    public List<Topic> getTopicsDelete() {
        return topicsDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(mContext).inflate( R.layout.recycle_item_book_topic, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        // 获取错题对象
        Topic topic = topicsFilter.get(position);

        holder.topicDate.setText(getTopicDate(topic.getTopic_create_time()));

        if (topic.isTopic_collection() == 1){
            holder.likeButton_collection.setLiked(true);
        }else{
            holder.likeButton_collection.setLiked(false);
        }

        //@solved 解决在BookDetailActivity页面点击GridView也会触发CheckBox的选中事件的BUG
        // 设置CheckBox不可点击，同时修改selector_correction_collection中的样式
        holder.likeButton_collection.setEnabled(false);
        holder.likeButton_collection.setClickable(false);

        Bitmap bitmap = imageTask.getImageCache(topic.getId());

        if (bitmap != null){
            // 加载原题图片
            Glide.with(mContext)
                    .load(bitmap)
//                    .thumbnail(0.5f)
                    .error(R.drawable.ic_error_outline_white_24dp)
                    .into(holder.image_correction);
        }


        if (mShowDelete){
            holder.itemChecked.setVisibility(View.VISIBLE);
            if (!TopicDaoImpl.isTopicsContainTopic(topicsDelete,topicsFilter.get(position))){
                holder.itemChecked.setImageResource(R.drawable.item_uncheck);
                holder.frameLayout.setBackgroundResource(R.color.white);
            }else{
                holder.itemChecked.setImageResource(R.drawable.topic_delete_check);
                holder.frameLayout.setBackgroundResource(R.drawable.check_delete_background);
            }
        }else{
            holder.itemChecked.setVisibility(View.GONE);
            holder.frameLayout.setBackgroundResource(R.color.white);
        }

        List<Tag> itemTags = TagDaoImpl.findTagByTopic(LitePal.find(Topic.class, topic.getId()).getTopic_tag());

        if (!itemTags.isEmpty()){
            // 在错题本详情界面显示标签布局
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setAdapter(new TagAdapter<Tag>(itemTags) {
                @Override
                public View getView(FlowLayout parent, int position, Tag tag) {
                    TextView tagText = (TextView) LayoutInflater.from(mContext).inflate
                            (R.layout.flow_item_tag_on_book, holder.tagLayout, false);
                    tagText.setText(tag.getTag_name());
                    tagText.setClickable(false);
                    return tagText;
                }
            });
        }else {
            holder.tagLayout.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return topicsFilter.size();
    }


    //重写getFilter()方法
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                String tagsum = String.valueOf(constraint);

                if (tagsum.equals("")){
                    topicsFilter = topics;
                }else{
                    topicsFilter = TagDaoImpl.findTopicsByTags(topics, tagsum);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = topicsFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                topicsFilter = (List<Topic>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    /**
     * 加载布局的Holder
     * */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private LikeButton likeButton_collection;
        private ImageView image_correction;
        private TagFlowLayout tagLayout;
        private ImageView itemChecked;
        private FrameLayout frameLayout;
        private TextView topicDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_collection);
            likeButton_collection = itemView.findViewById(R.id.likebutton_correction);
            image_correction = itemView.findViewById(R.id.image_correction);
            tagLayout = itemView.findViewById(R.id.tags_showed_on_book);
            itemChecked = itemView.findViewById(R.id.item_Checked);
            frameLayout = itemView.findViewById(R.id.frame_layout);
            topicDate = itemView.findViewById(R.id.topic_date);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mShowDelete){
                        Intent intent = new Intent(mContext, TopicViewPageActivity.class);
                        intent.putExtra(ConstantsUtil.BOOK_ID, book_id);
                        intent.putExtra(ConstantsUtil.TOPIC_POSITION, findPositionByFilter(getAdapterPosition()));
                        mContext.startActivity(intent);
                    }else{
                        if (TopicDaoImpl.isTopicsContainTopic(topicsDelete,topicsFilter.get(getAdapterPosition()))){
                            topicsDelete.remove(topicsFilter.get(getAdapterPosition()));
                            itemChecked.setImageResource(R.drawable.item_uncheck);
                            frameLayout.setBackgroundResource(R.color.white);
                        }else{
                            startPropertyAnim(cardView);
                            topicsDelete.add(topicsFilter.get(getAdapterPosition()));
                            itemChecked.setImageResource(R.drawable.topic_delete_check);
                            frameLayout.setBackgroundResource(R.drawable.check_delete_background);
                        }
                    }
                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mShowDelete = true;
                    topicsDelete.clear();
                    onItemListener.onItemLongClickListener(getAdapterPosition());
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }

    // 动画实际执行
    private void startPropertyAnim(View view) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f),
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0.8f, 1f)
        );
        set.setDuration(1000);
        set.start();
    }



    private int findPositionByFilter(int positionFilter){

        int positionSource = -1;

        for (int i = 0; i < topics.size(); i++) {
            if (topics.get(i).getId() == topicsFilter.get(positionFilter).getId()){
                positionSource = i;
                return positionSource;
            }
        }

        return positionSource;
    }

    /**
     * 点击事件监听接口
     */
    public interface onItemListener {
        void onItemLongClickListener(int position);
        void finishDeleteTopic(boolean b);
    }

    private onItemListener onItemListener = null;

    public void setOnItemListener (onItemListener mListener) {
        this.onItemListener = mListener;
    }

    /**
     * 错题删除
     * @author qmn
     */
    public void deleteTopics() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();

                try {
                    for (Topic topic:topicsDelete){
                        FilesUtils.cascadeDeleteTopic(topic.getId(), mContext);
                        CorrectionLab.deleteTopic(topic.getId());
                        Log.d(TAG, "run: "+topic.getId());
                    }
                    message.what = 1;
                }catch (Exception e){
                    message.what = 0;
                }
                handler.sendMessage(message);
            }
        }).start();
    }

    private String getTopicDate(Date topicDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        return format0.format(topicDate);
    }
}
