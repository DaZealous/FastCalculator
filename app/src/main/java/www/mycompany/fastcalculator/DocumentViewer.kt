package www.mycompany.fastcalculator

import adapter.DocumentViewerAdapter
import adapter.DocumentsAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.AppDatabase
import db.DocumentEntity
import org.apache.commons.io.FileUtils
import utils.Config
import utils.LiveDocumentData
import java.io.File

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DocumentViewer : AppCompatActivity(), LiveDocumentData {

    var docFiles = ArrayList<DocumentEntity>()
    var docList = ArrayList<DocumentEntity>()
    lateinit var recyclerView: RecyclerView

    lateinit var adapter: DocumentViewerAdapter
    private lateinit var db: AppDatabase

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_viewer)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Restore Documents"

        db = AppDatabase(this)

        recyclerView = findViewById(R.id.activity_document_viewer_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.hasFixedSize()

        docFiles = ArrayList(db.documentsDao().getAll())

        docList = ArrayList(db.documentsDao().getAll())

        adapter = DocumentViewerAdapter(this, docFiles, this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    override fun isChosen(documents: DocumentEntity): Boolean = docList.contains(documents)

    override fun setDocument(documents: DocumentEntity) {

    }

    override fun removeDocument(documents: DocumentEntity) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Restore")
            .setMessage("This document will be restored back to its original location")
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.setPositiveButton("Ok") { dialogInterface, _ ->
                dialogInterface.dismiss()
                db.documentsDao().deleteDocument(documents)
                docFiles.remove(documents)
                doRestore(documents)
                adapter.notifyDataSetChanged()
            }
            .setCancelable(true)
        dialog.create().show()
    }

    private fun doRestore(t: DocumentEntity) {
        try {
            val fileDest = File(File(t.originalPath).parent)
            if(!fileDest.exists())
                fileDest.mkdir()
            FileUtils.copyFileToDirectory(File(t.temporalPath), fileDest)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(File(fileDest.absolutePath, File(t.temporalPath).name))
            FileUtils.forceDelete(File(t.temporalPath))
            sendBroadcast(intent)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

}
