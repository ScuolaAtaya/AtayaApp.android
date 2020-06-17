package it.mindtek.ruah.pojos

import android.os.Parcelable
import android.os.Parcel


/**
 * Created by alessandro on 09/01/2018.
 */
class Download(
        var progress: Int = 0,
        var currentFileSize: Int = 0,
        var totalFileSize: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(progress)
        parcel.writeInt(currentFileSize)
        parcel.writeInt(totalFileSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Download> {
        override fun createFromParcel(parcel: Parcel): Download {
            return Download(parcel)
        }

        override fun newArray(size: Int): Array<Download?> {
            return arrayOfNulls(size)
        }
    }
}