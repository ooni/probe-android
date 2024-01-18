package org.openobservatory.ooniprobe.model.database

import android.content.Context
import android.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.converter.TypeConverter
import com.raizlabs.android.dbflow.structure.BaseModel
import org.openobservatory.engine.BaseNettest
import org.openobservatory.engine.OONIRunNettest
import org.openobservatory.ooniprobe.R
import org.openobservatory.ooniprobe.activity.adddescriptor.adapter.GroupedItem
import org.openobservatory.ooniprobe.activity.runtests.models.ChildItem
import org.openobservatory.ooniprobe.activity.runtests.models.GroupItem
import org.openobservatory.ooniprobe.common.AbstractDescriptor
import org.openobservatory.ooniprobe.common.AppDatabase
import org.openobservatory.ooniprobe.common.OONITests
import org.openobservatory.ooniprobe.common.PreferenceManager
import java.io.Serializable
import java.util.Date
import com.raizlabs.android.dbflow.annotation.TypeConverter as TypeConverterAnnotation

@Table(database = AppDatabase::class)
class TestDescriptor(
    @PrimaryKey
    var runId: Long = 0,
    @Column
    var name: String = "",
    @Column(name = "name_intl", typeConverter = MapConverter::class)
    var nameIntl: Any? = null,
    @Column
    var author: String = "",
    @Column(name = "short_description")
    var shortDescription: String = "",
    @Column(name = "short_description_intl", typeConverter = MapConverter::class)
    var shortDescriptionIntl: Any? = null,
    @Column
    var description: String = "",
    @Column(name = "description_intl", typeConverter = MapConverter::class)
    var descriptionIntl: Any? = null,
    @Column
    var icon: String? = null,
    @Column
    var color: String? = null,
    @Column
    var animation: String? = null,
    @Column
    var isArchived: Boolean = false,
    @Column(name = "auto_run")
    var isAutoRun: Boolean = true,
    @Column(name = "auto_update")
    var isAutoUpdate: Boolean = false,
    @Column(name = "descriptor_creation_time")
    var descriptorCreationTime: Date? = null,
    @Column(name = "translation_creation_time")
    var translationCreationTime: Date? = null,
    @Column(typeConverter = NettestConverter::class)
    var nettests: Any = emptyList<OONIRunNettest>()
) : BaseModel(), Serializable {
    fun preferencePrefix(): String {
        return "${runId}_"
    }
}

fun TestDescriptor.shouldUpdate(updatedDescriptor: TestDescriptor): Boolean {
    return (updatedDescriptor.descriptorCreationTime?.after(descriptorCreationTime) ?: true
            || updatedDescriptor.translationCreationTime?.after(translationCreationTime) ?: true)
}

private const val DESCRIPTOR_TEST_NAME = "ooni_run"

class InstalledDescriptor(
    var testDescriptor: TestDescriptor
) : AbstractDescriptor<BaseNettest>(
    name = DESCRIPTOR_TEST_NAME,
    title = testDescriptor.name,
    shortDescription = testDescriptor.shortDescription,
    description = testDescriptor.description,
    icon = testDescriptor.icon ?: "settings_icon",
    color = Color.parseColor(testDescriptor.color ?: "#495057"),
    animation = testDescriptor.animation,
    dataUsage = R.string.TestResults_NotAvailable,
    nettests = when (testDescriptor.nettests is List<*>) {
        true -> (testDescriptor.nettests as List<*>)
            .filterIsInstance<OONIRunNettest>()
            .map { nettest: OONIRunNettest ->
                return@map BaseNettest(
                    name = nettest.name,
                    inputs = nettest.inputs,
                )
            }

        false -> emptyList()
    },
    descriptor = testDescriptor) {

    override fun isEnabled(preferenceManager: PreferenceManager): Boolean {
        return !testDescriptor.isArchived
    }

    override fun getRuntime(context: Context, preferenceManager: PreferenceManager): Int {
        return R.string.TestResults_NotAvailable
    }

    override fun toRunTestsGroupItem(preferenceManager: PreferenceManager): GroupItem {
        return GroupItem(
            selected = false,
            name = this.name,
            title = this.title,
            shortDescription = this.shortDescription,
            description = this.description,
            icon = this.icon,
            color = Color.parseColor(testDescriptor.color ?: "#495057"),
            animation = this.animation,
            dataUsage = this.dataUsage,
            nettests = this.nettests.map { nettest ->
                ChildItem(
                    selected = when (this.name == OONITests.EXPERIMENTAL.label) {
                        true -> preferenceManager.isExperimentalOn
                        false -> preferenceManager.resolveStatus(nettest.name)
                    }, name = nettest.name, inputs = nettest.inputs
                )
            },
            descriptor = testDescriptor
        )
    }

}

fun TestDescriptor.getNettests(): List<OONIRunNettest> {
    return when (nettests is List<*>) {
        true -> (nettests as List<*>)
            .filterIsInstance<OONIRunNettest>()
            .map { nettest: OONIRunNettest ->
                return@map GroupedItem(
                    name = nettest.name,
                    inputs = nettest.inputs,
                    selected = true
                )
            }

        false -> emptyList()
    }
}

fun Any?.getValueForKey(language: String): String? {
    return if (this is Map<*, *>) {
        this[language] as String?
    } else {
        null
    }
}


@TypeConverterAnnotation
class MapConverter : TypeConverter<String, Any>() {
    override fun getDBValue(model: Any?): String? {
        return Gson().toJson(model)
    }

    override fun getModelValue(json: String): HashMap<String, String> {
        val gson = Gson()
        val type = object : TypeToken<HashMap<String, String>>() {}.type
        return gson.fromJson(json, type)
    }
}


@TypeConverterAnnotation
class NettestConverter : TypeConverter<String, Any>() {
    override fun getDBValue(model: Any): String = Gson().toJson(model)

    override fun getModelValue(data: String): List<*> = Gson().fromJson(
        data, Array<OONIRunNettest>::class.java
    ).toList()
}

