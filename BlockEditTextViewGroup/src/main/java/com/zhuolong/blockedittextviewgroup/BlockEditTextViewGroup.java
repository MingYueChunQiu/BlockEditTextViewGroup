package com.zhuolong.blockedittextviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 明月春秋 on 2018/3/24.
 * 方块EditText容器view
 * 继承自LinearLayoutCompat
 */

public class BlockEditTextViewGroup extends LinearLayoutCompat {

    private int mCount;//方块个数
    private int mBlockSideLength;//方块边长
    private int mMargin, mMarginLeft, mMarginRight, mMarginTop, mMarginBottom;
    private int mTextSize;
    private OnFocusChangeListener mFocusChangeListener;
    private TextWatcher mTextWatcher;
    private List<AppCompatEditText> mBlockList;
    private View vFocused;//记录获取焦点的控件
    private List<String> mInputList;//存储所有输入集合
    private OnCompleteAllInputListener mListener;

    public BlockEditTextViewGroup(Context context) {
        this(context, null);
    }

    public BlockEditTextViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlockEditTextViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private BlockEditTextViewGroup(Builder builder) {
        this(builder.context);
        setCount(builder.count);
        setBlockSideLength(builder.blockSideLength);
        if (builder.margin > 0) {
            setMargin(builder.margin);
        } else {
            setMarginLeft(builder.marginLeft);
            setMarginRight(builder.marginRight);
            setMarginTop(builder.marginTop);
            setMarginBottom(builder.marginBottom);
        }
        setTextSize(builder.textSize);
        setOnFocusChangeListener(builder.onFocusChangeListener);
        setTextWatcher(builder.textWatcher);
        setOnCompleteAllInputListener(builder.onCompleteAllInputListener);
        setItemWidth(builder.itemWidth);
    }

    public OnCompleteAllInputListener getOnCompleteAllInputListener() {
        return mListener;
    }

    public void setOnCompleteAllInputListener(OnCompleteAllInputListener listener) {
        mListener = listener;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        if (count < 0) {
            return;
        }
        mCount = count;
        initializeInputList();
        setBlockViews(getContext());
    }

    public int getBlockSideLength() {
        return mBlockSideLength;
    }

    public void setBlockSideLength(int blockSideLength) {
        if (mBlockSideLength < 0) {
            return;
        }
        mBlockSideLength = blockSideLength;
    }

    public int getMargin() {
        return mMargin;
    }

    public void setMargin(int margin) {
        mMargin = margin;
        setBlockMargin(margin, margin, margin, margin);
    }

    public int getMarginLeft() {
        return mMarginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        mMarginLeft = marginLeft;
        setBlockMargin(mMarginLeft, getMarginTop(), getPaddingRight(), getMarginBottom());
    }

    public int getMarginRight() {
        return mMarginRight;
    }

    public void setMarginRight(int marginRight) {
        mMarginRight = marginRight;
        setBlockMargin(getMarginLeft(), getMarginTop(), mMarginRight, getMarginBottom());
    }

    public int getMarginTop() {
        return mMarginTop;
    }

    public void setMarginTop(int marginTop) {
        mMarginTop = marginTop;
        setBlockMargin(getMarginLeft(), mMarginTop, getPaddingRight(), getMarginBottom());
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        mMarginBottom = marginBottom;
        setBlockMargin(getMarginLeft(), getMarginTop(), getPaddingRight(), mMarginBottom);
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        if (textSize < 0) {
            return;
        }
        mTextSize = textSize;
        boolean isMeasured = false;
        for (AppCompatEditText appCompatEditText : mBlockList) {
            isMeasured = setBlockPadding(isMeasured, appCompatEditText);
            appCompatEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        }
    }

    /**
     * 设置每个输入框所占的平均宽度，并根据宽度，设置其横向外边距值
     *
     * @param itemWidth 输入框宽度
     */
    public void setItemWidth(int itemWidth) {
        if (itemWidth < mBlockList.get(0).getLayoutParams().width) {
            return;
        }
        int marginHorizontal = (itemWidth - mBlockList.get(0).getLayoutParams().width) / 2;
        for (AppCompatEditText appCompatEditText : mBlockList) {
            ((LayoutParams) appCompatEditText.getLayoutParams()).setMargins(marginHorizontal,
                    mMarginTop, marginHorizontal, mMarginBottom);
        }
    }

    public List<String> getInputList() {
        if (mInputList.size() > 0) {
            mInputList.clear();
        }
        for (AppCompatEditText appCompatEditText : mBlockList) {
            mInputList.add(appCompatEditText.getText().toString().trim());
        }
        return mInputList;
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        return mFocusChangeListener;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        if (onFocusChangeListener == null) {
            return;
        }
        mFocusChangeListener = onFocusChangeListener;
        for (AppCompatEditText appCompatEditText : mBlockList) {
            appCompatEditText.setOnFocusChangeListener(mFocusChangeListener);
        }
    }

    public TextWatcher getTextWatcher() {
        return mTextWatcher;
    }

    public void setTextWatcher(TextWatcher textWatcher) {
        if (textWatcher == null) {
            return;
        }
        for (AppCompatEditText appCompatEditText : mBlockList) {
            appCompatEditText.removeTextChangedListener(mTextWatcher);
            appCompatEditText.addTextChangedListener(textWatcher);
        }
        mTextWatcher = textWatcher;
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        //让容器先获取焦点，避免已进入界面时，就有EditText获取到焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        initAttributes(context, attrs);
        initializeInputList();
        mFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    vFocused = v;
                    /**
                     * 当有多个EditText时，选中其中一个EditText，会先发生上上个获取过焦点的EditText，
                     * 获取到焦点又失去焦点的争抢事件，所以要判断是被按下去的那个EditText已经有字符时，
                     * 才去清空，否则上上个EditText中的内容也会被清空
                     */
                    if (((AppCompatEditText) v).getText().length() >= 1 && v.isPressed()) {
                        ((AppCompatEditText) v).setText("");
                        v.requestFocus();
                    }
                }
            }
        };
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || vFocused == null) {
                    return;
                }
                int index = mBlockList.indexOf(vFocused);
                //如果是删除，则自动前进到前一个对话框
                if (s.length() == 0) {
                    goForward(index);
                }
                //如果是输入则自动往后一个对话框
                if (s.length() > 0) {
                    goBack(s, index);
                }
            }
        };
        setBlockViews(context);
    }

    /**
     * 焦点向前移动
     *
     * @param index 当前焦点处于第几个输入框
     */
    private void goForward(int index) {
        if (index > 0) {
            AppCompatEditText appCompatEditText = mBlockList.get(index - 1);
            appCompatEditText.requestFocus();
            //如果是从任意一个有值的输入框（A）开始输入到最后，再倒退删除，当光标前进到
            //A输入框时，光标会停留在字符的前面，所以要手动将光标进行定位
            appCompatEditText.setSelection(appCompatEditText.getText().length());
        }
    }

    /**
     * 焦点向后移动
     *
     * @param s     输入框改变后的内容
     * @param index 当前焦点处于第几个输入框
     */
    private void goBack(Editable s, int index) {
        //当用户直接任意选取一个有值得输入框时，注意s是指已经更改后的文本
        if (s.length() > 1) {
            mBlockList.get(index).setText(s.subSequence(s.length() - 1, s.length()));
        }
        mInputList.set(index, s.toString());
        if (index < mBlockList.size() - 1) {
            mBlockList.get(index + 1).setText("");
            mBlockList.get(index + 1).requestFocus();
        } else if (index == mBlockList.size() - 1) {
            callOnCompleteAllInput();
        }
    }

    /**
     * 当所有输入当完成时回调
     */
    private void callOnCompleteAllInput() {
        if (mListener != null) {
            boolean isAllInputted = true;
            for (AppCompatEditText appCompatEditText : mBlockList) {
                if (TextUtils.isEmpty(appCompatEditText.getText().toString().trim())) {
                    isAllInputted = false;
                    break;
                }
            }
            if (isAllInputted) {
                mListener.onCompleteAllInput(mInputList);
            }
        }
    }

    /**
     * 初始化输入集合
     */
    private void initializeInputList() {
        if (mInputList == null) {
            mInputList = new ArrayList<>(mCount);
        } else {
            mInputList.clear();
        }
        for (int i = 0; i < mCount; i++) {
            mInputList.add("");
        }
    }

    /**
     * 设置方块输入框
     *
     * @param context
     */
    private void setBlockViews(Context context) {
        if (mCount <= 0) {
            return;
        }
        resetBlockList();
        boolean isMeasured = false;
        for (int i = 0; i < mCount; i++) {
            AppCompatEditText appCompatEditText = new AppCompatEditText(context);
            appCompatEditText.setBackgroundResource(R.drawable.et_block_shape);
            appCompatEditText.setGravity(Gravity.CENTER);
            appCompatEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
            if (i == mCount - 1) {
                appCompatEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            }
            LayoutParams lpEditText = new LayoutParams(mBlockSideLength, mBlockSideLength);
            appCompatEditText.setLayoutParams(lpEditText);
            int padding = (int) (mBlockSideLength * 0.1);
            appCompatEditText.setPadding(padding, padding, padding, padding);
            isMeasured = setBlockPadding(isMeasured, appCompatEditText);
            if (mMargin > 0) {
                lpEditText.setMargins(mMargin, mMargin, mMargin, mMargin);
            } else {
                lpEditText.setMargins(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);
            }
            appCompatEditText.setOnFocusChangeListener(mFocusChangeListener);
            appCompatEditText.addTextChangedListener(mTextWatcher);
            mBlockList.add(appCompatEditText);
            addView(appCompatEditText);
        }
    }

    /**
     * 设置方块边距与内边距
     *
     * @param isMeasured        方块内文字高度是否已经测量过
     * @param appCompatEditText 输入框控件
     * @return 返回测量结果
     */
    private boolean setBlockPadding(boolean isMeasured, AppCompatEditText appCompatEditText) {
        if (!isMeasured) {
            Paint paint = new Paint();
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize,
                    getResources().getDisplayMetrics()));
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top;
            if (mBlockSideLength < textHeight) {
                mBlockSideLength = (int) textHeight;
            }
            isMeasured = true;
        }
        if (isMeasured) {
            appCompatEditText.getLayoutParams().width = mBlockSideLength;
            appCompatEditText.getLayoutParams().height = mBlockSideLength;
            int padding = (int) (mBlockSideLength * 0.1);
            appCompatEditText.setPadding(padding, padding, padding, padding);
        }
        return isMeasured;
    }

    /**
     * 初始化属性资源
     *
     * @param context
     * @param attrs
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlockEditTextViewGroup);
        if (a != null) {
            mCount = a.getInteger(R.styleable.BlockEditTextViewGroup_blockCount, 0);
            mBlockSideLength = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockSideLength, 10);
            mMargin = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockMargin, 0);
            mMarginLeft = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockMarginLeft, 0);
            mMarginRight = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockMarginRight, 0);
            mMarginTop = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockMarginTop, 0);
            mMarginBottom = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockMarginBottom, 0);
            mTextSize = a.getDimensionPixelSize(R.styleable.BlockEditTextViewGroup_blockTextSize, 20);
            a.recycle();
        }
    }

    /**
     * 设置输入框的边距
     *
     * @param marginLeft
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     */
    private void setBlockMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        for (AppCompatEditText appCompatEditText : mBlockList) {
            LayoutParams lpEditText = (LayoutParams) appCompatEditText.getLayoutParams();
            lpEditText.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        }
    }

    /**
     * 重置方块集合并移除所有方块view
     */
    private void resetBlockList() {
        if (mBlockList == null) {
            mBlockList = new ArrayList<>();
        } else if (mBlockList.size() > 0) {
            //如果还添加有其他view，使用这种方式移除view
//            for (AppCompatEditText appCompatEditText: mBlockList){
//                removeView(appCompatEditText);
//            }
            removeAllViews();
            mBlockList.clear();
        }
    }

    /**
     * 完成所有输入监听器
     */
    public interface OnCompleteAllInputListener {

        /**
         * 当完成最后一个输入框
         *
         * @param list
         */
        void onCompleteAllInput(List<String> list);

    }

    /**
     * Builder设计模式
     */
    public static class Builder {

        private Context context;
        private int count;//方块个数
        private int blockSideLength;//方块边长
        private int margin, marginLeft, marginRight, marginTop, marginBottom;
        private int textSize;
        private OnFocusChangeListener onFocusChangeListener;
        private TextWatcher textWatcher;
        private OnCompleteAllInputListener onCompleteAllInputListener;
        private int itemWidth;

        public Builder(Context context) {
            this.context = context;
        }

        public BlockEditTextViewGroup build() {
            return new BlockEditTextViewGroup(this);
        }

        public Builder setCount(int count) {
            this.count = count;
            return this;
        }

        public Builder setBlockSideLength(int blockSideLength) {
            this.blockSideLength = blockSideLength;
            return this;
        }

        public Builder setMargin(int margin) {
            this.margin = margin;
            return this;
        }

        public Builder setMarginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder setMarginRight(int marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        public Builder setMarginTop(int marginTop) {
            this.marginTop = marginTop;
            return this;
        }

        public Builder setMarginBottom(int marginBottom) {
            this.marginBottom = marginBottom;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setItemWidth(int itemWidth){
            this.itemWidth = itemWidth;
            return this;
        }

        public Builder setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
            this.onFocusChangeListener = onFocusChangeListener;
            return this;
        }

        public Builder setTextWatcher(TextWatcher textWatcher) {
            this.textWatcher = textWatcher;
            return this;
        }

        public Builder setOnCompleteAllInputListener(OnCompleteAllInputListener onCompleteAllInputListener) {
            this.onCompleteAllInputListener = onCompleteAllInputListener;
            return this;
        }

    }

}
