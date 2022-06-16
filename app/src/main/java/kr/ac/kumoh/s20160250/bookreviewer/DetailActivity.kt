package kr.ac.kumoh.s20160250.bookreviewer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bumptech.glide.Glide
import kr.ac.kumoh.s20160250.bookreviewer.databinding.ActivityDetailBinding
import kr.ac.kumoh.s20160250.bookreviewer.model.Book
import kr.ac.kumoh.s20160250.bookreviewer.model.Review

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = getAppDatabase(this)

        val model = intent.getParcelableExtra<Book>("bookModel")
        Log.d("bookmodel정보", model.toString())
        binding.titleTextView.text = model?.title.orEmpty()
        binding.descriptionTextView.text = model?.description.orEmpty()
        Glide.with(binding.coverImageView.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.coverImageView)

        Thread {
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            runOnUiThread {
                binding.reviewEditText.setText(review?.review.orEmpty())
            }
        }.start()

        binding.saveButton.setOnClickListener {
            Thread {
                db.reviewDao().saveReview(
                    Review(
                        model?.id?.toInt() ?: 0, binding.reviewEditText.text.toString()
                    )
                )
            }.start()
            AlertDialog.Builder(this).setTitle("리뷰 저장").setMessage("저장되었습니다").show()
        }

    }
}