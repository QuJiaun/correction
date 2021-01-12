package com.luckyxmobile.correction.presenter.impl;

import android.content.Context;
import android.content.Intent;

import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.model.BookModel;
import com.luckyxmobile.correction.model.bean.Book;
import com.luckyxmobile.correction.model.impl.BookModelImpl;
import com.luckyxmobile.correction.model.impl.CorrectionSharedPreImpl;
import com.luckyxmobile.correction.model.impl.TagDaoImpl;
import com.luckyxmobile.correction.presenter.MainViewPresenter;
import com.luckyxmobile.correction.presenter.OnBookFinishedListener;
import com.luckyxmobile.correction.utils.IImage;
import com.luckyxmobile.correction.utils.ImageUtil;
import com.luckyxmobile.correction.ui.activity.CropImageActivity;
import com.luckyxmobile.correction.utils.ConstantsUtil;
import com.luckyxmobile.correction.ui.dialog.BookInfoDialog;
import com.luckyxmobile.correction.view.IMainView;
import com.luckyxmobile.correction.ui.activity.MainActivity;

import org.litepal.LitePal;

/**
 * MainView的presenter层，负责MainView与Model层（数据库）的联系
 * 提供功能：addBook(), alterBook(), deleteBook()，通知model层进行操作，并返回结果给MainView
 * 错题本信息弹窗Dialog 与 MainView 解耦
 * @author qujiajun 2020/12/08
 */
public class MainViewPresenterImpl implements OnBookFinishedListener, MainViewPresenter {

    private final IMainView mainView;
    private final BookModel bookModel;
    private volatile BookInfoDialog bookInfoDialog;
    private final IImage imageUtil;

    public MainViewPresenterImpl(Context context){
        this.mainView = (IMainView) context;
        bookModel = new BookModelImpl();
        bookModel.setOnBookFinishedListener(this);
        imageUtil = new ImageUtil(context);

        initSQLFirst();

        mainView.setHeadBookList(LitePal.findAll(Book.class));
    }

    /**
     * 在第一次安装时，初始化数据库
     */
    private void initSQLFirst(){
        CorrectionSharedPreImpl preferenceImpl = CorrectionSharedPreImpl.initPreferencesImpl(mainView.getMainViewContext());

        if (preferenceImpl.getPreferences().getBoolean(ConstantsUtil.TABLE_SHARED_IS_FIRST_START, true)){
            preferenceImpl.getEditor().putBoolean(ConstantsUtil.TABLE_SHARED_IS_FIRST_START, true).apply();

            bookModel.newBook(mainView.getMainViewContext().getString(R.string.favorites),"R.mipmap.favorite");

            TagDaoImpl.newTag(mainView.getMainViewContext().getString(R.string.emphasis));
            TagDaoImpl.newTag(mainView.getMainViewContext().getString(R.string.error_prone));
        }
    }


    /**
     * 获取dialog实例
     * @return BookInfoDialog
     */
    private BookInfoDialog getBookInfoDialog(){

        bookInfoDialog = new BookInfoDialog(mainView.getMainViewContext());

        //设置取消按钮
        bookInfoDialog.setNegativeButton(R.string.cancel,null);

        //修改错题本封面按钮
        bookInfoDialog.getAlterBookCoverBtn().setOnClickListener(v ->{

            mainView.startActivityForResult(CropImageActivity.getJumpIntent(mainView.getMainViewContext(),
                    MainActivity.TAG, true, ConstantsUtil.IMAGE_BOOK_COVER,
                    false,false,0),
                    100);

            Intent intent = new Intent(mainView.getMainViewContext(), CropImageActivity.class);
            mainView.startActivityForResult(intent, ConstantsUtil.REQUEST_CODE);
        });



        return bookInfoDialog;
    }

    /**
     * 接收从MainView返回的图片路径
     * @param path 图片路径
     */
    public void alterBookCoverPath(String path){
        if (bookInfoDialog != null){//修改错题本封面
            bookInfoDialog.setBookCoverView(path);
        }
    }

    @Override
    public void addBook() {

        bookInfoDialog = getBookInfoDialog();

        bookInfoDialog.setPositiveButton(R.string.ensure, (dialogInterface, i) -> {
            if(bookInfoDialog.getBookName() == null){//错题本名字为空
                bookInfoDialog.onFailToast(mainView.getMainViewContext().getString(R.string.empty_input));
            }else{//调用model层，在数据库添加错题本
                bookModel.newBook(bookInfoDialog.getBookName(), bookInfoDialog.getBookCoverPath());
            }
        });

        //显示错题本信息dialog
        bookInfoDialog.build().show();
    }

    @Override
    public void alterBookInfo(Book book) {

        bookInfoDialog = getBookInfoDialog();

        bookInfoDialog.setBookName(book.getBook_name())
        .setBookCoverView(book.getBook_cover());

        bookInfoDialog.setPositiveButton(R.string.ensure, (dialogInterface, i) -> {
            if(bookInfoDialog.getBookName() == null){//错题本名字为空
                bookInfoDialog.onFailToast(mainView.getMainViewContext().getString(R.string.empty_input));
            }else{//调用model层，在数据库修改错题本
                if (!book.getBook_name().equals(bookInfoDialog.getBookName())
                || !book.getBook_cover().equals(bookInfoDialog.getBookCoverPath())){
                    bookModel.alterBook(book.getId(), bookInfoDialog.getBookName(), bookInfoDialog.getBookCoverPath());
                }
            }
        });

        //显示错题本信息dialog
        bookInfoDialog.build().show();

    }

    @Override
    public void deleteBook(Book book) {
        mainView.deleteBookFinished(book);
        bookModel.deleteBook(book.getId());
    }

    @Override
    public void openCamera() {
        imageUtil.openCamera();
    }

    @Override
    public void openAlbum() {
        imageUtil.openAlbum();
    }

    @Override
    public Context getContext() {
        return mainView.getMainViewContext();
    }


    @Override
    public void addSuccess(Book book) {
        mainView.addBookFinished(book);
        mainView.OnToastFinished(mainView.getMainViewContext().getString(R.string.add_successful));
    }

    @Override
    public void alterSuccess(Book book) {
        mainView.alterBookFinished(book);
        mainView.OnToastFinished(mainView.getMainViewContext().getString(R.string.alter_successful));
    }

    @Override
    public void deleteSuccess() {
        mainView.OnToastFinished(mainView.getMainViewContext().getString(R.string.delete_successful));
    }

}
