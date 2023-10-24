package org.openobservatory.ooniprobe.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import org.openobservatory.ooniprobe.BuildConfig
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.adapters.StringListRecyclerViewAdapter
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.databinding.ActivityOonirunBinding
import org.openobservatory.ooniprobe.domain.GetTestSuite
import org.openobservatory.ooniprobe.domain.VersionCompare
import org.openobservatory.ooniprobe.domain.models.Attribute
import org.openobservatory.ooniprobe.test.suite.AbstractSuite
import javax.inject.Inject

class OoniRunActivity : AbstractActivity() {
    lateinit var binding: ActivityOonirunBinding

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var versionCompare: VersionCompare

    @Inject
    lateinit var getSuite: GetTestSuite

    @Inject
    lateinit var gson: Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        binding = ActivityOonirunBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        manageIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        manageIntent(intent)
    }

    private fun manageIntent(intent: Intent) {
        if (isTestRunning) {
            Toast.makeText(this, getString(R.string.OONIRun_TestRunningError), Toast.LENGTH_LONG)
                .show()
            finish()
        } else if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            val mv = uri?.getQueryParameter("mv")
            val tn = uri?.getQueryParameter("tn")
            val ta = uri?.getQueryParameter("ta")
            loadScreen(mv, tn, ta)
        } else if (Intent.ACTION_SEND == intent.action) {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (url != null && Patterns.WEB_URL.matcher(url).matches()) {
                val urls = listOf(url)
                val suite = getSuite["web_connectivity", urls]
                if (suite != null) {
                    loadSuite(suite, urls)
                } else {
                    loadInvalidAttributes()
                }
            } else {
                loadInvalidAttributes()
            }
        }
    }

    private fun loadScreen(mv: String?, tn: String?, ta: String?) {
        val split = BuildConfig.VERSION_NAME.split("-".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val versionName = split.first()
        if (mv != null && tn != null) {
            if (versionCompare.compare(versionName, mv) >= 0) {
                try {
                    val attribute = gson.fromJson(ta, Attribute::class.java)
                    val urls = attribute?.urls
                    val suite = getSuite[tn, urls]
                    if (suite != null) {
                        loadSuite(suite, urls)
                    } else {
                        loadInvalidAttributes()
                    }
                } catch (e: Exception) {
                    loadInvalidAttributes()
                }
            } else {
                loadOutOfDate()
            }
        } else {
            loadInvalidAttributes()
        }
    }

    private fun loadOutOfDate() {
        setTextColor(resources.getColor(R.color.color_black))
        binding.title.setText(R.string.OONIRun_OONIProbeOutOfDate)
        binding.desc.setText(R.string.OONIRun_OONIProbeNewerVersion)
        binding.icon.setImageResource(R.drawable.update)
        binding.run.apply {
            setText(R.string.OONIRun_Update)
            setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
                finish()
            }
        }
    }

    private fun loadSuite(suite: AbstractSuite, urls: List<String>?) {
        val items = ArrayList<String>()
        binding.icon.setImageResource(suite.icon)
        binding.title.setText(suite.getTestList(preferenceManager).first().labelResId)
        binding.desc.text = getString(R.string.OONIRun_YouAreAboutToRun)
        urls?.let { urls ->
            urls.filterTo(items) { URLUtil.isValidUrl(it) }
            binding.recycler.apply {
                layoutManager = LinearLayoutManager(this@OoniRunActivity)
                adapter = StringListRecyclerViewAdapter(items)
            }
        }
        setThemeColor(resources.getColor(suite.color))
        binding.run.setOnClickListener {
            RunningActivity.runAsForegroundService(
                this@OoniRunActivity, suite.asArray(), { finish() }, preferenceManager
            )
        }
    }

    private fun setThemeColor(color: Int) {
        val window = window
        window.statusBarColor = color
        binding.appbarLayout.setBackgroundColor(color)
        when {
            ColorUtils.calculateLuminance(color) > 0.5 -> {
                setTextColor(Color.WHITE)
            }

            else -> {
                setTextColor(Color.WHITE)
            }
        }
    }

    private fun setTextColor(color: Int) {
        binding.title.setTextColor(color)
        binding.icon.setColorFilter(color)
        binding.desc.setTextColor(color)
        binding.run.apply {
            setTextColor(color)
            strokeColor = ColorStateList.valueOf(color)
        }
    }

    private fun loadInvalidAttributes() {
        setTextColor(resources.getColor(R.color.color_black))
        binding.title.setText(R.string.OONIRun_InvalidParameter)
        binding.desc.setText(R.string.OONIRun_InvalidParameter_Msg)
        binding.icon.setImageResource(R.drawable.question_mark)
        binding.run.apply {
            setText(R.string.OONIRun_Close)
            setOnClickListener { finish() }
        }
    }
}