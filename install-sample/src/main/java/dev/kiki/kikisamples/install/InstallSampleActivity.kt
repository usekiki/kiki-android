package dev.kiki.kikisamples.install

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.kiki.install.AppInstallManager
import dev.kiki.install.InstallManager

class InstallSampleActivity : AppCompatActivity() {

    private val installManager: InstallManager = AppInstallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_install)
        findViewById<Button>(R.id.installWithUi).setOnClickListener {
            handleInstallWithUi()
        }
    }

    private fun handleInstallWithUi() {
        installManager.checkUpdateInfo {
            onSuccess { updateInfo ->
                if (updateInfo.isUpdateAvailable(this@InstallSampleActivity)) {
                    installManager.startUpdateFlow(updateInfo)
                } else {
                    Log.w(
                        TAG,
                        "No Update Available! latest available version = " +
                                "${updateInfo.availableVersionCode}"
                    )
                }
            }
            onFailed { throwable ->
                Log.w(TAG, "Error Happened in getting update info -> $throwable")
            }
        }
    }

    companion object {

        private const val TAG = "INSTALL"
    }
}