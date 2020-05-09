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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import db.ImagesEntity
import utils.LiveImageData
import www.mycompany.fastcalculator.R
import java.io.File

class ImageFilesAdapter(
    var context: Context,
    var list: List<ImagesEntity>,
    var liveImageData: LiveImageData
) : RecyclerView.Adapter<ImageFilesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.single_file_pick_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val entity = list[position]

        if (liveImageData.isChosen(entity))
            holder.btnCheck.setImageResource(R.drawable.ic_check_circle_black_24dp)
        else
            holder.btnCheck.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp)

        Glide.with(context).load(File(entity.temporalPath)).placeholder(R.drawable.ic_refresh_black_24dp)
            .into(holder.imageView)

        /*Glide.with(context)
            .asBitmap()
            .load(File(entity.temporalPath))
            .override(800, 800)
            .placeholder(R.drawable.ic_warning_black_24dp)
            .into(object : BitmapImageViewTarget(holder.imageView) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    holder.imageView.setImageBitmap(resource)
                }
            })*/

        holder.imageView.setOnClickListener {
            if (liveImageData.isChosen(entity)) {
                holder.btnCheck.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp)
                liveImageData.removeImage(entity)
            } else {
                holder.btnCheck.setImageResource(R.drawable.ic_check_circle_black_24dp)
                liveImageData.setImage(entity)
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnCheck: ImageButton =
            itemView.findViewById(R.id.single_file_pick_check_uncheck_radio_btn)
        var imageView: ImageView = itemView.findViewById(R.id.single_file_pick_image)
        var layout: RelativeLayout = itemView.findViewById(R.id.single_file_pick_layout)
    }
}