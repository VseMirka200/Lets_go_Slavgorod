package com.example.slavgorodbus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_times")
data class FavoriteTimeEntity(
    @PrimaryKey
    val id: String,
    val someNewProperty: String = "",

    @ColumnInfo(name = "route_id")
    val routeId: String,

    @ColumnInfo(name = "stop_name")
    val stopName: String,

    @ColumnInfo(name = "departure_time")
    val departureTime: String,

    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int,

    @ColumnInfo(name = "departure_point")
    val departurePoint: String,

    @ColumnInfo(name = "is_active", defaultValue = "true")
    val isActive: Boolean = true
)