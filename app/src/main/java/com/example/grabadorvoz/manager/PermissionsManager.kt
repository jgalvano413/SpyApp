package com.ade.accessControl.manager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.grabadorvoz.activity.MainActivity
import com.galvancorp.spyapp.R

class PermissionsManager(private val activity: Activity) {

    private val _alert = MutableLiveData<Boolean>()
    val alert: LiveData<Boolean> get() = _alert
    private val PERMISSION_REQUEST_CODE = 123
    private val REQUEST_BLUETOOTH_PERMISSIONS = 124
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    // Verifica si todos los permisos están concedidos
    fun arePermissionsGranted(): Boolean {
        // Verifica permisos generales
        val generalPermissionsGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
        Log.d("Permisos",generalPermissionsGranted.toString())
        return generalPermissionsGranted
    }

    // Solicita permisos si es necesario
    fun checkAndRequestPermissions() {
        val permissionsNeeded = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        val bluetoothPermissionsNeeded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            ).filter {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }
        } else {
            emptyList()
        }

        if (permissionsNeeded.isNotEmpty() || bluetoothPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                (permissionsNeeded + bluetoothPermissionsNeeded).toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun handlePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    println("${permissions[i]} granted")
                } else {
                    deniedPermissions.add(permissions[i])
                    println("${permissions[i]} denied")
                }
            }

            if (deniedPermissions.isNotEmpty()) {
                _alert.postValue(true)
                val alerta = AlertDialog.Builder(activity)
                alerta.setTitle("ADVERTENCIA")
                    .setMessage(activity.getString(R.string.permiss))
                    .setPositiveButton("OK") { dialog, _ -> activity.finish() }
                alerta.show()
            } else {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
}
