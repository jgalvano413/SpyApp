package com.example.grabadorvoz.widgets.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.galvancorp.spyapp.R


fun showAlertDialogcustom(
    context: Context,
    title: String,
    message: String,
    positiveListener: DialogInterface.OnClickListener,
    negativeListener: DialogInterface.OnClickListener,
    messageOK:String = "ACEPTAR",
    messageCancel: String = "CANCELAR"
): AlertDialog {
    val builder = AlertDialog.Builder(context)
    val inflater = LayoutInflater.from(context)

    // Inflar el diseño del AlertDialog personalizado
    val dialogView = inflater.inflate(R.layout.dialog_custom_alert, null)
    builder.setView(dialogView)

    val titleView = dialogView.findViewById<TextView>(R.id.tvTitle)
    titleView.text = title

    val messageView = dialogView.findViewById<TextView>(R.id.tvMessage)
    messageView.text = message


    val dialog = builder.create()

    // Asignar listeners a los botones
    val btnOK = dialogView.findViewById<Button>(R.id.btnConfirmDialog)
    btnOK.setText(messageOK)
    btnOK.setOnClickListener {
        positiveListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
        dialog.dismiss()
    }

    val btnCancel = dialogView.findViewById<Button>(R.id.btnCanceldialog)
    btnCancel.setText(messageCancel)
    btnCancel.setOnClickListener {
        negativeListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
        dialog.dismiss()
    }

    // Evitar que el usuario pueda cancelar el diálogo con el botón 'atrás'
    builder.setCancelable(false)
    dialog.setCanceledOnTouchOutside(false)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    return dialog
}
