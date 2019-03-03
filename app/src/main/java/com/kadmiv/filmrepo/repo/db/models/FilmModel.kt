package com.kadmiv.filmrepo.repo.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.NonNull

@Entity(tableName = "films_table")

class FilmModel() : BaseModel(), Parcelable {
    @NonNull
    @PrimaryKey
    override var id: Long = 0
    var title: String = ""
    var image: String? = ""
    var director: String = ""
    var category: String = ""
    var overview: String = ""
    var releaseDate: String = ""
    var rating: Double = 0.0
    var isFavorite: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        title = parcel.readString()
        image = parcel.readString()
        director = parcel.readString()
        category = parcel.readString()
        overview = parcel.readString()
        releaseDate = parcel.readString()
        rating = parcel.readDouble()
        isFavorite = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(director)
        parcel.writeString(category)
        parcel.writeString(overview)
        parcel.writeString(releaseDate)
        parcel.writeDouble(rating)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilmModel> {
        override fun createFromParcel(parcel: Parcel): FilmModel {
            return FilmModel(parcel)
        }

        override fun newArray(size: Int): Array<FilmModel?> {
            return arrayOfNulls(size)
        }
    }
}