package com.rafaelanastacioalves.design.concepts.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
 data class MainEntity (

        @PrimaryKey  var id: String,
        @ColumnInfo(name = "title")   var title: String,
        @ColumnInfo(name = "price")   var price: String,
        @ColumnInfo(name = "price_currency")
        @SerializedName("price_currency")
        var priceCurrency: String,
        @ColumnInfo(name = "image_url")
        @SerializedName("image_url")
        var imageUrl: String

)

