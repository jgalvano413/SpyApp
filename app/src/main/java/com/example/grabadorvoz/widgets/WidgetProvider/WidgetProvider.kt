package com.example.grabadorvoz.widgets.WidgetProvider

import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.IS_SERVICE
import com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.WIDGET_TYPE
import com.example.grabadorvoz.Service.GrabacionService
import com.example.grabadorvoz.Service.photoService
import com.example.grabadorvoz.Service.videoRecording
import com.example.grabadorvoz.manager.managerData
import com.example.pruebaremoto.widgets.toast.showToast
import com.galvancorp.spyapp.R

class WidgetProvider : AppWidgetProvider() {

    companion object {
        private lateinit var updateWidget: updateWidget
        private lateinit var managerData: managerData
        private const val ACTION_TOGGLE_SERVICE = "com.galvancorp.TOGGLE_SERVICE"
        private const val AUDIO_SERVICE = "com.galvancorp.AUDIO_SERVICE"
        private const val VIDEO_TOGGLE_SERVICE = "com.galvancorp.VIDEO_TOGGLE_SERVICE"
        private const val PHOTO_TOGGLE_SERVICE = "com.galvancorp.PHOTO_TOGGLE_SERVICE"

    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget = updateWidget(context)
            val views = RemoteViews(context.packageName, R.layout.layout_widget_homescreen)
            views.setOnClickPendingIntent(R.id.audioWidget,getpendingIntent(context,appWidgetId, AUDIO_SERVICE))
            views.setOnClickPendingIntent(R.id.camaraWidget,getpendingIntent(context,appWidgetId, PHOTO_TOGGLE_SERVICE))
            views.setOnClickPendingIntent(R.id.videoWidget,getpendingIntent(context,appWidgetId, VIDEO_TOGGLE_SERVICE))
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    fun getIntent(context:Context,appWidgetId:Int, widgetType: String) : Intent {
        return  Intent(context, WidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_SERVICE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(WIDGET_TYPE, widgetType)
        }
    }

    fun getpendingIntent(context:Context,appWidgetId:Int,widgetType: String) : PendingIntent {
        return  PendingIntent.getBroadcast(
            context,
            (appWidgetId * 31) + widgetType.hashCode(),
            getIntent(context, appWidgetId,widgetType),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        managerData = managerData(context)
        if (intent.action == ACTION_TOGGLE_SERVICE) {
            val widgetType = intent.getStringExtra("WIDGET_TYPE")
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId == -1) {
                showToast(context, context.getString(R.string.widgetNULL), false)
                return
            }
            Log.d("WidgetProvider", "Action: $ACTION_TOGGLE_SERVICE, WidgetType: $widgetType, AppWidgetId: $appWidgetId")
            when (widgetType) {
                AUDIO_SERVICE -> updateWidget.toggleService(context, GrabacionService::class.java)
                VIDEO_TOGGLE_SERVICE -> updateWidget.toggleService(context, videoRecording::class.java)
                PHOTO_TOGGLE_SERVICE -> updateWidget.toggleService(context, photoService::class.java)
                else -> showToast(context, context.getString(R.string.widgetNULL), false)
            }
        }
    }



}
