package www.mycompany.fastcalculator


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.bumptech.glide.Glide
import db.AppDatabase
import db.DocumentEntity
import db.ImagesEntity
import db.VideosEntity
import java.io.File

class HiddenFiles : Fragment() {

    private lateinit var db : AppDatabase

   private lateinit var imageFiles : List<ImagesEntity>
   private lateinit var videoFiles : List<VideosEntity>
   private lateinit var docFiles : List<DocumentEntity>

    private lateinit var imagePic : ImageButton
    private lateinit var videoPic : ImageButton
    private lateinit var docPic : ImageButton
    private lateinit var appPic : ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_hidden_files, container, false)

        imagePic = v.findViewById(R.id.img_hidden_picture_btn)
        videoPic = v.findViewById(R.id.img_hidden_video_btn)
        docPic = v.findViewById(R.id.img_hidden_document_btn)
        appPic = v.findViewById(R.id.img_hidden_document_btn)

        db = AppDatabase(context!!)

        imagePic.setOnClickListener {
            if(db.imagesDao().getAll().isEmpty())
                Toast.makeText(context, "No image files saved", Toast.LENGTH_SHORT).show()
            else
            startActivity(Intent(context, ImageViewer::class.java))
        }
        videoPic.setOnClickListener {
            if(db.videosDao().getAll().isEmpty())
                Toast.makeText(context, "No video files saved", Toast.LENGTH_SHORT).show()
            else
            startActivity(Intent(context, VideoViewer::class.java))
        }
        docPic.setOnClickListener {
            if(db.documentsDao().getAll().isEmpty())
                Toast.makeText(context, "No document files saved", Toast.LENGTH_SHORT).show()
            else
            startActivity(Intent(context, DocumentViewer::class.java))
        }

        appPic.setOnClickListener {
            if(db.appsDao().getAll().isEmpty())
                Toast.makeText(context, "No apps saved", Toast.LENGTH_SHORT).show()
            else
            startActivity(Intent(context, AppsViewer::class.java))
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        imageFiles = db.imagesDao().getAll()
        if(imageFiles.isNotEmpty()) {
            val file = File(imageFiles.last().temporalPath)
            Glide.with(context!!).load(file).placeholder(R.drawable.ic_warning_black_24dp)
                .into(imagePic)
        }else
            imagePic.setImageResource(R.drawable.ic_warning_black_24dp)

        videoFiles = db.videosDao().getAll()
        if(videoFiles.isNotEmpty()) {
            val file = File(videoFiles.last().temporalPath)
            Glide.with(context!!).load(file).placeholder(R.drawable.ic_warning_black_24dp)
                .into(videoPic)
        }else
            videoPic.setImageResource(R.drawable.ic_warning_black_24dp)
    }

}
