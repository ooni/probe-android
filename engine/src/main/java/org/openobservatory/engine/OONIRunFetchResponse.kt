package org.openobservatory.engine

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date
import java.util.HashMap

/**
 * This class represents the response from a fetch request to the OONI API.
 *
 * @property archived Whether the descriptor is archived.
 * @property creationTime The creation time of the descriptor.
 * @property translationCreationTime The translation creation time of the descriptor.
 * @property descriptor The descriptor.
 */
data class OONIRunFetchResponse(
    @JvmField
    val archived: Boolean,

    @JvmField
    @SerializedName("descriptor_creation_time")
    val creationTime: Date,

    @JvmField
    @SerializedName("translation_creation_time")
    val translationCreationTime: Date,

    @JvmField
    val descriptor: OONIRunDescriptor
) : Serializable

/**
 * Data class representing the OONI Run Descriptor.
 *
 * @property author The author of the nettest.
 * @property description The description of the nettest in English.
 * @property descriptionIntl The description of the nettest in other languages. The key is the language code and the value is the description in that language.
 * @property icon The URL of the icon representing the nettest.
 * @property color The color associated with the nettest in hexadecimal format.
 * @property animation The URL of the animation representing the nettest.
 * @property name The name of the nettest in English.
 * @property nameIntl The name of the nettest in other languages. The key is the language code and the value is the name in that language.
 * @property shortDescription The short description of the nettest in English.
 * @property shortDescriptionIntl The short description of the nettest in other languages. The key is the language code and the value is the short description in that language.
 * @property nettests A list of nettests associated with the run descriptor.
 *
 * @see [https://github.com/ooni/spec/blob/master/backends/bk-005-ooni-run-v2.md]
 */
data class OONIRunDescriptor(
    val author: String,

    val description: String,

    @SerializedName("description_intl")
    val descriptionIntl: HashMap<String, String>,

    val icon: String,

    val color: String,

    val animation: String,

    var name: String,

    @SerializedName("name_intl")
    val nameIntl: HashMap<String, String>,

    @SerializedName("short_description")
    val shortDescription: String,

    @SerializedName("short_description_intl")
    val shortDescriptionIntl: HashMap<String, String>,

    var nettests: List<OONIRunNettest>
) : Serializable

/**
 * Class representing a single OONI Run Nettest.
 *
 * @property name The name of the nettest.
 * @property inputs The inputs of the nettest.
 */
open class OONIRunNettest(
    @SerializedName("test_name")
    open var name: String,

    open var inputs: List<String>?
) : Serializable
