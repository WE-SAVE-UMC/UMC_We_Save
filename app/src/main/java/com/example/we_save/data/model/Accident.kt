package com.example.we_save.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.we_save.domain.model.AccidentType
import java.util.Date

@Entity
data class Accident(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = "",
    val title: String = "",
    val description: String = "",
    val type: AccidentType = AccidentType.FIRE,
    val images: List<String> = listOf(),
    val timestamp: Long = Date().time,
    @ColumnInfo("is_end_situation")
    val isEndSituation: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        AccidentType.entries[parcel.readInt()],
        parcel.createStringArrayList()!!,
        parcel.readLong(),
        parcel.readInt() == 1
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(address)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(type.ordinal)
        parcel.writeStringList(images)
        parcel.writeLong(timestamp)
        parcel.writeInt(if (isEndSituation) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Accident> {
        override fun createFromParcel(parcel: Parcel): Accident {
            return Accident(parcel)
        }

        override fun newArray(size: Int): Array<Accident?> {
            return arrayOfNulls(size)
        }
    }
}
