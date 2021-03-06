package adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import db.DocumentEntity
import db.ImagesEntity
import utils.Config
import utils.LiveDocumentData
import utils.LiveImageData
import www.mycompany.fastcalculator.R
import java.io.File

class DocumentViewerAdapter(
    var context: Context,
    var list: List<DocumentEntity>,
    var liveDocumentData: LiveDocumentData
) : RecyclerView.Adapter<DocumentViewerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.single_document_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val entity = list[position]
        holder.btnCheck.setImageResource(R.drawable.ic_location_off_black_24dp)

        holder.btnCheck.setOnClickListener {
                liveDocumentData.removeDocument(entity)
        }
        Glide.with(context).load(File(entity.temporalPath)).placeholder(holder.getDrawable(File(entity.temporalPath).name.split(".")[1]))
            .into(holder.imageView)

        holder.textView.text = File(entity.temporalPath).name
        holder.textExt.text = File(entity.temporalPath).name.split(".")[1]

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnCheck: ImageButton = itemView.findViewById(R.id.document_select_btn)
        var imageView: ImageView = itemView.findViewById(R.id.document_select_image)
        var layout: CardView = itemView.findViewById(R.id.document_select_layout)
        var textView: TextView = itemView.findViewById(R.id.document_select_text)
        var textExt: TextView = itemView.findViewById(R.id.document_select_image_ext)

        fun getDrawable(value: String): Int {
            return when (value) {
                "docx" -> R.drawable.ic_insert_drive_file_blue_24dp
                "txt" -> R.drawable.ic_insert_drive_file_green_24dp
                "pdf" -> R.drawable.ic_insert_drive_file_red_24dp
                "doc" -> R.drawable.ic_insert_drive_file_blue_24dp
                "html" -> R.drawable.ic_insert_drive_file_yellow_24dp
                "css" -> R.drawable.ic_insert_drive_file_green_24dp
                else -> R.drawable.ic_insert_drive_file_grey_24dp
            }
        }
    }
}
