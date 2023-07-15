package org.openobservatory.engine

import com.google.gson.annotations.SerializedName

data class OONIRunFetchResponse(
    @JvmField
    val createdTime: String,
    @JvmField
    val descriptor: OONIRunDescriptor
)

data class OONIRunDescriptor(
    val author: String,
    val description: String,
    @SerializedName("description_intl")
    val descriptionIntl: Map<String, String>,
    val icon: String,
    val name: String,
    val nameIntl: String,
    val archived: Boolean,
    @SerializedName("short_description")
    val shortDescription: String,
    @SerializedName("short_description_intl")
    val shortDescriptionIntl:  Map<String, String>,
    val nettests: List<OONIRunNettest>
)


class OONIRunNettest(
    @SerializedName("test_name")
    val name: String,
    val inputs: List<String>
)
