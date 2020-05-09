package www.mycompany.fastcalculator

import adapter.DocumentsAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.AppDatabase
import db.DocumentEntity
import db.ImagesEntity
import org.apache.commons.io.FileUtils
import utils.Config
import utils.LiveDocumentData
import java.io.File

class Documents : AppCompatActivity(), LiveDocumentData {

    var docFiles = ArrayList<File>()
    var docList = ArrayList<DocumentEntity>()
    lateinit var recyclerView: RecyclerView

    lateinit var adapter: DocumentsAdapter
    private lateinit var db: AppDatabase

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Documents"

        db = AppDatabase(this)

        recyclerView = findViewById(R.id.activity_document_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.hasFixedSize()

        docFiles = getDocuments()
        docList = ArrayList(db.documentsDao().getAll())

        adapter = DocumentsAdapter(this, docFiles, this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    private fun getDocuments(): java.util.ArrayList<File> {
        val lists = ArrayList<File>()
        try {
            val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
            val selection =
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE
            val cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                null
            )

            while (cursor!!.moveToNext()) {
                val filePath = cursor.getString(0)
                if (endsWith(File(filePath)))
                    lists.add(File(filePath))
            }

            cursor.close()
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return lists
    }

    private fun endsWith(file: File): Boolean {
        return file.name.endsWith(".docx")
                || file.name.endsWith(".txt")
                || file.name.endsWith(".pdf")
                || file.name.endsWith(".doc")
                || file.name.endsWith(".html")
                || file.name.endsWith(".css")
    }

    override fun isChosen(documents: DocumentEntity): Boolean = docList.contains(documents)

    override fun setDocument(documents: DocumentEntity) {
      db.documentsDao().addDocument(documents)
        docList.add(documents)
        doMove(File(documents.originalPath))
    }

    override fun removeDocument(documents: DocumentEntity) {
        db.documentsDao().deleteDocument(documents)
        docList.remove(documents)
        doRestore(documents)
    }

    private fun doMove(file : File){
        try {
            val fileDest = File(Config.DocPath)
            if(!fileDest.exists())
                fileDest.mkdir()
            FileUtils.copyFileToDirectory(file, fileDest)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
            FileUtils.forceDelete(file)
            sendBroadcast(intent)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
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
