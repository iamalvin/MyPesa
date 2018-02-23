package com.ecmdapps.mypesa

import android.os.Parcel
import android.os.Parcelable


class Coin(val name: String, val id: String, val value: Double,  val valueUSD: Double, val mktCap: Double, val mktCapUSD: Double, val percentChange: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeDouble(value)
        parcel.writeDouble(valueUSD)
        parcel.writeDouble(mktCap)
        parcel.writeDouble(mktCapUSD)
        parcel.writeDouble(percentChange)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coin> {
        override fun createFromParcel(parcel: Parcel): Coin {
            return Coin(parcel)
        }

        override fun newArray(size: Int): Array<Coin?> {
            return arrayOfNulls(size)
        }
    }
}