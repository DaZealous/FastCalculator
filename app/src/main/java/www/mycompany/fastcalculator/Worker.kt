package www.mycompany.fastcalculator

import adapter.TabAdapter
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import me.nereo.multi_image_selector.MultiImageSelector
import android.widget.TextView
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import db.AppDatabase
import db.ImagesEntity
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
import java.io.File


class Worker : AppCompatActivity() {

    private lateinit var adapter: TabAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var toolbar: Toolbar

    private val IMAGE = 1
    private var paths: ArrayList<String>? = null

    var status = 0
    lateinit var dialog: Dialog
    lateinit var progressBar: ProgressBar
    lateinit var textProgress: TextView
    lateinit var observable: Observable<String>

    var compositeDisposable = CompositeDisposable()

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker)

        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        setSupportActionBar(toolbar)

        adapter = TabAdapter(supportFragmentManager)
        adapter.addFragment(HideFiles(), "Save")
        adapter.addFragment(HiddenFiles(), "Restore")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        db = AppDatabase(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK) {
            paths = data?.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT)
            Log.d("tag1", paths?.get(0).toString())
            moveFile()
        }else{
            Toast.makeText(this, "no images selected", Toast.LENGTH_LONG).show()
            Log.d("tag1", "no images selected")
        }
    }

    private fun moveFile() {
       dialog = Dialog(this)
           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
           dialog.setCancelable(false)
        dialog.setContentView(R.layout.progress_dialog_layout)

        progressBar = dialog.findViewById(R.id.progress_horizontal)
        textProgress = dialog.findViewById(R.id.text_progress)

        dialog.show()


    val window = dialog.window
    window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        observable = Observable.fromIterable<String>(paths)
            .subscribeOn(Schedulers.io())
            .filter {
                Thread.sleep(500)
                true
            }
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<String> {

            override fun onNext(t: String) {
                status += 1
                progressBar.progress = status
                textProgress.text = "$status/${paths?.size}"
                doMove(File(t))
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
            }
        })
    }

    private fun doMove(file : File){
        try {
            val fileDest = File(Config.PicturePath)
            if(!fileDest.exists())
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

    private fun saveToDatabase(absolutePath: String, temporalPath : String) {
        try {
            GlobalScope.launch {
                db.imagesDao().addImage(ImagesEntity(absolutePath, temporalPath))
            }
        } catch (e: Exception) {
            Log.d("Ohhhhh Gd1111", e.message)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
