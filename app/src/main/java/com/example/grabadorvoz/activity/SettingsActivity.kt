package com.example.grabadorvoz.activity

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.grabadorvoz.GlobalConfigurations.GlobalConfiguration.NOTIFICATION
import com.example.grabadorvoz.manager.managerData
import com.galvancorp.spyapp.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var manager: managerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        Init()
    }

    private fun Init(){
        manager = managerData(this)
        ActionButton()
        setCheckbox()
    }

    private fun ActionButton(){
        findViewById<View>(R.id.backSettings).setOnClickListener(View.OnClickListener { onBackPressed() })
    }

    private fun setCheckbox(){
        findViewById<CheckBox>(R.id.checkboxNotification).isChecked = manager.getBooleanNotidication(NOTIFICATION)
        findViewById<CheckBox>(R.id.checkboxNotification).setOnCheckedChangeListener { _, isChecked ->
            manager.saveBoolean(NOTIFICATION,isChecked)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}