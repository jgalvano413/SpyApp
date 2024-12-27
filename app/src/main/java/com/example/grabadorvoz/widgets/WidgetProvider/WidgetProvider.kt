package com.example.grabadorvoz.widgets.WidgetProvider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.galvancorp.spyapp.R

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widget_text, "¡Actualizado!")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
