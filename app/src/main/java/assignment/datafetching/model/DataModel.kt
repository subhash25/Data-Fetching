package assignment.datafetching.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataModelItem(
    val body: String,
    val id: Int,
    val title: String,
    val userId: Int
) : Parcelable

