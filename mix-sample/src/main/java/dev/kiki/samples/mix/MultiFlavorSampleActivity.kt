package dev.kiki.samples.mix

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.kiki.common.UpdateInfo
import dev.kiki.install.AppInstallManager
import dev.kiki.install.InstallManager
import dev.kiki.install.UpdateStateListener
import dev.kiki.install.UpdateStatus
import dev.kiki.install.model.UpdateInfoResult
import dev.kiki.install.model.fold
import dev.kiki.samples.mix.databinding.ActivityMainBinding
import dev.kiki.update.AppUpdateManager
import dev.kiki.update.UpdateManager
import java.util.Locale

class MultiFlavorSampleActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val installManager: InstallManager = AppInstallManager
    private val updateManager: UpdateManager = AppUpdateManager

    private var updateLocale = "fa"
    private var installLocale = "en"

    private var _updateInfo: UpdateInfo? = null

    private val updateListener: UpdateStateListener = object : UpdateStateListener {
        override fun onDownloadProgressChanged(
            totalBytesToDownload: Long,
            bytesDownloaded: Long
        ) {
            Log.d(
                INSTALL_TAG,
                "onDownloadProgressChanged, totalBytesToDownload=$totalBytesToDownload," +
                        " bytesDownloaded=$bytesDownloaded"
            )
        }

        override fun onGetUpdateInfo(result: UpdateInfoResult) {
            result.fold(
                ifSuccess = {
                    Log.d(INSTALL_TAG, "onGetUpdateInfo succeed, result=$it")
                },
                ifFailure = {
                    Log.d(INSTALL_TAG, "onGetUpdateInfo failed, cause=$it")
                })
        }

        override fun onUpdateErrorHappened(errorCode: Int) {
            Log.d(INSTALL_TAG, "onUpdateErrorHappened, errorCode=$errorCode")
        }

        override fun onUpdateStatusChanged(updateStatus: UpdateStatus) {
            Log.d(INSTALL_TAG, "onInstallStateChanged, updateStatus=$updateStatus")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        handleUpdateSdk()
        binding?.apply {
            isInstallEnable.setOnCheckedChangeListener { _, isChecked ->
                _updateInfo = null
                if (isChecked) {
                    handleInstallSdk()
                } else {
                    handleUpdateSdk()
                }
            }
        }
    }

    private fun handleUpdateSdk() {
        binding?.apply {
            updateLocale = "en"
            startUpdateFlowImmediately.visibility = View.INVISIBLE
            startUpdateFlow.setOnClickListener {
                updateManager.startUpdateFlow()
            }
            changeLocale.setOnClickListener {
                updateLocale = if (updateLocale == "fa") {
                    "en"
                } else {
                    "fa"
                }
                Toast.makeText(
                    this@MultiFlavorSampleActivity,
                    "Locale Changed to $updateLocale (Update)",
                    Toast.LENGTH_LONG
                ).show()
                updateManager.setLocale(Locale(updateLocale))
            }

            getShareLink.setOnClickListener {
                onShareClicked(this@MultiFlavorSampleActivity, updateManager.getShareLink())
            }
            checkUpdate.setOnClickListener {
                updateManager.checkUpdateInfo {
                    onSuccess { updateInfo ->
                        _updateInfo = updateInfo
                        Toast.makeText(
                            this@MultiFlavorSampleActivity,
                            "Get update info succeed!",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(UPDATE_TAG, "Get update info succeed!, result=$updateInfo")
                    }
                    onFailed {
                        _updateInfo = null
                        Toast.makeText(
                            this@MultiFlavorSampleActivity,
                            "Get update info failed!",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(UPDATE_TAG, "Get update info failed!, cause=$it")
                    }
                }
            }
        }
    }

    @Suppress("LongMethod")
    private fun handleInstallSdk() {
        binding?.apply {
            installManager.setLocale(Locale("en"))
            startUpdateFlowImmediately.visibility = View.VISIBLE
            startUpdateFlow.setOnClickListener {
                installManager.startUpdateFlow()
            }
            getShareLink.setOnClickListener {
                onShareClicked(this@MultiFlavorSampleActivity, installManager.getShareLink())
            }
            changeLocale.setOnClickListener {
                installLocale = if (installLocale == "fa") {
                    "en"
                } else {
                    "fa"
                }
                installManager.setLocale(Locale(installLocale))
                Toast.makeText(
                    this@MultiFlavorSampleActivity,
                    "Locale Changed to $installLocale (Install)",
                    Toast.LENGTH_LONG
                ).show()
            }

            checkUpdate.setOnClickListener {
                installManager.checkUpdateInfo {
                    onSuccess { updateInfo ->
                        _updateInfo = updateInfo
                        Toast.makeText(
                            this@MultiFlavorSampleActivity,
                            "Get update info succeed!",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(INSTALL_TAG, "Get update info succeed!, result=$updateInfo")
                    }
                    onFailed {
                        _updateInfo = null
                        Toast.makeText(
                            this@MultiFlavorSampleActivity,
                            "Get update info failed!",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(INSTALL_TAG, "Get update info failed!, cause=$it")
                    }
                }
            }

            startUpdateFlowImmediately.setOnClickListener {
                if (_updateInfo != null) {
                    installManager.startUpdateFlow(
                        requireNotNull(_updateInfo)
                    )
                } else {
                    Toast.makeText(
                        this@MultiFlavorSampleActivity,
                        "Update info unavailable!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        installManager.unregisterListener(updateListener)
        binding = null
    }

    private fun onShareClicked(
        context: Context,
        shareMessage: String,
        shareSubject: String = context.getString(R.string.share_entity_title)
    ) {

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_SUBJECT, shareSubject)
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_entity_title)
            )
        )
    }

    companion object {

        private const val INSTALL_TAG = "INSTALL_DEBUG"
        private const val UPDATE_TAG = "UPDATE_DEBUG"
    }
}