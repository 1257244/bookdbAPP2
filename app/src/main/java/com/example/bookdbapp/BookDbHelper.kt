package com.example.bookdbapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class BookDbHelper private constructor(val context: Context) :
    SQLiteOpenHelper(context, DB_FILE, null, 1) {

    companion object {
        // 宣告常數
        private const val DB_FILE = "books.db"
        private const val BOOK_TABLE = "books"
        private const val ID = "id"
        private const val BOOK_TITLE = "book_title"
        private const val BOOK_AUTHOR = "book_author"
        private const val BOOK_PUBLISHER = "book_publisher"

        private var bookDbHelper: BookDbHelper? = null

        fun init(context: Context) {
            if (bookDbHelper == null) bookDbHelper = BookDbHelper(context)
        }

        fun getInstance(): BookDbHelper? {
            return bookDbHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlCreateTable = "CREATE TABLE $BOOK_TABLE ($ID integer primary key," +
                "$BOOK_TITLE nvarchar(30), $BOOK_AUTHOR nvarchar(20), $BOOK_PUBLISHER nvarchar(50));"

        db?.execSQL(sqlCreateTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $BOOK_TABLE")
            onCreate(db)
        }
    }

    fun addBook(book: Book) {
        val cv = ContentValues()
        cv.put(BOOK_TITLE, book.title)
        cv.put(BOOK_AUTHOR, book.author)
        cv.put(BOOK_PUBLISHER, book.publisher)

        writableDatabase.insert(BOOK_TABLE, null, cv)
        writableDatabase.close()
    }

    fun printAllBooks() {
        val c = readableDatabase.query(
            BOOK_TABLE, arrayOf(BOOK_TITLE, BOOK_AUTHOR, BOOK_PUBLISHER),
            null, null, null, null, null
        )
        if (c.count != 0) {
            c.moveToFirst()
            do {
                Toast.makeText(
                    context, "Book: ${c.getString(0)}, ${c.getString(1)}, ${c.getString(2)}",
                    Toast.LENGTH_SHORT
                ).show()
            } while (c.moveToNext())
        }
    }

    fun getAllBooks(): ArrayList<Book> {
        val c = readableDatabase.query(
            BOOK_TABLE, arrayOf(BOOK_TITLE, BOOK_AUTHOR, BOOK_PUBLISHER),
            null, null, null, null, null
        )

        val books = arrayListOf<Book>()

        if (c.count != 0) {
            c.moveToFirst()
            do {
                val b = Book(c.getString(0), c.getString(1), c.getString(2))
                books.add(b)
            } while (c.moveToNext())
        }

        return books
    }
}