package com.cip.cipstudio.view.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.cip.cipstudio.R

object LoadingSpinner {

    private lateinit var loadingDialog : Dialog

    fun showLoadingDialog(context : Context){
        loadingDialog = Dialog(context)
        loadingDialog.setCancelable(false)
        loadingDialog.setTitle("Loading...")
        loadingDialog.setContentView(R.layout.loading_spinner)
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.show()
    }

    fun dismiss(){
        if(loadingDialog != null){
            loadingDialog.dismiss()
        }
    }

}