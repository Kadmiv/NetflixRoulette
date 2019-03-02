package com.kadmiv.filmrepo.repo

import android.os.Parcel
import android.os.Parcelable

data class InfoModel(val infoText: String, val image: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(infoText)
        parcel.writeInt(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InfoModel> {
        override fun createFromParcel(parcel: Parcel): InfoModel {
            return InfoModel(parcel)
        }

        override fun newArray(size: Int): Array<InfoModel?> {
            return arrayOfNulls(size)
        }
    }
}