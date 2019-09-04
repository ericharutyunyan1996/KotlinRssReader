@file:Suppress("DEPRECATION")

package com.example.kotlinrssreader

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinrssreader.Adapter.FeedAdapter
import com.example.kotlinrssreader.Model.RSSObject
import com.example.kotlinrssreader.Network.HTTPDataHandler
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val RSS_LINK = "https://rss.nytimes.com/services/xml/rss/nyt/Science.xml"
    private val RSS_TO_JSON_API = "https://api.rss2json.com/v1/api.json?rss_url="
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = "NEWS"
        setSupportActionBar(toolbar)
        val linearLayoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        loadRSS()
    }

    private fun loadRSS() {
        val loadRSSAsync = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, String, String>() {
            var mDialog = ProgressDialog(this@MainActivity)

            override fun onPostExecute(result: String?) {
                mDialog.dismiss()
                var rssObject: RSSObject
                rssObject = Gson().fromJson<RSSObject>(result, RSSObject::class.java)
                val adapter = FeedAdapter(rssObject, baseContext)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun doInBackground(vararg params: String): String {
                val result: String
                val http = HTTPDataHandler()
                result = http.GetHTTPHandler(params[0])
                return result
            }


            override fun onPreExecute() {
                mDialog.setMessage("Please wait")
                mDialog.show()
            }
        }
        val url_get_data = StringBuilder(RSS_TO_JSON_API)
        url_get_data.append(RSS_LINK)
        loadRSSAsync.execute(url_get_data.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh)
            loadRSS()
        return true
    }
}
