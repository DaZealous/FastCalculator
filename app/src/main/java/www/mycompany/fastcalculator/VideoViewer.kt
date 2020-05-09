package www.mycompany.fastcalculator

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.AppDatabase
import db.VideosEntity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import adapter.VideoFilesAdapter
import android.annotation.SuppressLint
import utils.LiveVideoData

class VideoViewer : AppCompatActivity(), LiveVideoData {

    lateinit var recyclerView: RecyclerView
    lateinit var textCount: TextView
    lateinit var btnDone: ImageButton

    private lateinit var db: AppDatabase
    private lateinit var videoFiles : ArrayList<VideosEntity>

    lateinit var adapter: VideoFilesAdapter

    private var videoList = ArrayList<VideosEntity>()

    private var compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_viewer)

        db = AppDatabase(this)

        videoFiles = ArrayList(db.videosDao().getAll().reversed())

        recyclerView = findViewById(R.id.video_viewer_select_image_recycler_view)
        textCount = findViewById(R.id.video_viewer_select_image_count)
        btnDone = findViewById(R.id.video_viewer_select_image_done)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.hasFixedSize()

        textCount.text = "0/5"

        adapter = VideoFilesAdapter(this, videoFiles, this)

        recyclerView.adapter = adapter


        adapter.notifyDataSetChanged()
        btnDone.setOnClickListener {
            if (videoList.isNotEmpty()) {
                val options = arrayOf<CharSequence>("Restore", "Delete", "Share")
                val dialog = AlertDialog.Builder(this)
                    .setItems(options) { dialogInterface, i ->
                        when (i) {
                            0 -> {
                                dialogInterface.dismiss()
                                alertRestoreImages()
                            }
                            1 -> {
                                dialogInterface.dismiss()
                                alertDeleteImages()
                            }
                            2 -> {
                                dialogInterface.dismiss()
                                alertShareImages()
                            }
                        }
                    }
                    .setCancelable(true)
                dialog.create().show()
            }
        }
    }

    private fun alertShareImages() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("")
            .setCancelable(true)
        dialog.create().show()
    }

    private fun alertDeleteImages() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("The selected images will be permanently deleted")
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.setPositiveButton("Ok") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deleteImages()
            }
            .setCancelable(true)
        dialog.create().show()

    }

    private fun deleteImages() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Deleting")
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val observable = Observable.fromIterable<VideosEntity>(videoList)
            .subscribeOn(Schedulers.io())
            .filter {
                Thread.sleep(500)
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<VideosEntity> {

            override fun onNext(t: VideosEntity) {
                doDelete(t)
                videoFiles.remove(t)
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                compositeDisposable.clear()
                progressDialog.dismiss()
                textCount.text = "0/5"
                videoList.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun doDelete(video: VideosEntity) {
        try {
            FileUtils.forceDelete(File(video.temporalPath))
            deleteFromDatabase(video)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFromDatabase(video: VideosEntity) {
        try {
            GlobalScope.launch {
                db.videosDao().deleteVideo(video)
            }
        } catch (e: Exception) {
            Log.d("Ohhhhh Gd1111", e.message)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun alertRestoreImages() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Restore")
            .setMessage("The selected images will be restored back to their original location")
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.setPositiveButton("Ok") { dialogInterface, _ ->
                dialogInterface.dismiss()
                restoreImages()
            }
            .setCancelable(true)
        dialog.create().show()
    }

    private fun restoreImages() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Restoring...")
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val observable = Observable.fromIterable<VideosEntity>(videoList)
            .subscribeOn(Schedulers.io())
            .filter {
                Thread.sleep(1000)
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<VideosEntity> {

            override fun onNext(t: VideosEntity) {
                doRestore(t)
                videoFiles.remove(t)
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)

            }

            override fun onError(e: Throwable) {

            }

            @SuppressLint("SetTextI18n")
            override fun onComplete() {
                compositeDisposable.clear()
                progressDialog.dismiss()
                textCount.text = "0/5"
                videoList.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun doRestore(t: VideosEntity) {
        try {
            val fileDest = File(File(t.originalPath).parent)
            if (!fileDest.exists())
                fileDest.mkdir()
            FileUtils.copyFileToDirectory(File(t.temporalPath), fileDest)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(File(fileDest.absolutePath, File(t.temporalPath).name))
            FileUtils.forceDelete(File(t.temporalPath))
            sendBroadcast(intent)
            deleteFromDatabase(t)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun removeVideo(video: VideosEntity) {
        videoList.remove(video)
        if (videoList.size == 0) {
            btnDone.setImageResource(R.drawable.ic_apps_black_24dp)
            textCount.text = "0/5"
        } else {
            btnDone.setImageResource(R.drawable.ic_apps_white_24dp)
            textCount.text = "${videoList.size}/5"
        }
    }

    override fun getVideos(): ArrayList<VideosEntity> = videoList

    @SuppressLint("SetTextI18n")
    override fun setVideo(video: VideosEntity) {
            videoList.add(video)
            btnDone.setImageResource(R.drawable.ic_apps_white_24dp)
            textCount.text = "${videoList.size}/5"
    }

    override fun isChosen(video: VideosEntity): Boolean = videoList.contains(video)

}