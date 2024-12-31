package com.example.grabadorvoz.widgets.WidgetProvider

import android.app.ActivityManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.IS_SERVICE
import com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.TAKE_PHOTO
import com.example.grabadorvoz.Service.GrabacionService
import com.example.grabadorvoz.Service.photoService
import com.example.grabadorvoz.Service.videoRecording
import com.example.grabadorvoz.manager.managerData
import com.example.grabadorvoz.widgets.WidgetProvider.WidgetProvider.Companion
import com.example.pruebaremoto.widgets.toast.showToast
import com.galvancorp.spyapp.R

class updateWidget (val context: Context) {

    private val manager = managerData(context)

    fun toggleService(context: Context, serviceClass: Class<*>) {
        val isServiceRunning = isRunningService(serviceClass, context)
        val isServiceEnabled = manager.getBoolean(IS_SERVICE)
        when {
            !isServiceRunning && !isServiceEnabled -> {
                context.startService(Intent(context, serviceClass))
                changeLayout(context, serviceClass,true)
            }
            isServiceRunning && isServiceEnabled -> {
                context.stopService(Intent(context, serviceClass))
                changeLayout(context, serviceClass,false)
            }
            else -> {
                showToast(context, context.getString(R.string.serviceRuning), false)
            }
        }
    }

    fun changeLayout(context: Context, serviceClass: Class<*>, type: Boolean) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, R.layout.layout_widget_homescreen)

        when (serviceClass.name) {
            GrabacionService::class.java.name -> {
                if (type) {
                    setAudiolayout(views, context)
                } else {
                    setLayout(views, context)
                }
            }

            videoRecording::class.java.name -> {
                if (type) {
                    setVideolayout(views, context)
                } else {
                    setLayout(views, context)
                }
            }
            photoService::class.java.name -> {
                manager.saveBoolean(TAKE_PHOTO,true)
            }

            else -> Log.e("ChangeLayout", "Unknown service: ${serviceClass.name}")
        }

        val appWidgetIds = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }

    fun setAudiolayout(views: RemoteViews, context: Context){
        views.setViewVisibility(R.id.camaraWidget, View.GONE)
        views.setViewVisibility(R.id.videoWidget, View.GONE)
        views.setViewVisibility(R.id.lineOne, View.GONE)
        views.setViewVisibility(R.id.lineTow, View.GONE)
        views.setViewVisibility(R.id.audioWidget, View.VISIBLE)
        views.setImageViewResource(R.id.audioImage, R.drawable.baseline_stop_24)
        views.setTextViewText(R.id.audioView,context.getString(R.string.stopWidget))
    }

    fun setVideolayout(views: RemoteViews, context: Context){
        views.setViewVisibility(R.id.lineOne, View.GONE)
        views.setViewVisibility(R.id.lineTow, View.GONE)
        views.setViewVisibility(R.id.camaraWidget, View.GONE)
        views.setViewVisibility(R.id.audioWidget, View.GONE)
        views.setViewVisibility(R.id.videoWidget, View.VISIBLE)
        views.setImageViewResource(R.id.videoView, R.drawable.baseline_stop_24)
        views.setTextViewText(R.id.videoTest,context.getString(R.string.stopWidget))
    }

    fun setLayout(views: RemoteViews, context: Context){
        views.setViewVisibility(R.id.camaraWidget, View.VISIBLE)
        views.setViewVisibility(R.id.videoWidget, View.VISIBLE)
        views.setViewVisibility(R.id.audioWidget, View.VISIBLE)
        views.setViewVisibility(R.id.lineOne, View.VISIBLE)
        views.setViewVisibility(R.id.lineTow, View.VISIBLE)
        views.setImageViewResource(R.id.audioImage, R.drawable.baseline_volume_up_24_white)
        views.setTextViewText(R.id.audioView,context.getString(R.string.audio))
        views.setImageViewResource(R.id.camaraview, R.drawable.baseline_camera_alt_24_white)
        views.setTextViewText(R.id.camaratext,context.getString(R.string.camara))
        views.setImageViewResource(R.id.videoView, R.drawable.baseline_videocam_24)
        views.setTextViewText(R.id.videoTest,context.getString(R.string.video))
    }

    private fun isRunningService(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any { it.service.className == serviceClass.name }
    }
}