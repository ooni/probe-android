package org.openobservatory.ooniprobe.common

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.openobservatory.engine.BaseNettest
import org.openobservatory.engine.LoggerArray
import org.openobservatory.engine.OONIRunDescriptor
import org.openobservatory.engine.OONIRunRevisions
import org.openobservatory.ooniprobe.BuildConfig
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.adddescriptor.adapter.GroupedItem
import org.openobservatory.ooniprobe.fragment.resultList.ResultItemType
import org.openobservatory.ooniprobe.fragment.resultList.ResultListSpinnerItem
import org.openobservatory.ooniprobe.model.database.ITestDescriptor
import org.openobservatory.ooniprobe.model.database.InstalledDescriptor
import org.openobservatory.ooniprobe.model.database.Result
import org.openobservatory.ooniprobe.model.database.Result_Table
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.model.database.TestDescriptor_Table
import org.openobservatory.ooniprobe.model.database.Url
import org.openobservatory.ooniprobe.model.database.getNettests
import org.openobservatory.ooniprobe.test.EngineProvider
import org.openobservatory.ooniprobe.test.suite.DynamicTestSuite
import org.openobservatory.ooniprobe.test.test.WebConnectivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class is responsible for managing the test descriptors
 */
@Singleton
class TestDescriptorManager @Inject constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val gson: Gson
) {
    private val descriptors: List<OONIDescriptor<BaseNettest>> = ooniDescriptors(context)

    fun getDescriptors(): List<OONIDescriptor<BaseNettest>> {
        return descriptors
    }

    fun getDescriptorByName(name: String): OONIDescriptor<BaseNettest>? {
        return descriptors.find { it.name == name }
    }

    fun getTestByDescriptorName(name: String): DynamicTestSuite? {
        return getDescriptorByName(name)?.getTest(context)
    }

    /**
     * Fetches the descriptor from the ooni server using the run id.
     * @param runId the run id of the descriptor to fetch
     * @param context the context to use for the request
     */
    fun fetchDescriptorFromRunId(runId: Long, context: Context): TestDescriptor? {
        val session = EngineProvider.get().newSession(
                EngineProvider.get().getDefaultSessionConfig(
                        context,
                        BuildConfig.SOFTWARE_NAME,
                        BuildConfig.VERSION_NAME,
                        LoggerArray(),
                        (context.applicationContext as Application).preferenceManager.proxyURL
                )
        )
        val ooniContext = session.newContextWithTimeout(300)

        runCatching {
            // Fetch the descriptor from the ooni server
            session.getLatestOONIRunLink(ooniContext, BuildConfig.OONI_API_BASE_URL, runId)
        }.fold(
            onSuccess = { ooniRunDescriptor ->

                var revisions: OONIRunRevisions? = null

                try {
                    if (ooniRunDescriptor.revision.toInt() > 1) {
                        revisions = session.getOONIRunLinkRevisions(
                                ooniContext,
                                BuildConfig.OONI_API_BASE_URL,
                                runId
                        )
                    }
                } catch (e: Exception) {
                    ThirdPartyServices.logException(e)
                }
                return ooniRunDescriptor.toTestDescriptor().apply {
                    previousRevision = Gson().toJson(revisions)
                }
            },
            onFailure = { throwable ->
                // Log the error or handle it appropriately
                ThirdPartyServices.logException(Exception(throwable))
                return null
            }
        )
    }

    fun addDescriptor(
        descriptor: TestDescriptor,
        disabledAutorunNettests: List<GroupedItem>
    ): Boolean {
        descriptor.getNettests()
            .filter { it.name == WebConnectivity.NAME }
            .forEach {
                it.inputs?.forEach { url -> Url.checkExistingUrl(url) }
            }
        descriptor.getNettests().map { it.name }
            .filter { item -> disabledAutorunNettests.map { it.name }.contains(item) }
            .forEach { item ->
                preferenceManager.enableTest(
                    name = item,
                    prefix = descriptor.preferencePrefix(),
                    autoRun = true
                )
            }
        return descriptor.save()
    }

    fun getById(runId: Long): TestDescriptor? {
        return SQLite.select().from(TestDescriptor::class.java)
            .where(TestDescriptor_Table.runId.eq(runId)).querySingle()
    }

    fun delete(descriptor: InstalledDescriptor): Boolean {
        preferenceManager.sp.all.entries.forEach { entry ->
            if (entry.key.contains(descriptor.testDescriptor.runId.toString())) {
                preferenceManager.sp.edit().remove(entry.key).apply()
            }
        }
        return SQLite.select().from(Result::class.java)
            .where(Result_Table.descriptor_runId.eq(descriptor.testDescriptor.runId))
            .queryList().forEach { it.delete(context) }.run {
                descriptor.testDescriptor.delete()
            }
    }

    fun getRunV2Descriptors(autoUpdate: Boolean? = null, expired: Boolean? = null): List<TestDescriptor> {
        return SQLite.select().from(TestDescriptor::class.java)
                .where(
                        autoUpdate?.let { TestDescriptor_Table.auto_update.eq(it) },
                        expired?.let { TestDescriptor_Table.is_expired.eq(it) }
                )
                .orderBy(TestDescriptor_Table.is_expired.asc())
                .queryList()
    }

    fun updateFromNetwork(descriptorJson: String): Boolean {
        return runCatching {
            gson.fromJson(descriptorJson, Array<ITestDescriptor>::class.java)
                    .map { it.toTestDescriptor() }.map { updateFromNetwork(it) }.all { it }
        }.fold(
            onSuccess = { response -> response },
            onFailure = { false }
        )
    }

    fun updateFromNetwork(testDescriptor: TestDescriptor): Boolean {
        getById(testDescriptor.runId)?.let { descriptor ->
            testDescriptor.isAutoUpdate = descriptor.isAutoUpdate
            descriptor.getNettests().forEach { nettest ->
                nettest.inputs?.forEach { input ->
                    Url.checkExistingUrl(input)
                }
            }
            return testDescriptor.save()
        } ?: run {
            return false
        }
    }

    fun getDescriptorsFromIds(ids: Array<Long>): List<TestDescriptor> {
        return SQLite.select().from(TestDescriptor::class.java)
            .where(TestDescriptor_Table.runId.`in`(ids.toList()))
            .queryList()
    }

    fun getFilterItems(resources: Resources): List<ResultListSpinnerItem> {
        val values = resources.getStringArray(R.array.filterTestValues)
        resources.getStringArray(R.array.filterTests).also { labels ->
            return labels.mapIndexed { index, label ->
                ResultListSpinnerItem(values[index], label)
            }.toMutableList().apply {
                addAll(
                        getRunV2Descriptors().map {
                            return@map ResultListSpinnerItem(it.runId.toString(), it.name, ResultItemType.RUN_V2_ITEM)
                        }
                )
            }
        }
    }
}

fun OONIRunDescriptor.toTestDescriptor(): TestDescriptor {
    return TestDescriptor(
        runId = oonirunLinkId.toLong(),
        name = name,
        shortDescription = shortDescription,
        description = description,
        author = author,
        nettests = nettests,
        nameIntl = nameIntl,
        shortDescriptionIntl = shortDescriptionIntl,
        descriptionIntl = descriptionIntl,
        icon = icon,
        color = color,
        animation = animation,
        expirationDate = expirationDate,
        dateCreated = dateCreated,
        dateUpdated = dateUpdated,
        revision = revision,
        isExpired = isExpired
    )
}
