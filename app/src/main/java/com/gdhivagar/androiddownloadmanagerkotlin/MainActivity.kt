package com.gdhivagar.androiddownloadmanagerkotlin

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.gdhivagar.androiddownloadmanagerkotlin.databinding.ActivityMainBinding
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var downloadReference: Long? = null
    private lateinit var receiver: BroadcastReceiver
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDownload.setOnClickListener {
            downloadFile("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
        }

        broadcastReceiver()

    }

    private fun downloadFile(file: String) {
        showToast("Downloading...")
        var url: URL? = null
        var name = ""
        try {
            url = URL(file)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        name = url!!.path
        name = name.substring(name.lastIndexOf('/') + 1)
        val uri = Uri.parse(file)
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setMimeType("application/pdf")
        request.allowScanningByMediaScanner()
        request.setAllowedOverMetered(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            this@MainActivity,
            Environment.DIRECTORY_DOCUMENTS,
            name
        )
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadReference = downloadManager.enqueue(request)
    }

    private fun broadcastReceiver() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val action = p1!!.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val requestQuery = DownloadManager.Query()
                    requestQuery.setFilterById(downloadReference!!)
                    val cursor = downloadManager.query(requestQuery)
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                            showToast("Download successfully!!!")
                        } else if (DownloadManager.STATUS_FAILED == cursor.getInt(columnIndex)) {
                            showToast("Download failed")
                        }
                    }
                }
            }
        }
    }

    private fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

}