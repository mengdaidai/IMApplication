package com.daidai.im.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daidai.im.R;

/**
 * Created by songs on 2016/1/29.
 */
public class RecordDialog {
    RelativeLayout mExitLayout,mRecordLayout;
    Dialog mDialog;
    VolumnViewer mVolumnViewer;
    Context context ;
    public RecordDialog(Context context){
        mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.record_dialog);
        this.context = context;
        mExitLayout = (RelativeLayout)mDialog.findViewById(R.id.dialog_exit);
        mRecordLayout = (RelativeLayout)mDialog.findViewById(R.id.dialog_record);
        mVolumnViewer = (VolumnViewer)mDialog.findViewById(R.id.volumnViewer);
    }


    public void show(){
        mDialog.show();
    }

    public void showExit(){
        mExitLayout.setVisibility(View.VISIBLE);
        mRecordLayout.setVisibility(View.GONE);
    }

    public void showRecord(){
        mExitLayout.setVisibility(View.GONE);
        mRecordLayout.setVisibility(View.VISIBLE);
    }

    public void updateViewer(int volumn){
        mVolumnViewer.setmVolumeValue(volumn);
    }

    public void cancel(){
        mDialog.dismiss();
        mVolumnViewer.stopRefresh();
    }
}
