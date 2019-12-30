package com.project.app.Helpers

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import com.project.app.R

class DialogFactory {

    companion object{
        fun createSystemDialogPositive(context: Context, content:String, header:String, positiveText: String, positiveCallback: DialogInterface.OnClickListener){
           val dialog= AlertDialog.Builder(context, R.style.AlertDialogTheme).setMessage(content).setTitle(header).setCancelable(true)
                .setPositiveButton(positiveText,positiveCallback)
                 .create()

            dialog.show()
        }
        fun createSystemDialog(context: Context, content:String, header:String, positiveText: String, positiveCallback: DialogInterface.OnClickListener, negativeText: String, negativeCallback: DialogInterface.OnClickListener){

           val dialog= AlertDialog.Builder(context,R.style.AlertDialogTheme).setMessage(content).setTitle(header).setCancelable(true)
                .setPositiveButton(positiveText,positiveCallback)
                .setNegativeButton(negativeText,negativeCallback)
                .create()
            dialog.show()

        }
    }
}
