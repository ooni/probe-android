package org.openobservatory.ooniprobe.activity.customwebsites

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.util.Patterns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.RunningActivity
import org.openobservatory.ooniprobe.activity.customwebsites.adapter.CustomWebsiteRecyclerViewAdapter
import org.openobservatory.ooniprobe.activity.customwebsites.adapter.ItemChangedListener
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.databinding.ActivityCustomwebsiteBinding
import org.openobservatory.ooniprobe.fragment.ConfirmDialogFragment
import org.openobservatory.ooniprobe.model.database.Url
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite
import java.io.Serializable
import javax.inject.Inject

/** This activity allows a user to test a custom website. */
class CustomWebsiteActivity : AbstractActivity(), ConfirmDialogFragment.OnClickListener {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    val viewModel: CustomWebsiteViewModel by viewModels()

    private lateinit var adapter: CustomWebsiteRecyclerViewAdapter
    private lateinit var binding: ActivityCustomwebsiteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        binding = ActivityCustomwebsiteBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val layoutManager = LinearLayoutManager(this)
        binding.urlContainer.layoutManager = layoutManager
        adapter = CustomWebsiteRecyclerViewAdapter(
            onItemChangedListener = object : ItemChangedListener {
                override fun onItemRemoved(position: Int) {
                    binding.bottomBar.title = getString(
                        R.string.OONIRun_URLs, adapter.itemCount.toString()
                    )
                    viewModel.onItemRemoved(position)
                }

                override fun onItemUpdated(position: Int, item: String) {
                    viewModel.updateUrlAt(position, item)
                }
            },
        )
        viewModel.urls.observe(this) { urls ->
            binding.bottomBar.title = getString(
                R.string.OONIRun_URLs, urls.size.toString()
            )
        }

        binding.bottomBar.setOnMenuItemClickListener { item: MenuItem? -> runTests() }
        binding.add.setOnClickListener { add() }

        binding.urlContainer.adapter = adapter
        if (viewModel.urls.value == null) {
            add()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.urls.value?.let { urls ->
            adapter.submitList(urls)
            binding.urlContainer.post { adapter.notifyDataSetChanged() }
        }
    }

    /**
     * This function will run the tests if the list of urls is not empty.
     * If the list is empty, it will not run the tests.
     * This function will also sanitize the url and remove any new lines.
     * It will also check if the url is valid and not too long.
     * If the url is not valid or too long, it will not be added to the tests.
     */
    private fun runTests(): Boolean {
        val items = viewModel.urls.value ?: listOf()
        if (items.isEmpty()) {
            return false
        }
        val urls = ArrayList<String>(items.size)
        for (value in items) {
            val sanitizedUrl = value.replace("\\r\\n|\\r|\\n".toRegex(), " ")
            //https://support.microsoft.com/en-us/help/208427/maximum-url-length-is-2-083-characters-in-internet-explorer
            if (Patterns.WEB_URL.matcher(sanitizedUrl)
                    .matches() && sanitizedUrl.length < 2084
            ) urls.add(
                Url.checkExistingUrl(sanitizedUrl).toString()
            )
        }
        val suite = WebsitesSuite()
        suite.getTestList(preferenceManager)[0].inputs = urls
        RunningActivity.runAsForegroundService(
            this@CustomWebsiteActivity, suite.asArray(), { finish() }, preferenceManager
        )
        return true
    }

    /**
     * This function will show a dialog if the user has edited the list of urls.
     * If the user has edited the list of urls, it will show a dialog asking if the user wants to save the changes.
     * If the user has not edited the list of urls, it will just call super.onBackPressed()
     */
    override fun onBackPressed() {
        val base = getString(R.string.http)
        val edited = adapter.itemCount > 0 && viewModel.urls.value?.get(0) != base
        if (edited) {
            ConfirmDialogFragment(
                title = getString(R.string.Modal_CustomURL_Title_NotSaved),
                message = getString(R.string.Modal_CustomURL_NotSaved),
            ).show(supportFragmentManager, null)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.close, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_button -> {
                onSupportNavigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * This function will add a new url to the list of urls.
     * It will also scroll to the bottom of the list.
     */
    fun add() {
        viewModel.addUrl(getString(R.string.http))
        binding.urlContainer.layoutManager?.scrollToPosition(adapter.itemCount - 1)
    }

    /**
     * This function will be called when the user clicks on a button in the dialog.
     * If the user clicks on the positive button, it will call super.onBackPressed()
     */
    override fun onConfirmDialogClick(
        serializable: Serializable?, parcelable: Parcelable?, buttonClicked: Int
    ) {
        if (buttonClicked == DialogInterface.BUTTON_POSITIVE) super.onBackPressed()
    }
}
