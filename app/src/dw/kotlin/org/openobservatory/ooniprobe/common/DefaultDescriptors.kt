package org.openobservatory.ooniprobe.common

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.openobservatory.engine.BaseNettest
import org.openobservatory.engine.OONIRunDescriptor
import org.openobservatory.ooniprobe.model.database.TestDescriptor
import org.openobservatory.ooniprobe.model.database.TestDescriptor_Table
import org.openobservatory.ooniprobe.model.database.Url
import org.openobservatory.ooniprobe.test.suite.DynamicTestSuite

class DefaultDescriptors {
    companion object {

        @SuppressLint("StringFormatInvalid")
        @JvmStatic
        fun getAll(context: Context): List<AbstractDescriptor<BaseNettest>> {
            // NOTE: Performance hit since json is read and database checked every time this method is called.
            context.assets.open("descriptors.json").use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    // Read the json file and convert it to a list of OONIRunDescriptor.
                    val descriptorType = object : TypeToken<List<OONIRunDescriptor>>() {}.type
                    val descriptorList: List<OONIRunDescriptor> =
                            Gson().fromJson(jsonReader, descriptorType)
                    // Save the descriptors to the database.
                    descriptorList.forEach {

                        // Check if the descriptor is already in the database.
                        it.nettests.forEach { nettest ->
                            nettest.inputs?.forEach { input ->
                                Url.checkExistingUrl(input)
                            }
                        }
                        // Check if the descriptor is already in the database.
                        val testDescriptor = SQLite.select().from(TestDescriptor::class.java)
                                .where(TestDescriptor_Table.runId.eq(it.oonirunLinkId.toLong()))
                                .querySingle()
                        // Save the descriptor if it is not in the database.
                        if (testDescriptor == null) {
                            it.toTestDescriptor().apply {
                                isAutoUpdate = true
                                isAutoRun = true
                            }.save()
                        }
                    }
                    return emptyList()
                }
            }
        }

        @JvmStatic
        fun autoRunTests(
                context: Context,
                preferenceManager: PreferenceManager
        ): List<DynamicTestSuite> {

            return getAll(context).filter { ooniDescriptor ->
                when (ooniDescriptor.name) {
                    OONITests.EXPERIMENTAL.label -> preferenceManager.resolveStatus(
                            name = ooniDescriptor.name,
                            prefix = ooniDescriptor.preferencePrefix(),
                            autoRun = true
                    )

                    else -> ooniDescriptor.nettests.any { nettest ->
                        preferenceManager.resolveStatus(
                                name = nettest.name,
                                prefix = ooniDescriptor.preferencePrefix(),
                                autoRun = true
                        )
                    }
                }
            }.map { ooniDescriptor ->
                when (ooniDescriptor.name) {
                    OONITests.EXPERIMENTAL.label -> DynamicTestSuite(
                            name = ooniDescriptor.name,
                            title = ooniDescriptor.title,
                            shortDescription = ooniDescriptor.shortDescription,
                            description = ooniDescriptor.description,
                            icon = ooniDescriptor.getDisplayIcon(context),
                            icon_24 = ooniDescriptor.getDisplayIcon(context),
                            color = ooniDescriptor.color,
                            animation = ooniDescriptor.animation,
                            dataUsage = ooniDescriptor.dataUsage,
                            nettest = (ooniDescriptor.nettests).run {
                                this + (ooniDescriptor.longRunningTests?.filter { nettest ->
                                    preferenceManager.resolveStatus(
                                            name = nettest.name,
                                            prefix = ooniDescriptor.preferencePrefix(),
                                            autoRun = true
                                    )
                                } ?: listOf())
                            }
                    ).apply {
                        autoRun = true
                    }

                    else -> DynamicTestSuite(
                            name = ooniDescriptor.name,
                            title = ooniDescriptor.title,
                            shortDescription = ooniDescriptor.shortDescription,
                            description = ooniDescriptor.description,
                            icon = ooniDescriptor.getDisplayIcon(context),
                            icon_24 = ooniDescriptor.getDisplayIcon(context),
                            color = ooniDescriptor.color,
                            animation = ooniDescriptor.animation,
                            dataUsage = ooniDescriptor.dataUsage,
                            nettest = (ooniDescriptor.nettests).filter { nettest ->
                                preferenceManager.resolveStatus(
                                        name = nettest.name,
                                        prefix = ooniDescriptor.preferencePrefix(),
                                        autoRun = true
                                )
                            }.run {
                                this + (ooniDescriptor.longRunningTests?.filter { nettest ->
                                    preferenceManager.resolveStatus(
                                            name = nettest.name,
                                            prefix = ooniDescriptor.preferencePrefix(),
                                            autoRun = true
                                    )
                                } ?: listOf())
                            }
                    ).apply {
                        autoRun = true
                    }
                }
            }
        }

    }
}
