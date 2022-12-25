package dev.kiki.kikisamples

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dev.kiki.update.AppUpdateManager
import dev.kiki.update.UpdateManager
import java.util.Locale

class UpdateSampleActivity : AppCompatActivity() {

    private val updateManager: UpdateManager = AppUpdateManager.apply {
        // If your default lang is not english, use this to select the right locale.
        // otherwise the sdk will select the right locale from your app locale
        // setLocale(Locale("fa"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.updateWithUi).setOnClickListener {
            handleUpdateWithUi()
        }
        findViewById<Button>(R.id.updateWithOutUi).setOnClickListener {
            handleUpdateWithoutUi()
        }
    }

    private fun handleUpdateWithUi() {
        updateManager.checkUpdateInfo {
            onSuccess { updateInfo ->
                if (updateInfo.isUpdateAvailable(this@UpdateSampleActivity)) {
                    updateManager.startUpdateFlow()
                } else {
                    Log.w(
                        TAG,
                        "No Update Available! latest available version = ${updateInfo.availableVersionCode}"
                    )
                }
            }
            onFailed { throwable ->
                Log.w(TAG, "Error Happened in getting update info -> $throwable")
            }
        }
    }

    private fun handleUpdateWithoutUi() {
        updateManager.checkUpdateInfo {
            onSuccess { updateInfo ->
                if (updateInfo.isUpdateAvailable(this@UpdateSampleActivity)) {
                    showMyUpdateCustomUi(updateInfo.link)
                } else {
                    Log.w(
                        TAG,
                        "No Update Available! latest available version = ${updateInfo.availableVersionCode}"
                    )
                }
            }
            onFailed { throwable ->
                Log.w(TAG, "Error Happened in getting update info -> $throwable")
            }
        }
    }

    private fun showMyUpdateCustomUi(link: String) {
        val rootView = findViewById<View>(R.id.rootView)
        Snackbar.make(rootView, R.string.new_update_available, Snackbar.LENGTH_LONG)
            .setAction(R.string.update) { openBrowser(link) }.show()
    }

    private fun openBrowser(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_app_to_handle, Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        private const val TAG = "UPDATE"
    }
}