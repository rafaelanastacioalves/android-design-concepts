package com.example.rafaelanastacioalves.moby.domain.entities;

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EntityDetails {

    @PrimaryKey
    @ColumnInfo(name = "title") public lateinit var title: String
    @ColumnInfo(name = "description") public lateinit var description: String
    @ColumnInfo(name = "price") public lateinit var price: String
    @ColumnInfo(name = "image_url") public lateinit var image_url: String

}
