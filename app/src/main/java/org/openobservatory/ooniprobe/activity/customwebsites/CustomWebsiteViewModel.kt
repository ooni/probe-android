package org.openobservatory.ooniprobe.activity.customwebsites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * This class is used to store the data for the CustomWebsiteActivity.
 * The data is stored in a ViewModel so that it can survive configuration changes (like rotation).
 * This class shound not be injected to the activity using a DI framework.
 */
class CustomWebsiteViewModel : ViewModel() {

    val urls = MutableLiveData<MutableList<String>>()

    /**
     * This function will add a new url to the list of urls.
     * If the list is null, it will create a new list.
     */
    fun addUrl(url: String) {
        val currentUrls = urls.value ?: ArrayList()
        currentUrls.add(url)
        urls.value = currentUrls
    }

    /**
     * This function will remove a url from the list of urls.
     * If the list is null, it will not do anything.
     */
    fun onItemRemoved(position: Int) {
        val currentList = urls.value ?: mutableListOf()
        if (position < currentList.size) {
            currentList.removeAt(position)
            urls.value = currentList
        }
    }

    /**
     * This function will update the url at the given position.
     * If the list is null, it will not do anything.
     */
    fun updateUrlAt(position: Int, newUrl: String) {
        val currentList = urls.value ?: mutableListOf()
        if (position < currentList.size) {
            currentList[position] = newUrl
            urls.value = currentList
        }
    }

}