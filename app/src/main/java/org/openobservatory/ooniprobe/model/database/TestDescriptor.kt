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
import org.openobservatory.ooniprobe.common.LocaleUtils
import org.openobservatory.ooniprobe.common.PreferenceManager
import org.openobservatory.ooniprobe.common.resolveStatus
import java.io.Serializable
import java.util.Date
import com.raizlabs.android.dbflow.annotation.TypeConverter as TypeConverterAnnotation

@Table(database = AppDatabase::class)
class TestDescriptor(

    @PrimaryKey
    var runId: Long = 0,

    @Column
    var name: String = "",

    @Column(name = "short_description")
    var shortDescription: String = "",

    @Column
    var description: String = "",

    @Column
    var author: String = "",

    @Column(typeConverter = NettestConverter::class)
    var nettests: Any = emptyList<OONIRunNettest>(),

    @Column(name = "name_intl", typeConverter = MapConverter::class)
    var nameIntl: Any? = null,

    @Column(name = "short_description_intl", typeConverter = MapConverter::class)
    var shortDescriptionIntl: Any? = null,

    @Column(name = "description_intl", typeConverter = MapConverter::class)
    var descriptionIntl: Any? = null,

    @Column
    var icon: String? = null,

    @Column
    var color: String? = null,

    @Column
    var animation: String? = null,

    @Column(name = "expiration_date")
    var expirationDate: Date? = null,

    @Column(name = "date_created")
    var dateCreated: Date? = null,

    @Column(name = "date_updated")
    var dateUpdated: Date? = null,

    @Column
    var revision: String? = null,


    @Column(name = "previous_revision")
    var previousRevision: String? = null,

    @Column(name = "is_expired")
    var isExpired: Boolean? = false,

    @Column(name = "auto_run")
    var isAutoRun: Boolean = true,

    @Column(name = "auto_update")
    var isAutoUpdate: Boolean = false,

) : BaseModel(), Serializable {
    fun preferencePrefix(): String {
        return "${runId}_"
    }

    fun localizedName() :String {
        return nameIntl.getValueForKey(LocaleUtils.getLocale().language) ?: name
    }

    fun localizedShortDescription() :String {
        return shortDescriptionIntl.getValueForKey(LocaleUtils.getLocale().language) ?: shortDescription
    }

    fun localizedDescription() :String {
        return descriptionIntl.getValueForKey(LocaleUtils.getLocale().language) ?: description
    }
}

/**
 * Check if the test descriptor should be updated
 * @param updatedDescriptor The new descriptor
 * @return True if the descriptor should be updated, False otherwise
 */
fun TestDescriptor.shouldUpdate(updatedDescriptor: TestDescriptor): Boolean {
    return (updatedDescriptor.dateUpdated?.after(dateUpdated) ?: true)
}

private const val DESCRIPTOR_TEST_NAME = "ooni_run"

class InstalledDescriptor(
    var testDescriptor: TestDescriptor,
    var tags: List<String>? = null
) : AbstractDescriptor<BaseNettest>(
    name = DESCRIPTOR_TEST_NAME,
    title = testDescriptor.localizedName(),
    shortDescription = testDescriptor.localizedShortDescription(),
    description = testDescriptor.localizedDescription(),
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

    override fun getRuntime(context: Context, preferenceManager: PreferenceManager): Int {
        return R.string.TestResults_NotAvailable
    }

    fun isUpdateAvailable(): Boolean {
        return tags?.contains("updated") ?: false
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
                    selected = preferenceManager.resolveStatus(
                        name = nettest.name,
                        prefix = preferencePrefix(),
                    ), name = nettest.name, inputs = nettest.inputs
                )
            },
            descriptor = this.testDescriptor
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

class ITestDescriptor(

    var runId: Long = 0,

    var name: String = "",

    var shortDescription: String = "",

    var description: String = "",

    var author: String = "",


    var nettests: List<OONIRunNettest>? = emptyList(),

    var nameIntl: HashMap<String, String>? = null,

    var shortDescriptionIntl: HashMap<String, String>? = null,

    var descriptionIntl: HashMap<String, String>? = null,

    var icon: String? = null,

    var color: String? = null,

    var animation: String? = null,

    val expirationDate: Date? = null,

    val dateCreated: Date? = null,

    val dateUpdated: Date? = null,

    val revision: String? = null,

    var previousRevision: String? = null,

    val isExpired: Boolean? = false,

    var isAutoUpdate: Boolean = false,

) : Serializable {
    fun toTestDescriptor(): TestDescriptor {
        return TestDescriptor(
            runId = runId,
            name = name,
            shortDescription = shortDescription,
            description = description,
            author = author,
            nettests = nettests?: emptyList<OONIRunNettest>(),
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
            previousRevision = previousRevision,
            isExpired = isExpired,
            isAutoUpdate = isAutoUpdate
        )
    }
}
