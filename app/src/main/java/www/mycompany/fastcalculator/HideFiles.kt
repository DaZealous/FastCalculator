package www.mycompany.fastcalculator


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import me.nereo.multi_image_selector.MultiImageSelector

class HideFiles : Fragment() {

    private lateinit var btnImage: ImageButton
    private lateinit var btnVideo: ImageButton
    private lateinit var btnDoc: ImageButton
    private lateinit var btnApp: ImageButton
    private val IMAGE_RESULT = 1
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val v: View = inflater.inflate(R.layout.fragment_hide_files, container, false)

        btnImage = v.findViewById(R.id.fragment_hide_image_pick)
        btnVideo = v.findViewById(R.id.fragment_hide_video_pick)
        btnDoc = v.findViewById(R.id.fragment_hide_document_pick)
        btnApp = v.findViewById(R.id.fragment_hide_apps_pick)

        btnImage.setOnClickListener {

            MultiImageSelector.create(context)
                .showCamera(false) // show camera or not. true by default
                .count(20) // max select image size, 9 by default. used width #.multi()
                .multi() // multi mode, default mode;
                .origin(ArrayList()) // original select data set, used // width #.multi()
                .start(activity, IMAGE_RESULT)
        }

        btnVideo.setOnClickListener {
            startActivity(Intent(context, VideoPicker::class.java))
        }

        btnDoc.setOnClickListener {
            startActivity(Intent(context, Documents::class.java))
        }

        btnApp.setOnClickListener {
            startActivity(Intent(context, HideApps::class.java))
        }
        return v
    }

}
