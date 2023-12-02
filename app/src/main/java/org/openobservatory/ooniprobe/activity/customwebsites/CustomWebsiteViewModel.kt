package org.openobservatory.ooniprobe.activity.customwebsites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CustomWebsiteViewModel : ViewModel() {

    val urls = MutableLiveData<MutableList<String>>()

    fun addUrl(url: String) {
        val currentUrls = urls.value ?: ArrayList()
        currentUrls.add(url)
        urls.value = currentUrls
    }

    fun onItemRemoved(position: Int) {
        val currentList = urls.value ?: mutableListOf()
        if (position < currentList.size) {
            currentList.removeAt(position)
            urls.value = currentList
        }
    }

    fun updateUrlAt(position: Int, newUrl: String) {
        val currentList = urls.value ?: mutableListOf()
        if (position < currentList.size) {
            currentList[position] = newUrl
            urls.value = currentList
        }
    }

}