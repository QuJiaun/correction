package com.luckyxmobile.correction.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.PrintPreviewAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MyApplication;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author ChangHao
 * data: 2019年7月24日
 * 用于生成pdf
 * close的时候可以获取pdf的文件名, 然后通过SDCardUtil获取pdf的存储路径
 * 两个相加就是pdf文件的路径, 注意 '/'
 */
public class PdfUtils{

    private final static String TAG = "PdfUtils";

    private Context context;
    private Paper paper;
    private List<Topic> topicList;
    private String filePathName;

    private MySharedPreferences preferences = MySharedPreferences.getInstance();
    private boolean showHighlighter;
    private String printPage;

    private PdfHelper pdfHelper = PdfHelper.getInstance();

    private PdfUtils() {
        this.context = MyApplication.getContext();
    }

    private static final PdfUtils single = new PdfUtils();

    public static PdfUtils getInstance() {
        return single;
    }

    public PdfUtils init(Paper paper) throws Exception {
        return init(paper, BeanUtils.findTopicAll(paper));
    }

    public PdfUtils init(Paper paper, List<Topic> topicList) throws Exception{
        this.paper = paper;
        this.topicList = topicList;

        this.showHighlighter = preferences.getBoolean(Constants.PRINT_HIDE_HIGHLIGHTER, true);
        this.printPage = preferences.getString(Constants.TABLE_PRINT_PAGE, "0");

        filePathName = FilesUtils.getInstance().getPdfPath(paper);
        FilesUtils.getInstance().createFile(filePathName);
        pdfHelper.onOpenDocument(filePathName);
        return this;
    }


    public boolean start() throws Exception{
        if (topicList == null || topicList.isEmpty()) {
            return false;
        }
        //复习卷头
        defaultOp();
        if (printPage.contains("0")) { //复习卷正文-题干
            addTopicToPdf(Constants.TOPIC_STEM);
        }
        if (printPage.contains("1")) { //复习卷正文-正解
            pdfHelper.addTitle2PDF(context.getString(R.string.correct));
            addTopicToPdf(Constants.TOPIC_CORRECT);
        }
        if (printPage.contains("2")) { //复习卷正文-错解
            pdfHelper.addTitle2PDF(context.getString(R.string.incorrect));
            addTopicToPdf(Constants.TOPIC_INCORRECT);
        }
        if (printPage.contains("3")) { //复习卷正文-知识点
            pdfHelper.addTitle2PDF(context.getString(R.string.key));
            addTopicToPdf(Constants.TOPIC_KEY);
        }
        if (printPage.contains("4")) { //复习卷正文-原因
            pdfHelper.addTitle2PDF(context.getString(R.string.cause));
            addTopicToPdf(Constants.TOPIC_CAUSE);
        }
        pdfHelper.onCloseDocument();
        return true;
    }



    private void addTopicToPdf(int type) throws Exception{
        int count = 1;
        List<TopicImage> topicImages;
        for (Topic topic: topicList) {
            //给每一道题添加标题
            pdfHelper.addText2PDF(count++ + "  • " + BeanUtils.getBookName(topic),
                    pdfHelper.getNormalFount16(), Element.ALIGN_LEFT);

            if (!TextUtils.isEmpty(topic.getText())) {
                pdfHelper.addText2PDF(topic.getText(), pdfHelper.getNormalFount12(),
                        Element.ALIGN_BASELINE);
            }

            topicImages = BeanUtils.findTopicImageByType(topic, type);
            if (!topicImages.isEmpty()) {
                for (TopicImage topicImage : topicImages) {
                    Bitmap bitmap = BitmapUtils.getBitmapInPdf(topicImage, showHighlighter);
                    pdfHelper.addImage2PDF(bitmap, Element.ALIGN_LEFT);
                    bitmap.recycle();
                }
            }
        }
    }

    //默认添加的PDF头
    private void defaultOp() throws DocumentException {
        //测试卷名
        pdfHelper.addTitle2PDF(paper.getPaperName());
        //打印时间
        pdfHelper.addText2PDF("打印时间 :\t\t" + FilesUtils.getCurrentTime()+"\n",
                pdfHelper.getNormalFount16(), Element.ALIGN_RIGHT);
        //名字：__ 分数：__
        pdfHelper.addText2PDF("名字 : ________\t\t\t\t\t\t\t\t 分数 : ________\n\n",
                pdfHelper.getNormalFount16(), Element.ALIGN_CENTER);

        pdfHelper.addBlankToPdf(12);
    }
}
