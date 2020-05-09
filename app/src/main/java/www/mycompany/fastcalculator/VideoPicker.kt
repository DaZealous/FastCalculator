package www.mycompany.fastcalculator

import adapter.VideoPickerAdapter
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.AppDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import utils.Config
import utils.LiveVideoPickerData
import java.io.File
import db.VideosEntity

class VideoPicker : AppCompatActivity(), LiveVideoPickerData {

    var videoFiles = ArrayList<String>()
    var videoList = ArrayList<String>()
    lateinit var recyclerView: RecyclerView
    lateinit var textCount: TextView
    lateinit var btnDone: ImageButton

    private var compositeDisposable = CompositeDisposable()
    lateinit var adapter: VideoPickerAdapter
    private lateinit var db: AppDatabase

    var status = 0
    lateinit var dialog: Dialog
    lateinit var progressBar: ProgressBar
    lateinit var textProgress: TextView
    lateinit var observable: Observable<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_picker)

        recyclerView = findViewById(R.id.pick_video_select_image_recycler_view)
        textCount = findViewById(R.id.pick_video_select_image_count)
        btnDone = findViewById(R.id.pick_video_select_image_done)

        btnDone.alpha = 0.4f

        db = AppDatabase(this)
        videoFiles = loadVideos()

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.hasFixedSize()

        textCount.text = "0/5"

        adapter = VideoPickerAdapter(this, videoFiles, this)

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

        btnDone.setOnClickListener {
            if (videoList.size != 0)
                moveFile()
        }

        loadVideos()
    }

    private fun loadVideos(): ArrayList<String> {
        val list = ArrayList<String>()
        val cursor: Cursor?
        try {
            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Video.VideoColumns.DATA)
            cursor = contentResolver.query(uri, projection, null, null, null)

            while (cursor!!.moveToNext()) {
                list.add(cursor.getString(0))
            }

            cursor.close()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return list
    }

    override fun isChosen(video: String): Boolean = videoList.contains(video)

    override fun setVideo(video: String) {
        videoList.add(video)
        btnDone.alpha = 1f
        textCount.text = "${videoList.size}/5"
    }

    override fun removeVideo(video: String) {
        videoList.remove(video)
        if (videoList.size == 0) {
            btnDone.alpha = 0.4f
            textCount.text = "0/5"
        } else {
            btnDone.alpha = 1f
            textCount.text = "${videoList.size}/5"
        }
    }

    override fun getVideos(): ArrayList<String> = videoList

    private fun moveFile() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.progress_dialog_layout)

        progressBar = dialog.findViewById(R.id.progress_horizontal)
        textProgress = dialog.findViewById(R.id.text_progress)

        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        observable = Observable.fromIterable<String>(videoList)
            .subscribeOn(Schedulers.io())
            .filter {
                Thread.sleep(1000)
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<String> {

            override fun onNext(t: String) {
                status += 1
                progressBar.progress = status
                textProgress.text = "$status/${videoList?.size}"
                doMove(File(t))
                videoFiles.remove(t)
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                status = 0
                compositeDisposable.clear()
                dialog.dismiss()
                videoList.clear()
                btnDone.alpha = 0.4f
                textCount.text = "0/5"
                adapter.notifyDataSetChanged()
            }
        })

        dialog.show()
    }

    private fun doMove(file: File) {
        try {
            val fileDest = File(Config.VideoPath)
            if (!fileDest.exists())
                fileDest.mkdir()
            FileUtils.copyFileToDirectory(file, fileDest)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
            FileUtils.forceDelete(file)
            sendBroadcast(intent)
            saveToDatabase(file.absolutePath, File(fileDest.absolutePath, file.name).absolutePath)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToDatabase(absolutePath: String, temporalPath: String) {
        try {
            GlobalScope.launch {
                db.videosDao().addVideo(VideosEntity(absolutePath, temporalPath))
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}


