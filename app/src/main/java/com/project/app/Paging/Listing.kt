package com.project.app.Paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * Data class that is necessary for a UI to show a listing and interact w/ the rest of the system
 */
data class Listing<T>(
    // the LiveData of paged lists for the UI to observe
    val pagedList: LiveData<PagedList<T>>,
    // represents the network request status to show to the user
    val networkState: LiveData<NetworkState>,

    val refresh: () -> Unit,
    // retries any failed requests.
    val retry: () -> Unit,
    val fetchMore: () -> Unit
)
