package www.mycompany.fastcalculator

import adapter.ImageFilesAdapter
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.AppDatabase
import db.ImagesEntity
import utils.LiveImageData
import android.net.Uri
import android.util.Log
import android.widget.Toast
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


class ImageViewer : AppCompatActivity(), LiveImageData {

    lateinit var recyclerView: RecyclerView
    lateinit var textCount: TextView
    lateinit var btnDone: ImageButton

    private lateinit var db: AppDatabase
    private lateinit var imageFiles: ArrayList<ImagesEntity>

    lateinit var adapter: ImageFilesAdapter

    private var imageList = ArrayList<ImagesEntity>()

    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        db = AppDatabase(this)

        imageFiles = ArrayList(db.imagesDao().getAll().reversed())

        recyclerView = findViewById(R.id.image_viewer_select_image_recycler_view)
        textCount = findViewById(R.id.image_viewer_select_image_count)
        btnDone = findViewById(R.id.image_viewer_select_image_done)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.hasFixedSize()

        textCount.text = "0/${imageFiles.size}"

        adapter = ImageFilesAdapter(this, imageFiles, this)

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

        btnDone.setOnClickListener {
            if (imageList.isNotEmpty()) {
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

        val observable = Observable.fromIterable<ImagesEntity>(imageList)
            .subscribeOn(Schedulers.io())
            .filter {
                Thread.sleep(500)
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<ImagesEntity> {

            override fun onNext(t: ImagesEntity) {
                doDelete(t)
                imageFiles.remove(t)
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                compositeDisposable.clear()
                progressDialog.dismiss()
                textCount.text = "0/${imageFiles.size}"
                imageList.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun doDelete(image: ImagesEntity) {
        try {
            FileUtils.forceDelete(File(image.temporalPath))
            deleteFromDatabase(image)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFromDatabase(image: ImagesEntity) {
        try {
            GlobalScope.launch {
                db.imagesDao().deleteImage(image)
            }
        } catch (e: Exception) {
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

        val observable = Observable.fromIterable<ImagesEntity>(imageList)
            .subscribeOn(Schedulers.io())
            .filter {
                Thread.sleep(500)
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<ImagesEntity> {

            override fun onNext(t: ImagesEntity) {
                doRestore(t)
                imageFiles.remove(t)
            }

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                compositeDisposable.clear()
                progressDialog.dismiss()
                textCount.text = "0/${imageFiles.size}"
                imageList.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun doRestore(t: ImagesEntity) {
        try {
            val fileDest = File(File(t.originalPath).parent)
           if(!fileDest.exists())
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

    override fun removeImage(image: ImagesEntity) {
        imageList.remove(image)
        if (imageList.size == 0) {
            btnDone.setImageResource(R.drawable.ic_apps_black_24dp)
            textCount.text = "0/${imageFiles.size}"
        } else {
            btnDone.setImageResource(R.drawable.ic_apps_white_24dp)
            textCount.text = "${imageList.size}/${imageFiles.size}"
        }
    }


    override fun setImage(image: ImagesEntity) {
        imageList.add(image)
        btnDone.setImageResource(R.drawable.ic_apps_white_24dp)
        textCount.text = "${imageList.size}/${imageFiles.size}"
    }

    override fun isChosen(image: ImagesEntity): Boolean = imageList.contains(image)
}
