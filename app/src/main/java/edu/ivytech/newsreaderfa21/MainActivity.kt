package edu.ivytech.newsreaderfa21

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import edu.ivytech.newsreaderfa21.ui.articlelist.ArticleListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ArticleListFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.settings -> {
                //Toast.makeText(this, "Clicked Settings", Toast.LENGTH_SHORT)
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.refresh -> {
                ArticleRepository.get().fetchArticles()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    companion object {
        fun newIntent(context : Context) : Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}