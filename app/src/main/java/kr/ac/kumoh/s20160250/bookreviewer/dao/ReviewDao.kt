package kr.ac.kumoh.s20160250.bookreviewer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.ac.kumoh.s20160250.bookreviewer.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE id == :id")
    fun getOneReview(id:Int): Review?

    @Insert(onConflict = OnConflictStrategy.REPLACE) //값이 새로운 년석이 들어오면 대체 시키기
    fun saveReview(review: Review)
}