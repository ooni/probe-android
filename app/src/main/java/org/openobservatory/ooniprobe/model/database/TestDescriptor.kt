package org.openobservatory.ooniprobe.model.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.converter.TypeConverter
import com.raizlabs.android.dbflow.structure.BaseModel
import org.openobservatory.engine.OONIRunNettest
import org.openobservatory.ooniprobe.activity.add_descriptor.adapter.GroupedItem
import org.openobservatory.ooniprobe.common.AppDatabase
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
) : BaseModel(), Serializable

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

