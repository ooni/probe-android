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
                    descriptorList.forEach { ooniRunDescriptor ->

                        // Check if the descriptor is already in the database.
                        ooniRunDescriptor.nettests.forEach { nettest ->
                            nettest.inputs?.forEach { input ->
                                Url.checkExistingUrl(input)
                            }
                        }
                        // Check if the descriptor is already in the database.
                        val testDescriptor = SQLite.select().from(TestDescriptor::class.java)
                                .where(TestDescriptor_Table.runId.eq(ooniRunDescriptor.oonirunLinkId.toLong()))
                                .querySingle()
                        // Save the descriptor if it is not in the database.
                        if (testDescriptor == null) {
                            ooniRunDescriptor.toTestDescriptor().apply {

                                ooniRunDescriptor.nettests.forEach { nettest ->
                                    (context as? Application)?.preferenceManager?.let {
                                        it.enableTest(nettest.name, preferencePrefix())
                                        it.enableTest(nettest.name, preferencePrefix(),true)
                                    }
                                }

                                isAutoUpdate = true
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
            preferenceManager: PreferenceManager,
            testDescriptorManager: TestDescriptorManager
        ): MutableList<DynamicTestSuite> {
            return testDescriptorManager.getRunV2Descriptors(expired = false)
                .map { testDescriptor ->
                    testDescriptor.toDynamicTestSuite(context).apply {
                        autoRun = true
                    }
                }.toMutableList()
        }

    }
}
