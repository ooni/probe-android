package org.openobservatory.engine

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date
import java.util.HashMap

/**
 * This class represents the response from a fetch request to the OONI API.
 *
 * @see [https://github.com/ooni/spec/blob/master/backends/bk-005-ooni-run-v2.md]
 */
data class OONIRunDescriptor(

    @SerializedName("oonirun_link_id")
    val oonirunLinkId: String,

    var name: String,

    @SerializedName("short_description")
    val shortDescription: String,

    val description: String,

    val author: String,

    var nettests: List<OONIRunNettest>,

    @SerializedName("name_intl")
    val nameIntl: HashMap<String, String>,

    @SerializedName("short_description_intl")
    val shortDescriptionIntl: HashMap<String, String>,

    @SerializedName("description_intl")
    val descriptionIntl: HashMap<String, String>,

    val icon: String,

    val color: String,

    val animation: String,

    @SerializedName("expiration_date")
    val expirationDate: Date,

    @SerializedName("date_created")
    val dateCreated: Date,

    @SerializedName("date_updated")
    val dateUpdated: Date,

    val revision: String,

    @SerializedName("is_expired")
    val isExpired: Boolean

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


class OONIRunRevisions(
    val revisions: List<String>
) : Serializable