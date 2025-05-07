package com.example.bookdbapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookdbapp.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private lateinit var bookList: ArrayList<Book>
    private lateinit var bookRecyclerViewAdapter: BookRecyclerViewAdapter

    private class BookRecyclerViewAdapter(private val books: ArrayList<Book>):
            RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder>() {
                data class ViewHolder(val bookView: View):
                        RecyclerView.ViewHolder(bookView) {
                            val tvTitle = bookView.findViewById<TextView>(R.id.tvTitle)
                            val tvAuthor = bookView.findViewById<TextView>(R.id.tvAuthor)
                            val tvPublisher = bookView.findViewById<TextView>(R.id.tvPublisher)
                        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v =LayoutInflater.from(parent.context)
                .inflate(R.layout.book_item, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val book = books[position]
            holder.tvTitle.text = book.title
            holder.tvAuthor.text = book.author
            holder.tvPublisher.text = book.publisher

            holder.bookView.setOnLongClickListener {
                val dlg =BottomSheetDialog(it.context, R.style.BottomSheetDlg)
                dlg.setContentView(R.layout.dlg_del_and_update)
                val btDel = dlg.findViewById<Button>(R.id.btDel)
                val btUpdate = dlg.findViewById<Button>(R.id.btUpdate)

                btDel?.setOnClickListener {
                    BookDbHelper.getInstance()?.deleteBook(book.id)
                    books.removeAt(position)
                    notifyDataSetChanged()
                    dlg.dismiss()
                }

                dlg.show()
                true
            }
        }

        override fun getItemCount(): Int {
           return  books.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 設定RecycleView
        bookList = arrayListOf()
        bookRecyclerViewAdapter = BookRecyclerViewAdapter(bookList)
        val rvBookList = findViewById<RecyclerView>(R.id.rvBookList)
        rvBookList.adapter = bookRecyclerViewAdapter
        rvBookList.layoutManager = LinearLayoutManager(this)

        BookDbHelper.init(this)
        val dbHelper = BookDbHelper.getInstance()

        dataBinding.btAdd.setOnClickListener {
            val book = Book(
                -1,
                dataBinding.edTitle.text.toString(),
                dataBinding.edAuthor.text.toString(),
                dataBinding.edPublisher.text.toString(),
                )
            dbHelper?.addBook(book)
        }

        dataBinding.btList.setOnClickListener {
            bookList.clear()
            val books = dbHelper?.getAllBooks()
            books?.let {
                bookList.addAll(books)
                bookRecyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }
}