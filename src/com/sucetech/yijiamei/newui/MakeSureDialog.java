package com.sucetech.yijiamei.newui;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.sucetech.yijiamei.R;

/**
 * Created by lihh on 2018/11/12.
 */

public class MakeSureDialog extends DialogFragment {
    private View mView;
    private TextView mTvContent;
    private String content = "您确认执行此操作吗？";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mView = inflater.inflate(R.layout.dialog_make_sure, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
        return mView;
    }

    private void initView() {
        TextView mTvSure = (TextView) mView.findViewById(R.id.tv_sure);
        TextView mTvCancel = (TextView) mView.findViewById(R.id.tv_cancel);
        mTvContent = (TextView) mView.findViewById(R.id.tv_dialog_make_sure_content);
        mTvContent.setText(content);
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSureClick();
                }
                dismiss();
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancelClick();
                }
                dismiss();
            }
        });
    }

    public void setContent(String content) {
        this.content = content;

    }

    public interface onDialogClickListener {
        public void onSureClick();

        public void onCancelClick();
    }

    private onDialogClickListener mListener;

    public void setDialogClickListener(onDialogClickListener mListener) {
        this.mListener = mListener;
    }
}
