package org.openobservatory.ooniprobe.common

import android.content.Context
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.openobservatory.engine.BaseNettest
import org.openobservatory.engine.LoggerArray
import org.openobservatory.engine.OONIRunDescriptor
import org.openobservatory.ooniprobe.BuildConfig
import org.openobservatory.ooniprobe.activity.adddescriptor.adapter.GroupedItem
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
    private val preferenceManager: PreferenceManager
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
    fun fetchDescriptorFromRunId(runId: Long, context: Context): TestDescriptor {
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

        val response: OONIRunDescriptor =
            session.ooniRunFetch(ooniContext, BuildConfig.OONI_API_BASE_URL, runId)
        return TestDescriptor(
            runId = runId,
            name = response.name,
            shortDescription = response.shortDescription,
            description = response.description,
            author = response.author,
            nettests = response.nettests,
            nameIntl = response.nameIntl,
            shortDescriptionIntl = response.shortDescriptionIntl,
            descriptionIntl = response.descriptionIntl,
            icon = response.icon,
            color = response.color,
            animation = response.animation,
            expirationDate = response.expirationDate,
            dateCreated = response.dateCreated,
            dateUpdated = response.dateUpdated,
            revision = response.revision,
            isExpired = response.isExpired
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

    fun getRunV2Descriptors(): List<TestDescriptor> {
        return SQLite.select().from(TestDescriptor::class.java).queryList()
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

    fun getDescriptorWithAutoUpdateEnabled(): List<TestDescriptor> {
        return SQLite.select().from(TestDescriptor::class.java)
            .where(TestDescriptor_Table.auto_update.eq(true)).queryList()
    }

    fun getDescriptorWithAutoUpdateDisabled(): List<TestDescriptor> {
        return SQLite.select().from(TestDescriptor::class.java)
            .where(TestDescriptor_Table.auto_update.eq(false)).queryList()
    }

    fun updateFromNetwork(testDescriptor: TestDescriptor): Boolean {
        getById(testDescriptor.runId)?.let { descriptor ->
            testDescriptor.isAutoUpdate = descriptor.isAutoUpdate
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
}
