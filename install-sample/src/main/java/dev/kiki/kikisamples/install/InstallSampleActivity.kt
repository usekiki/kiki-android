package dev.kiki.kikisamples.install

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dev.kiki.common.UpdateInfo
import dev.kiki.install.AppInstallManager
import dev.kiki.install.InstallManager
import dev.kiki.install.UpdateStateListener
import dev.kiki.install.UpdateStatus
import dev.kiki.install.model.UpdateInfoResult
import java.util.Locale

class InstallSampleActivity : AppCompatActivity() {

    private var _snackBar: Snackbar? = null
    private val snackBar: Snackbar get() = requireNotNull(_snackBar)

    private val installManager: InstallManager = AppInstallManager.apply {
        // If your default lang is not english, use this to select the right locale.
        // otherwise the sdk will select the right locale from your app locale
        setLocale(Locale("fa"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_install)
        val rootView = findViewById<View>(R.id.rootView)
        _snackBar = Snackbar.make(rootView, R.string.ui_button, Snackbar.LENGTH_INDEFINITE)
        findViewById<Button>(R.id.installWithUi).setOnClickListener {
            handleInstallWithUi()
        }
        findViewById<Button>(R.id.installWithCustomUi).setOnClickListener {
            handleInstallWithCustomUi()
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

    private var _updateInfo: UpdateInfo? = null
    private fun handleInstallWithCustomUi() {
        installManager.checkUpdateInfo {
            onSuccess { updateInfo ->
                _updateInfo = updateInfo
                if (updateInfo.isUpdateAvailable(this@InstallSampleActivity)) {
                    installManager.startUpdateFlow(updateInfo, shouldHideUi = true)
                    snackBar
                        .setText(getString(R.string.download_progress_place_holder, 0f))
                        .show()
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
        installManager.registerListener(listener)
    }

    private val listener = object : UpdateStateListener {
        override fun onDownloadProgressChanged(
            totalBytesToDownload: Long,
            bytesDownloaded: Long
        ) {
            val progress: Long = 100 * bytesDownloaded / totalBytesToDownload
            snackBar.setText(getString(R.string.download_progress_place_holder, progress.toFloat()))
        }

        override fun onGetUpdateInfo(result: UpdateInfoResult) {}

        override fun onUpdateErrorHappened(errorCode: Int) {}

        override fun onUpdateStatusChanged(updateStatus: UpdateStatus) {
            when (updateStatus) {
                UpdateStatus.DOWNLOADING -> snackBar.show()
                UpdateStatus.CHECKING,
                UpdateStatus.DOWNLOADED -> {
                    snackBar.setText(getString(R.string.install_now)).setAction(
                        R.string.install
                    ) { installManager.installIfDownloadCompleted(_updateInfo!!) }
                }
                UpdateStatus.FAILED -> {
                    snackBar.duration = Snackbar.LENGTH_SHORT
                    snackBar.setText(R.string.update_failed)
                }
                else -> snackBar.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        installManager.unregisterListener(listener)
    }

    companion object {

        private const val TAG = "INSTALL"
    }
}