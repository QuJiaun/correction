package com.luckyxmobile.correction.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.TextUtils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.adapter.PrintPreviewAdapter;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.model.BeanUtils;
import com.luckyxmobile.correction.model.bean.Paper;
import com.luckyxmobile.correction.model.bean.Topic;
import com.luckyxmobile.correction.model.bean.TopicImage;
import com.luckyxmobile.correction.ui.dialog.ProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author ChangHao
 * data: 2019年7月24日
 * 用于生成pdf
 * close的时候可以获取pdf的文件名, 然后通过SDCardUtil获取pdf的存储路径
 * 两个相加就是pdf文件的路径, 注意 '/'
 */
public class PdfUtils {

    private final static String TAG = "PdfUtils";
    //左和右的margin
    private float documentMarginLR = 50;
    //top和bottom的margin
    private float documentMarginTB = 30;

    //文件的路径+名字
    private String filePathName;
    private static Font boldFount20;
    private static Font normalFount16;
    private static Font normalFount12;

    //字间距
    private float CharacterSpace = 1.5f;

    private static Document document;

    private PdfUtils() { }

    private static final PdfUtils single = new PdfUtils();

    public static PdfUtils getInstance() {
        return single;
    }

    private Context context;
    private Paper paper;
    private List<Topic> topicList;
    private boolean showHighlighter;


    public PdfUtils init(Context context, Paper paper, List<Topic> topicList) throws Exception{

        this.context = context;
        this.paper = paper;
        this.topicList = topicList;
        this.showHighlighter = MySharedPreferences.getInstance().getBoolean(Constants.PRINT_HIDE_HIGHLIGHTER, false);

        document = new Document(PageSize.A4, documentMarginLR,
                documentMarginLR, documentMarginTB, documentMarginTB);
        filePathName = FilesUtils.getInstance().getPaperDir() + "/" + paper.getId() + ".pdf";
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePathName));
        pdfWriter.setStrictImageSequence(true);

        document.open();

        boldFount20 = setChineseFont(20, Font.BOLD);
        normalFount16 = setChineseFont(16, Font.NORMAL);
        normalFount12 = setChineseFont(12, Font.NORMAL);
        return this;
    }

    public PdfUtils init(Context context, Paper paper) throws Exception {
        return init(context, paper, BeanUtils.findTopicAll(paper));
    }

    public String start() throws Exception{
        ProgressDialog.getInstance().init(context).show();
        create();
        ProgressDialog.getInstance().init(context).dismiss();
        return close();
    }

    private Font setChineseFont(int size, int style) {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            bf = BaseFont.createFont();
//            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fontChinese;
    }

    //默认添加的PDF头
    private void defaultOp() throws DocumentException {
        //测试卷名
        addTitle2PDF(paper.getPaperName());
        //打印时间
        addText2PDF(context.getString(R.string.print_time)+":\t\t" +
                FilesUtils.getCurrentTime()+"\n", normalFount16, Element.ALIGN_RIGHT);
        //名字：__ 分数：__
        addText2PDF(context.getString(R.string.name)+" : ________\t\t\t\t\t\t\t\t"+
                context.getString(R.string.score)+ " : ________\n\n", normalFount16, Element.ALIGN_CENTER);
        addBlankToPdf(24);
    }

    private void create() throws Exception{
        defaultOp();

        String printPage = MySharedPreferences.getInstance().getString(Constants.TABLE_PRINT_PAGE, "0");

        if (printPage.contains("0")) {
            addTopicToPdf(Constants.TOPIC_STEM);
        }

        if (printPage.contains("1")) {
            addTitle2PDF(context.getString(R.string.correct));
            addTopicToPdf(Constants.TOPIC_CORRECT);
        }

        if (printPage.contains("2")) {
            addTitle2PDF(context.getString(R.string.incorrect));
            addTopicToPdf(Constants.TOPIC_INCORRECT);
        }

        if (printPage.contains("3")) {
            addTitle2PDF(context.getString(R.string.key));
            addTopicToPdf(Constants.TOPIC_KEY);
        }

        if (printPage.contains("4")) {
            addTitle2PDF(context.getString(R.string.cause));
            addTopicToPdf(Constants.TOPIC_CAUSE);
        }
    }

    private String close() {
        if (document.isOpen()) {
            document.close();
            return filePathName;
        } else {
            return "error";
        }
    }

    /**
     * @param Height 空白的大小  相对于A4纸的大小 note: A4的高度为842
     * @throws DocumentException on error
     * @author Changhao
     * 给pdf添加空白
     */
    private PdfUtils addBlankToPdf(Integer Height) throws DocumentException {
        Paragraph elements = new Paragraph(" ", normalFount16);
        elements.setLeading(Height);
        document.add(elements);
        return this;
    }

    /**
     * 生成一个有字间距的chunk
     */
    private Chunk getParFromChunk(String content, Font font) {
        Chunk chunk = new Chunk(content, font);
        chunk.setCharacterSpacing(CharacterSpace);
        return chunk;
    }

    private PdfUtils addTitle2PDF(String title) throws DocumentException{
        Paragraph elements = new Paragraph();
        elements.add(getParFromChunk(title, boldFount20));
        elements.setAlignment(Element.ALIGN_CENTER);
        document.add(elements); // result为保存的字符串
        return this;
    }

    private PdfUtils addText2PDF(String title, Font font, int alignment) throws DocumentException {
        Paragraph elements = new Paragraph(title, font);
        elements.setAlignment(alignment);
        document.add(elements);
        return this;
    }

    private PdfUtils addImage2PDF(TopicImage topicImage, int alignment) throws Exception{
        Bitmap bitmap = BitmapUtils.getBitmapInPdf(context, topicImage, showHighlighter);
        //宽度要适中,如果比A4的宽, 宽度就减去两倍的margin大,就是它的最大值
        float maxWidth = PageSize.A4.getWidth() - 2 * documentMarginLR;
        if (bitmap.getWidth() > maxWidth) {
            float maxHeight = maxWidth/bitmap.getWidth() * bitmap.getHeight();
            bitmap = BitmapUtils.resizeBitmap(bitmap, (int) maxWidth, (int) maxHeight);
        }

        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream3);

        Image img = Image.getInstance(stream3.toByteArray());
        img.setAlignment(alignment);
        img.scaleToFit(bitmap.getWidth(), bitmap.getHeight());

        Paragraph imgParagraph = new Paragraph();
        imgParagraph.add(img);
        document.add(imgParagraph);
        return this;
    }

    private void addTopicToPdf(@Constants.TopicImageType int type) throws Exception{
        int count = 1;
        List<TopicImage> topicImages;
        for (Topic topic: topicList) {
            //给每一道题添加标题
            addText2PDF(count++ + "  • " + BeanUtils.getBookName(topic), normalFount16, Element.ALIGN_LEFT);
            if (!TextUtils.isEmpty(topic.getText())) {
                addText2PDF(topic.getText(), normalFount12, Element.ALIGN_BASELINE);
            }
            topicImages = BeanUtils.findTopicImageByType(topic, Constants.TOPIC_STEM);
            for (TopicImage topicImage : topicImages) {
                addImage2PDF(topicImage, Element.ALIGN_LEFT);
            }
        }
    }

    public void previewWindow() throws Exception{

        String path = FilesUtils.getInstance().getPdfPath(paper);

        if (path == null) {
            path = start();
        }

        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        printManager.print(String.valueOf(R.string.app_name), new PrintPreviewAdapter(context, path), builder.build());
    }


    public void share() throws Exception{

        String path = FilesUtils.getInstance().getPdfPath(paper);

        if (path == null) {
            path = start();
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, FilesUtils.getInstance().getUri(new File(path)));
        shareIntent.setType("application/pdf");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
