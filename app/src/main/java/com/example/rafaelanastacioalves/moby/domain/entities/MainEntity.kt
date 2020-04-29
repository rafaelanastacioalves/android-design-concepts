package com.example.rafaelanastacioalves.moby.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class MainEntity (

    @PrimaryKey  var id: String,
    @ColumnInfo(name = "title")   var title: String,
    @ColumnInfo(name = "price")   var price: String,
    @ColumnInfo(name = "price_currency")   var price_currency: String,
    @ColumnInfo(name = "image_url")   var image_url: String

)

