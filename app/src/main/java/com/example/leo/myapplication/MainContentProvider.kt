package com.example.leo.myapplication

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager

class MainContentProvider : ContentProvider() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(): Boolean {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle = when (method) {
        Key.serviceSwitch -> Bundle().apply {
            putBoolean(Key.serviceSwitch, preferences.getBoolean(Key.serviceSwitch, false))
        }
        Key.hooks -> Bundle().apply {
            putString(Key.hooks,
                context!!.openFileInput(Const.CONFIG_FILENAME).bufferedReader().use { it.readText() })
        }
        else -> Bundle.EMPTY
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("not implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("not implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("not implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("not implemented")
    }
}
