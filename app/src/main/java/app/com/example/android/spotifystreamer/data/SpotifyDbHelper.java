/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.com.example.android.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Manages a local database for spotify data.
 */
public class SpotifyDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "spotify.db";

    public SpotifyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRACKS_TABLE = "CREATE TABLE " + SpotifyContract.TracksEntry.TABLE_NAME + " (" +
                SpotifyContract.TracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SpotifyContract.TracksEntry.COLUMN_ARTISTS_ID + " INTEGER NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_ARTISTS_NAME + "TEXT NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_ARTISTS_IMAGE + "TEXT NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_TRACKS_ID + " INTEGER NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_TRACKS_NAME + " TEXT NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_ALBUM_IMAGE + " TEXT NOT NULL," +
                " UNIQUE (" + SpotifyContract.TracksEntry.COLUMN_TRACKS_ID + ") ON CONFLICT REPLACE);";

        Log.v("WeatherDBHelper", SQL_CREATE_TRACKS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_TRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.TracksEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
