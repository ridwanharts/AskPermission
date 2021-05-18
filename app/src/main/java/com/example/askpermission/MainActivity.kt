package com.example.askpermission

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_ALL = 100
        const val REQUEST_PERMISSION_STORAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PERMISSION_GRANTED
                || checkSelfPermission(READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED
                || checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_ALL
                )
            } else {
                Toast.makeText(this, "All permission granted", Toast.LENGTH_SHORT).show()
            }

            if(shouldShowRequestPermissionRationale(CAMERA)){
                Toast.makeText(this, "Permission camera is needed for take picture", Toast.LENGTH_SHORT).show()
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (checkPermissionIsExternalStorageManager()) {
                    Toast.makeText(this, "Permission storage manager granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    requestPermissionIsExternalStorageManager()
                }
            }

        } else {
            if (ActivityCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this,
                    WRITE_EXTERNAL_STORAGE
                ) != PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_ALL
                )
            } else {
                Toast.makeText(this, "All permission granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermissionIsExternalStorageManager(): Boolean {
        return Environment.isExternalStorageManager()
    }

    private fun requestPermissionIsExternalStorageManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, REQUEST_PERMISSION_STORAGE)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, REQUEST_PERMISSION_STORAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_ALL -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    for ((id, permission) in grantResults.withIndex()) {
                        if (permission != PERMISSION_GRANTED) {
                            Toast.makeText(
                                this,
                                "Permission ${permissions[id]} not granted !",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }
                        Toast.makeText(this, "All permission granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Must grant permission to use this app !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PERMISSION_STORAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        Toast.makeText(this, "Permission storage manager granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Must allow access to manage all files !", Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
        }
    }

}