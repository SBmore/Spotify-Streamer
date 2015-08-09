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
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                SpotifyContract.TracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                SpotifyContract.TracksEntry.COLUMN_ARTISTS_KEY + " INTEGER NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_TRACKS_NAME + " TEXT NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                SpotifyContract.TracksEntry.COLUMN_ALBUM_IMAGE + " TEXT NOT NULL," +
                SpotifyContract.TracksEntry.COLUMN_TRACKS_ID + " INTEGER NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + SpotifyContract.TracksEntry.COLUMN_ARTISTS_KEY + ") REFERENCES " +
                SpotifyContract.ArtistEntry.TABLE_NAME + " (" + SpotifyContract.ArtistEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + SpotifyContract.TracksEntry.COLUMN_TRACKS_ID + ") ON CONFLICT REPLACE);";

        Log.v("WeatherDBHelper", SQL_CREATE_TRACKS_TABLE);

        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + SpotifyContract.ArtistEntry.TABLE_NAME + " (" +
                SpotifyContract.ArtistEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this weather data
                SpotifyContract.ArtistEntry.COLUMN_ARTISTS_SETTING + " TEXT NOT NULL UNIQUE, " +
                SpotifyContract.ArtistEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                SpotifyContract.ArtistEntry.COLUMN_ARTISTS_IMAGE + " TEXT NOT NULL, " +
                SpotifyContract.ArtistEntry.COLUMN_ARTISTS_ID + " REAL NOT NULL);";

        Log.v("WeatherDBHelper", SQL_CREATE_ARTISTS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_TRACKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.ArtistEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.TracksEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
