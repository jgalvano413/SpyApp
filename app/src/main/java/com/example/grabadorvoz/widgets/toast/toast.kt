package com.example.pruebaremoto.widgets.toast

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.galvancorp.spyapp.R
import de.hdodenhof.circleimageview.CircleImageView

fun showToast(context: Context, message: String, info:Boolean) {
    // Infla el diseño personalizado para el Toast
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.custom_toast, null)

    // Encuentra y configura el mensaje
    var messageTextView = layout.findViewById<TextView>(R.id.infoViewtoast)
    messageTextView.text = message

    // Configura el icono (opcional)
    val icon = layout.findViewById<CircleImageView>(R.id.imageView2)
    if (info) icon.setImageResource(R.drawable.logo_app)
    else icon.setImageResource(R.drawable.error_toast)
    // Configura el Toast
    val toast = Toast(context)
    toast.duration = Toast.LENGTH_LONG
    toast.view = layout
    toast.show()
}
