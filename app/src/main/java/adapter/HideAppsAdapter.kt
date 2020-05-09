package adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import db.AppDatabase
import db.AppsEntity
import www.mycompany.fastcalculator.R

class HideAppsAdapter(
    var context: Context,
    var list: ArrayList<String>
) : RecyclerView.Adapter<HideAppsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.single_document_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val packageName = list[position]
        val entity = context.packageManager.getApplicationInfo(packageName, 0)

        Glide.with(context).load(context.packageManager.getApplicationIcon(entity))
            .placeholder(R.drawable.ic_insert_drive_file_grey_24dp)
            .into(holder.imageView)
        holder.textView.text = context.packageManager.getApplicationLabel(entity)
        holder.layout.setOnClickListener {
            holder.hideApp(entity, context)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnCheck: ImageButton = itemView.findViewById(R.id.document_select_btn)
        var imageView: ImageView = itemView.findViewById(R.id.document_select_image)
        var layout: CardView = itemView.findViewById(R.id.document_select_layout)
        var textView: TextView = itemView.findViewById(R.id.document_select_text)
        var textExt: TextView = itemView.findViewById(R.id.document_select_image_ext)

        fun hideApp(info: ApplicationInfo, context: Context) {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(info.packageName)
            val name = intent?.component
            val dialog = AlertDialog.Builder(context)
                .setTitle("Hide")
                .setMessage("The selected app will get hidden from your phone main menu")
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.setPositiveButton("Ok") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    pm.setComponentEnabledSetting(
                        name!!,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                    AppDatabase(context).appsDao().addAppInfo(AppsEntity(info.packageName))
                }
                .setCancelable(true)
            dialog.create().show()
        }
    }
}
