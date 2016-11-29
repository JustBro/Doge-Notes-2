package ru.oboturov_corp.dogenotes2;


import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private boolean mIsAddBtnsShow = false;
    private boolean mIsDeleteBtnShow = false;
    private boolean mIsDeselectBtnShow = false;

    private DbHelper mDbHelper;
    private Set<String> mSelectedItems = new HashSet<>();

    private String mOpenFolder = DbHelper.KEY_MAIN_FOLDER;
    private String mOpenNote;

    private FoldersFragment mFirstFragment = FoldersFragment.newInstance(mOpenFolder);
    private Fragment mSecondFragment = NotesFragment.newInstance(mOpenFolder);

    private ImageView mDeleteBtn;
    private ImageView mDeselectBtn;
    private ImageView mAddFolderBtn;
    private ImageView mAddNoteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new DbHelper(this);
        setFragments(mFirstFragment, mSecondFragment);

        final ImageView dogeBtn = (ImageView) findViewById(R.id.doge_fab);
        Animation animation = AnimationUtils
                .loadAnimation(this, R.anim.doge_btn_show_anim);
        animation.setInterpolator(new BounceInterpolator());
        dogeBtn.startAnimation(animation);

        mAddFolderBtn = (ImageView) findViewById(R.id.add_folder);
        mAddNoteBtn = (ImageView) findViewById(R.id.add_note);

        setPresListener(mAddFolderBtn);
        setPresListener(mAddNoteBtn);

        dogeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                setBtnPressAnimation(dogeBtn, event.getAction());

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setBtnSlideOutAnimation(mAddFolderBtn, 210, 50, mIsAddBtnsShow);
                    setBtnSlideOutAnimation(mAddNoteBtn, 50, 210, mIsAddBtnsShow);
                    mIsAddBtnsShow = !mIsAddBtnsShow;
                }
                return true;
            }
        });

        mDeleteBtn = (ImageView) findViewById(R.id.delete_btn);
        mDeleteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                setBtnPressAnimation(mDeleteBtn, event.getAction());
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (!mSelectedItems.isEmpty()) {
                        Iterator<String> iterator = mSelectedItems.iterator();
                        while (iterator.hasNext()) {
                            String item = iterator.next();
                            mDbHelper.deleteItem(item);
                            iterator.remove();
                        }
                        showOrHideBtn(mDeselectBtn, mIsDeselectBtnShow);
                    }

                    if (mSecondFragment instanceof NoteModifyFragment) {
                        mDbHelper.deleteItem(mOpenNote);
                        mSecondFragment = NotesFragment.newInstance(mOpenFolder);
                    } else {
                        ((NotesFragment) mSecondFragment).rebuildView();
                    }

                    setFragments(mFirstFragment, mSecondFragment);
                    mFirstFragment.rebuildView();

                    Toast.makeText(getApplicationContext(), R.string.note_is_deleted,
                            Toast.LENGTH_SHORT).show();
                    showOrHideBtn(mDeleteBtn, mIsDeleteBtnShow);
                    hideAddBtns();
                }
                return true;
            }
        });

        mDeselectBtn = (ImageView) findViewById(R.id.deselect_btn);
        mDeselectBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                setBtnPressAnimation(mDeselectBtn, event.getAction());
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mSelectedItems.clear();
                    if (!(mSecondFragment instanceof NoteModifyFragment)) {
                        ((NotesFragment) mSecondFragment).rebuildView();
                        showOrHideBtn(mDeleteBtn, mIsDeleteBtnShow);
                    }
                    mFirstFragment.rebuildView();
                    showOrHideBtn(mDeselectBtn, mIsDeselectBtnShow);
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mSelectedItems.isEmpty()) mSelectedItems.clear();
        if (mIsDeleteBtnShow) showOrHideBtn(mDeleteBtn, true);
        if (mIsDeselectBtnShow) showOrHideBtn(mDeselectBtn, true);
        hideAddBtns();
        if (mSecondFragment instanceof NoteModifyFragment) {
            mSecondFragment = NotesFragment.newInstance(mOpenFolder);
            setFragments(mFirstFragment, mSecondFragment);
        } else if (!mOpenFolder.equals(DbHelper.KEY_MAIN_FOLDER)){
            mOpenFolder = mDbHelper.getDateForId(mOpenFolder, DbHelper.KEY_PARENT_FOLDER);
            mFirstFragment = FoldersFragment.newInstance(mOpenFolder);
            mSecondFragment = NotesFragment.newInstance(mOpenFolder);
            setFragments(mFirstFragment, mSecondFragment);
        } else {
            this.finish();
        }
    }

    public void onItemClick(String id) {
        if (!mSelectedItems.isEmpty()) mSelectedItems.clear();
        if (mIsDeselectBtnShow) showOrHideBtn(mDeselectBtn, true);
        if (mIsDeleteBtnShow) showOrHideBtn(mDeleteBtn, true);
        hideAddBtns();
        String type = mDbHelper.getDateForId(id, DbHelper.KEY_ITEM_TYPE);
        if (type.equals(DbHelper.TYPE_FOLDER)) {
            mOpenFolder = id;
            mFirstFragment = FoldersFragment.newInstance(id);
            mSecondFragment = NotesFragment.newInstance(id);
            setFragments(mFirstFragment, mSecondFragment);
        } else {
            mOpenNote = id;
            mSecondFragment = NoteModifyFragment.newInstance(id);
            setFragments(mFirstFragment, mSecondFragment);
            mFirstFragment.rebuildView(); //т.к. могут быть выбраны элементы
            if (!mIsDeleteBtnShow) showOrHideBtn(mDeleteBtn, false);
        }
    }

    public void putInSet(String id) {
        mSelectedItems.add(id);
        if (!mIsDeleteBtnShow) showOrHideBtn(mDeleteBtn, false);
        if (!mIsDeselectBtnShow) showOrHideBtn(mDeselectBtn, false);
    }

    public void removeFromSet(String id) {
        mSelectedItems.remove(id);
        if (mIsDeselectBtnShow && mSelectedItems.isEmpty())
            showOrHideBtn(mDeselectBtn, mIsDeselectBtnShow);
        if (mIsDeleteBtnShow && mSelectedItems.isEmpty()
                && !(mSecondFragment instanceof NoteModifyFragment))
            showOrHideBtn(mDeleteBtn, mIsDeleteBtnShow);
    }

    public boolean isSetContains(String id) {
        return mSelectedItems.contains(id);
    }

    private void showOrHideBtn(View btn, boolean isBtnShow) {
        Animation animation;
        if (!isBtnShow) {
            animation = AnimationUtils.loadAnimation(this, R.anim.btn_show_anim);
            animation.setInterpolator(new MyInterpolator());
            btn.setVisibility(View.VISIBLE);
            btn.startAnimation(animation);

            if (btn.getId() == R.id.delete_btn) {
                mIsDeleteBtnShow = true;
            } else {
                mIsDeselectBtnShow = true;}
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.btn_hide_anim);
            animation.setInterpolator(new AnticipateOvershootInterpolator());
            btn.setVisibility(View.GONE);

            if (btn.getId() == R.id.delete_btn) {
                mIsDeleteBtnShow = false;
            } else {
                mIsDeselectBtnShow = false;}
        }
        btn.startAnimation(animation);
    }

    private void setBtnSlideOutAnimation(View btn, float xTo, float yTo, boolean isAddBtnsShow) {
        btn.setVisibility(View.VISIBLE);
        if (!isAddBtnsShow) {
            ObjectAnimator.ofFloat(btn, View.TRANSLATION_X, 0, -xTo).start();
            ObjectAnimator.ofFloat(btn, View.TRANSLATION_Y, 0, -yTo).start();
            ObjectAnimator.ofFloat(btn, View.SCALE_X, 0, 1).start();
            ObjectAnimator.ofFloat(btn, View.SCALE_Y, 0, 1).start();
        } else {
            ObjectAnimator.ofFloat(btn, View.TRANSLATION_X, -xTo, 0).start();
            ObjectAnimator.ofFloat(btn, View.TRANSLATION_Y, -yTo, 0).start();
            ObjectAnimator.ofFloat(btn, View.SCALE_X, 1, 0).start();
            ObjectAnimator.ofFloat(btn, View.SCALE_Y, 1, 0).start();
        }
    }

    private void hideAddBtns() {
        if (mIsAddBtnsShow) {
            setBtnSlideOutAnimation(mAddFolderBtn, 210, 50, true);
            setBtnSlideOutAnimation(mAddNoteBtn, 50, 210, true);
            mIsAddBtnsShow = false;
        }
    }

    private void setBtnPressAnimation(View btn, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator.ofFloat(btn, View.SCALE_X, 1, .9f).start();
                ObjectAnimator.ofFloat(btn, View.SCALE_Y, 1, .9f).start();
                break;
            case MotionEvent.ACTION_UP:
                ObjectAnimator.ofFloat(btn, View.SCALE_X, .9f, 1).start();
                ObjectAnimator.ofFloat(btn, View.SCALE_Y, .9f, 1).start();
                break;
        }
    }

    private void setPresListener(final View btn) {
        final String type = btn.getId() == R.id.add_folder ?
                DbHelper.TYPE_FOLDER : DbHelper.TYPE_NOTE;

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                setBtnPressAnimation(btn, event.getAction());
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CreateNewItemFragment.newInstance(type, mOpenFolder)
                            .show(getSupportFragmentManager(), null);
                    hideAddBtns();
                }
                return true;
            }
        });
    }

    private void setFragments(Fragment fragment1, Fragment fragment2) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.firs_container, fragment1)
                .replace(R.id.second_container, fragment2)
                .commit();
    }
}
