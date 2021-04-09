package com.luckyxmobile.correction.utils;

import android.graphics.Bitmap;

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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class PdfHelper {

    private PdfWriter pdfWriter;
    private Document document;
    private Font boldFount20;
    private Font normalFount16;
    private Font normalFount12;

    private String filePath;

    private PdfHelper() {}

    private static final PdfHelper helper = new PdfHelper();

    protected static PdfHelper getInstance() {
        return helper;
    }

    public void onOpenDocument(String pdfPath) throws Exception{
        this.filePath = pdfPath;
        document = new Document(PageSize.A4, 50, 50, 30, 30);
        pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        pdfWriter.setStrictImageSequence(true);
        // 设置每行的间距
        pdfWriter.setInitialLeading(30);
        // 创建日期
        document.addCreationDate();
        document.open();
    }

    public String onCloseDocument() {
        if (document != null && document.isOpen()) {
            document.close();
        }
        return filePath;
    }

    public Font getBoldFount20() {
        if (boldFount20 == null) {
            boldFount20 = createFount(20, Font.BOLD);
        }
        return boldFount20;
    }

    public Font getNormalFount12() {
        if (normalFount12 == null) {
            normalFount12 = createFount(12, Font.NORMAL);
        }
        return normalFount12;
    }

    public Font getNormalFount16() {
        if (normalFount16 == null) {
            normalFount16 = createFount(16, Font.NORMAL);
        }
        return normalFount16;
    }

    private Font createFount(int size, int style) {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, size, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fontChinese;
    }

    /**
     * @param Height 空白的大小  相对于A4纸的大小 note: A4的高度为842
     * @throws DocumentException on error
     * @author Changhao
     * 给pdf添加空白
     */
    public void addBlankToPdf(Integer Height) throws DocumentException {
        Paragraph elements = new Paragraph(" ", normalFount16);
        elements.setLeading(Height);
        document.add(elements);
    }

    /**
     * 生成一个有字间距的chunk
     */
    private Chunk getParFromChunk(String content, Font font) {
        Chunk chunk = new Chunk(content, font);
        chunk.setCharacterSpacing(1.5f);
        return chunk;
    }

    public void addTitle2PDF(String title) throws DocumentException{
        Paragraph elements = new Paragraph();
        elements.add(getParFromChunk(title, getBoldFount20()));
        elements.setAlignment(Element.ALIGN_CENTER);
        document.add(elements); // result为保存的字符串
    }

    public void addText2PDF(String title, Font font, int alignment) throws DocumentException {
        Paragraph elements = new Paragraph(title, font);
        elements.setAlignment(alignment);
        document.add(elements);
    }

    public void addImage2PDF(Bitmap bitmap, int alignment) throws Exception{

        //宽度要适中,如果比A4的宽, 宽度就减去两倍的margin大,就是它的最大值
        float maxWidth = PageSize.A4.getWidth() - 2 * 50;
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
    }

}
