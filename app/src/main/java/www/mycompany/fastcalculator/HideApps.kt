package www.mycompany.fastcalculator

import adapter.HideAppsAdapter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.AppDatabase
import db.AppsEntity

class HideApps : AppCompatActivity() {

    var appFiles = ArrayList<String>()

    lateinit var recyclerView: RecyclerView

    lateinit var adapter: HideAppsAdapter
    private lateinit var db: AppDatabase

    lateinit var toolbar: Toolbar

    lateinit var pm: PackageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hide_apps)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Apps"

        pm = packageManager

        db = AppDatabase(this)

        recyclerView = findViewById(R.id.activity_hide_apps_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.hasFixedSize()

        appFiles = getInstalledApps()

        adapter = HideAppsAdapter(this, appFiles)
        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

    }

    private fun getInstalledApps(): java.util.ArrayList<String> {
        val list = ArrayList<String>()

        for (info in ArrayList(pm.getInstalledApplications(PackageManager.GET_META_DATA))) {
            if (!isSystemPackage(info)) {
                if (!ArrayList(AppDatabase(this).appsDao().getAll()).contains(AppsEntity(info.packageName)))
                    list.add(info.packageName)
            }
        }

        return list
    }

    fun isSystemPackage(pkgInfo: ApplicationInfo): Boolean {
        return if (pkgInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0)
            false
        else if (pkgInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0)
            true
        else pkgInfo.flags and ApplicationInfo.FLAG_INSTALLED == 0

    }
}
