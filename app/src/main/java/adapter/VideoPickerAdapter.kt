package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import utils.LiveVideoPickerData
import www.mycompany.fastcalculator.R
import java.io.File

class VideoPickerAdapter(
    var context: Context,
    var list: List<String>,
    var liveImageData: LiveVideoPickerData
) : RecyclerView.Adapter<VideoPickerAdapter.MyViewHolder>() {

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

        Glide.with(context).load(File(entity)).placeholder(R.drawable.ic_refresh_black_24dp)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            if (liveImageData.isChosen(entity)) {
                holder.btnCheck.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp)
                liveImageData.removeVideo(entity)
            } else {
            if (liveImageData.getVideos().size >= 5) {
                Toast.makeText(
                    context,
                    "you cannot select more than five at a time",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                    holder.btnCheck.setImageResource(R.drawable.ic_check_circle_black_24dp)
                    liveImageData.setVideo(entity)
                }
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