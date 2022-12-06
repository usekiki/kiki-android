package dev.kiki.kikisamples

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.kiki.update.AppUpdateManager
import dev.kiki.update.UpdateManager

class MainActivity : AppCompatActivity() {

    private val updateManager: UpdateManager = AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.updateWithUi).setOnClickListener {
            handleUpdateWithUi()
        }
    }

    private fun handleUpdateWithUi() {
        updateManager.checkUpdateInfo {
            onSuccess { updateInfo ->
                if (updateInfo.isUpdateAvailable(this@MainActivity)) {
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

    companion object {

        private const val TAG = "UPDATE"
    }
}