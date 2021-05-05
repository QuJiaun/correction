package com.luckyxmobile.correction.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Tag;
import com.luckyxmobile.correction.ui.dialog.AlertDialog;
import com.luckyxmobile.correction.ui.dialog.EditTextDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理标签页面（新建、添加、删除）
 * @author qjj
 * @date 2019/07/26
 */
public class TagManagerActivity extends AppCompatActivity {


    /**选中标签对象*/
    private TagFlowLayout tagLayoutChoose;
    /**全部标签对象*/
    private TagFlowLayout tagLayoutAll;
    /**选中标签集合*/
    private List<Tag> chooseTagList = new ArrayList<>();
    /**全部标签集合*/
    private List<Tag> allTagList = new ArrayList<>();

    private EditTextDialog editTextDialog;
    private AlertDialog alertDialog;

    private int curTopicId = -1;
    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        //初始化标题栏
        Toolbar toolbar = findViewById(R.id.add_tag_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        curTopicId = getIntent().getIntExtra(Constants.TOPIC_ID, -1);
        tagLayoutChoose = findViewById(R.id.tag_layout_choose);
        tagLayoutAll = findViewById(R.id.tag_layout_all);

        allTagList = LitePal.findAll(Tag.class);

        if (curTopicId > 0) {
            for (Tag tag : allTagList) {
                if (tag.getTopicSet().contains(curTopicId)) {
                    chooseTagList.add(tag);
                }
            }
        }

        //设置全部标签适配器
        initAdapterAll();

        //设置选中标签适配器
        initAdapterChoose();

    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_tag_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_tag:
                isDelete = !isDelete;
                tagLayoutAll.onChanged();
                break;
            case R.id.add_tag:
                if (isDelete) {
                    isDelete = false;
                    tagLayoutAll.onChanged();
                }
                showTagDialog(null);
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化已选标签的适配器
     * 设置点击事件
     */
    private void initAdapterChoose() {

        tagLayoutChoose.setAdapter(new TagAdapter<Tag>(chooseTagList) {
            @Override
            public View getView(FlowLayout parent, int position, Tag tag) {

                CheckBox checkBox = (CheckBox) LayoutInflater.from(TagManagerActivity.this).inflate
                        (R.layout.flow_item_tag,tagLayoutChoose, false);
                checkBox.setText(tag.getTag_name());

                checkBox.setChecked(true);
                checkBox.setClickable(false);
                checkBox.setTextColor(getColor(R.color.white));

                return checkBox;
            }
        });

        tagLayoutChoose.setOnTagClickListener((view, position, parent) -> {
            if (isDelete) {
                isDelete = false;
                tagLayoutAll.onChanged();
            }
            if (curTopicId > 0) {
                chooseTagList.get(position).getTopicSet().remove(curTopicId);
                chooseTagList.get(position).save();
            }
            chooseTagList.remove(position);
            tagLayoutChoose.onChanged();
            tagLayoutAll.onChanged();
            return true;
        });
    }

    /**
     * 初始化全部标签适配器-传入数据
     * 设置点击事件
     */
    private void initAdapterAll() {

        tagLayoutAll.setAdapter(new TagAdapter<Tag>(allTagList) {
            @Override
            public View getView(FlowLayout parent, int position, Tag tag) {
                CheckBox checkBox = (CheckBox) LayoutInflater.from(TagManagerActivity.this).inflate
                        (R.layout.flow_item_tag,tagLayoutAll, false);
                checkBox.setText(tag.getTag_name() + " × " + tag.getTopicSet().size());

                checkBox.setChecked(chooseTagList.contains(tag));
                if (isDelete){
                    checkBox.setTextColor(Color.GRAY);
                    checkBox.setButtonDrawable(R.drawable.ic_delete);
                }

                return checkBox;
            }
        });

        //设置点击事件
        tagLayoutAll.setOnTagClickListener((view, position, parent) -> {

            if (!isDelete){
                if (curTopicId > 0) {
                    if (!chooseTagList.contains(allTagList.get(position))){
                        allTagList.get(position).getTopicSet().add(curTopicId);
                        allTagList.get(position).save();
                        chooseTagList.add(0,allTagList.get(position));
                    }else{
                        allTagList.get(position).getTopicSet().remove(curTopicId);
                        allTagList.get(position).save();
                        chooseTagList.remove(allTagList.get(position));
                    }
                } else {
                    showTagDialog(allTagList.get(position));
                }
            }else{
                deleteTag(position);
            }

            tagLayoutChoose.onChanged();
            tagLayoutAll.onChanged();
            return true;
        });
    }

    private void showTagDialog(Tag tag) {
        if (editTextDialog == null) {
            editTextDialog = new EditTextDialog(this);
            editTextDialog.create();
            editTextDialog.setTitle(R.string.tags);
            editTextDialog.setTextHint(R.string.input_tag_name);
            editTextDialog.setMaxLength(8);
            editTextDialog.setNegativeButton(R.string.cancel, () -> {
                editTextDialog.dismiss();
                isDelete = false;
                tagLayoutAll.onChanged();
            });
        }
        editTextDialog.setText(tag==null?null:tag.getTag_name());
        editTextDialog.setPositiveButton(R.string.ensure, () -> {
            String tagName = editTextDialog.getText();
            if (TextUtils.isEmpty(tagName)) return;

            if (BeanUtils.existsTag(tagName)) {
                if (tag == null || !tag.isSaved() || !tagName.equals(tag.getTag_name())) {
                    editTextDialog.onError(R.string.hint_repeated_tag);
                } else {
                    editTextDialog.dismiss();
                }
            } else {
                Tag result = tag==null?new Tag(tagName):tag;
                result.setTag_name(tagName);
                if (curTopicId > 0) {
                    result.getTopicSet().add(curTopicId);
                    chooseTagList.add(0, result);
                    tagLayoutChoose.onChanged();
                }

                if (!result.isSaved()) {
                    allTagList.add(0, result);
                } else {
                    int index = allTagList.indexOf(result);
                    allTagList.remove(index);
                    allTagList.add(index, result);
                }
                result.save();
                tagLayoutAll.onChanged();
                editTextDialog.dismiss();
            }
        });
        editTextDialog.show();
    }

    private void deleteTag(final int position) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog(this);
            alertDialog.create();
            alertDialog.setMessage(R.string.confirm_delete_tag);
        }
        alertDialog.setTitle(allTagList.get(position).getTag_name());
        alertDialog.setPositiveButton(R.string.ensure, () -> {
            allTagList.get(position).delete();
            chooseTagList.remove(allTagList.get(position));
            allTagList.remove(position);
            isDelete = false;
            tagLayoutChoose.onChanged();
            tagLayoutAll.onChanged();
            alertDialog.dismiss();
        });

        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDelete) {
            isDelete = false;
            tagLayoutAll.onChanged();
        }
        return true;
    }

    private void onToast(String log) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
    }
}
