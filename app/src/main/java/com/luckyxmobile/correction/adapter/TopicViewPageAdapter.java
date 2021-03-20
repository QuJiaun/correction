package com.luckyxmobile.correction.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.youth.banner.Banner;
import com.youth.banner.transformer.DepthPageTransformer;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TopicViewPageAdapter extends PagerAdapter {

    private Context context;
    private List<Topic> topics;
    private Map<Integer, List<TopicImage>> topicImageMap = new LinkedHashMap<>();

    public TopicViewPageAdapter(Context context, List<Topic> topics){
        this.context = context;
        this.topics = topics;

        for (Topic topic : topics) {
            int topic_id = topic.getId();
            List<TopicImage> topicImages =  LitePal.where("topic_id = ?",
                    String.valueOf(topic_id)).find(TopicImage.class);

            topicImageMap.put(topic_id, topicImages);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        return super.getItemPosition(object);
    }


    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view==object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view = View.inflate(context,R.layout.layout_banner,null);
        Banner<TopicImage, BannerTopicImageAdapter> banner = view.findViewById(R.id.banner);
        banner.setOrientation(Banner.VERTICAL);

        int topic_id = topics.get(position).getId();

        banner.setAdapter(new BannerTopicImageAdapter(context, topicImageMap.get(topic_id)));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {

        container.removeView((View)object);
    }


}
