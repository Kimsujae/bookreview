package kr.ac.kumoh.s20160250.bookreviewer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name ="keyword") val keyword: String?
)
