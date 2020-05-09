package www.mycompany.fastcalculator

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.pm.ApplicationInfo
import android.os.Bundle
import java.util.ArrayList
import adapter.HideAppsAdapter
import db.AppDatabase
import db.AppsEntity

class AppsViewer : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HideAppsAdapter
    private var apps = ArrayList<String>()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_viewer)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Restore Apps"

        apps = getAppsInfo(ArrayList(AppDatabase(this).appsDao().getAll()))

        recyclerView = findViewById(R.id.activity_view_apps_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter = HideAppsAdapter(this, apps)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun getAppsInfo(arrayList: ArrayList<AppsEntity>): ArrayList<String> {
        val list = ArrayList<String>()
        for (entity in arrayList) {
            list.add(entity.packageName)
        }
        return list
    }
}
