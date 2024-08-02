package com.example.vft

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

class IllustItem(val illustId: Int, val startDate: Timestamp, val endDate: Timestamp): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(illustId)
        parcel.writeParcelable(startDate, flags)
        parcel.writeParcelable(endDate, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IllustItem> {
        override fun createFromParcel(parcel: Parcel): IllustItem {
            return IllustItem(parcel)
        }

        override fun newArray(size: Int): Array<IllustItem?> {
            return arrayOfNulls(size)
        }
    }
}