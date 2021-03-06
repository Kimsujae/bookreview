package kr.ac.kumoh.s20160250.bookreviewer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kr.ac.kumoh.s20160250.bookreviewer.adapter.BookAdapter
import kr.ac.kumoh.s20160250.bookreviewer.adapter.HistoryAdapter
import kr.ac.kumoh.s20160250.bookreviewer.api.BookService
import kr.ac.kumoh.s20160250.bookreviewer.databinding.ActivityMainBinding
import kr.ac.kumoh.s20160250.bookreviewer.model.BestSellerDto
import kr.ac.kumoh.s20160250.bookreviewer.model.History
import kr.ac.kumoh.s20160250.bookreviewer.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initBookRecyclerView() //recyclerView init
        initHistoryRecyclerView() // HistoryRecyclerView()
        initSearchEditText()
        Log.d("binding.historyRecyclerView.isVisible",binding.historyRecyclerView.isVisible.toString())

        db= getAppDatabase(this)


        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    //todo ????????????
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    response.body()?.let {
                        //Log.d(TAG, it.toString())

                        it.books.forEach { book ->
                           //Log.d(TAG, book.toString())
                        }
                        adapter.submitList(it.books)
                    }

                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    //todo ?????? ??????
                    Log.e(TAG, t.toString())
                }

            })

    }

    private fun search(keyword: String) {

        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBookDto> {
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    hideHistoryView()
                    saveSearchKeyword(keyword)
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    adapter.submitList(response.body()?.books.orEmpty())


                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    //todo ?????? ??????
                    hideHistoryView()

                    Log.e(TAG, t.toString())
                }

            })
    }

    private fun initBookRecyclerView() {
        adapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this,DetailActivity::class.java)
            intent.putExtra("bookModel",it)
            startActivity(intent)
        })
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun initSearchEditText(){
        binding.searchEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener { _, event ->
            if(event.action ==MotionEvent.ACTION_DOWN ){
                showHistoryView()

            }
            return@setOnTouchListener false
        }
    }

    private fun showHistoryView() {

        Log.d("??????????????? ?????????","????????????")
        binding.historyRecyclerView.isVisible = true
        Thread(Runnable {
            db.historyDao()
                .getAll()
                .reversed()
                .run {
                    runOnUiThread {
                        binding.historyRecyclerView.isVisible = true
                        Log.d("binding.historyRecyclerView.isVisible",binding.historyRecyclerView.isVisible.toString())
                        historyAdapter.submitList(this)
                    }
                }

        }).start()

    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()

    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }



    companion object {
        private const val TAG = "MainActivity"
    }
}