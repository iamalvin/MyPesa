package com.ecmdapps.mypesa

import android.content.Context
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CoinsDatabaseHandler(context: Context) {
    companion object {
        private const val dbName = "Coins"
        private const val dbTable = "Following"
        const val colId = "Id"
        const val colCoinName = "CoinName"
        const val colCoinId = "CoinId"

        private const val dbVersion = 3
        private const val CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS $dbTable ($colId INTEGER PRIMARY KEY,$colCoinName TEXT, $colCoinId TEXT);"
    }

    private var db: SQLiteDatabase? = null

    fun insert(values: ContentValues): Long {
        return db!!.insert(dbTable, "", values)
    }

    fun queryAll(): Cursor {
        return db!!.rawQuery("select * from $dbTable", null)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        return db!!.delete(dbTable, selection, selectionArgs)
    }

    init {
        val dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
    }

    inner class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, dbName, null, dbVersion) {

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(CREATE_TABLE_SQL)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table IF EXISTS $dbTable")
            db.execSQL(CREATE_TABLE_SQL)
        }
    }

    private fun find(coinId: String) : Cursor {
        return db!!.rawQuery("select * from $dbTable where $colCoinId = ? ", arrayOf(coinId))
    }

    private fun findByColID(ID: Long) : Cursor {
        return db!!.rawQuery("select * from $dbTable where $colId = ? ", arrayOf(ID.toString()))
    }

    fun isCoinInDB(coinId: String) : Boolean {
        val cursor = find(coinId)
        return when {
            cursor.count > 0 -> {
                cursor.close()
                true
            }
            else -> false
        }
    }

    private fun getCoinByColID(colID: Long): String {
        val cursor = findByColID(colID)
        return if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val coinId = cursor.getString(cursor.getColumnIndex(colCoinId))
                    cursor.close()
                    coinId
                } else {
                    "NO_INSERT_OR_NO_SUCH_COIN_EXISTS"
                }
    }

    fun add(values: ContentValues) : String {
        val coinId = values.get(colCoinId) as String
        val cursor = find(coinId)
        return if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val insertedCoinId = cursor.getString(cursor.getColumnIndex(colCoinId))
                    cursor.close()
                    insertedCoinId
                } else {
                    getCoinByColID(insert(values))
                }
    }
}