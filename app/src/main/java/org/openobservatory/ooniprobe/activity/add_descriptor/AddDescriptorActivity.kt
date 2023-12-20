package org.openobservatory.ooniprobe.activity.add_descriptor

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.checkbox.MaterialCheckBox
import io.noties.markwon.Markwon
import org.openobservatory.engine.OONIRunDescriptor
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.AbstractActivity
import org.openobservatory.ooniprobe.activity.add_descriptor.adapter.AddDescriptorExpandableListAdapter
import org.openobservatory.ooniprobe.activity.add_descriptor.adapter.GroupedItem
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.ReadMorePlugin
import org.openobservatory.ooniprobe.common.TestDescriptorManager
import org.openobservatory.ooniprobe.databinding.ActivityAddDescriptorBinding
import javax.inject.Inject

class AddDescriptorActivity : AbstractActivity() {
    companion object {
        private const val DESCRIPTOR = "descriptor"

        @JvmStatic
        fun newIntent(context: Context, descriptor: OONIRunDescriptor): Intent {
            return Intent(context, AddDescriptorActivity::class.java).putExtra(
                DESCRIPTOR,
                descriptor
            )
        }

        /**
         * This method is used to set the text of a textview as markdown. The markdown is parsed using the markwon library.
         * The textview must have the attribute app:richText set to the markdown text to parse.
         * @param view is the textview that will contain the parsed text
         * @param richText is the markdown text to parse
         */
        @JvmStatic
        @BindingAdapter(value = ["richText"])
        fun setRichText(view: TextView, richText: String?) {
            richText?.let { textValue ->
                val r = view.context.resources
                val markwon = Markwon.builder(view.context)
                    .usePlugin(
                        ReadMorePlugin(
                            labelMore = r.getString(R.string.OONIRun_ReadMore),
                            labelLess = r.getString(R.string.OONIRun_ReadLess)
                        )
                    )
                    .build()
                markwon.setMarkdown(view, textValue)
            }
        }

        /**
         * This method is used to set the image of an imageview as a drawable resource. The drawable is set using the name of the resource.
         * The imageview must have the attribute app:resource set to the name of the resource to set.
         * @param imageView is the imageview that will contain the drawable resource
         * @param iconName is the name of the drawable resource
         */
        @JvmStatic
        @BindingAdapter(value = ["resource"])
        fun setImageViewResource(imageView: ImageView, iconName: String?) {
            /*TODO(aanorbel): Update to parse the icon name and set the correct icon.
            *  Remember to ignore icons generated when generated doing this.*/
            imageView.setImageResource(R.drawable.ooni_empty_state)
        }

    }

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var descriptorManager: TestDescriptorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        val binding = ActivityAddDescriptorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = "Add New Link"
        val descriptorExtra = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DESCRIPTOR, OONIRunDescriptor::class.java)
        } else {
            intent.getSerializableExtra(DESCRIPTOR) as OONIRunDescriptor?
        }
        val viewModel: AddDescriptorViewModel by viewModels {
            object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddDescriptorViewModel(descriptorManager) as T
                }
            }
        }
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        descriptorExtra?.let { descriptor ->
            viewModel.onDescriptorChanged(descriptor)
            val adapter = AddDescriptorExpandableListAdapter(
                nettests = descriptor.nettests.map { nettest ->
                    GroupedItem(
                        name = nettest.name,
                        inputs = nettest.inputs,
                        selected = true
                    )
                },
                viewModel = viewModel
            )
            binding.expandableListView.setAdapter(adapter)
            for (i in 0 until adapter.groupCount) {
                binding.expandableListView.expandGroup(i)
            }
            val bottomBarOnMenuItemClickListener: Toolbar.OnMenuItemClickListener =
                Toolbar.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.add_descriptor -> {
                            viewModel.onAddButtonClicked(
                                selectedNettest = adapter.nettests.filter { it.selected },
                                automatedUpdates = binding.automaticUpdatesSwitch.isChecked
                            )
                            true
                        }

                        else -> false
                    }
                }
            binding.bottomBar.setOnMenuItemClickListener(bottomBarOnMenuItemClickListener)

            viewModel.selectedAllBtnStatus.observe(this) { state ->
                binding.testsCheckbox.checkedState = state;
            }

            binding.testsCheckbox.addOnCheckedStateChangedListener { checkBox, state ->
                viewModel.setSelectedAllBtnStatus(state)
                adapter.notifyDataSetChanged()
            }

            viewModel.finishActivity.observe(this) { shouldFinish ->
                if (shouldFinish) {
                    finish()
                }
            }
        } ?: run {
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.close, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_button -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}