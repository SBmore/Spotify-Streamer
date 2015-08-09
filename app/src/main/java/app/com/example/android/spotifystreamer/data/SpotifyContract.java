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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the spotify database.
 */
public class SpotifyContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.spotifystreamer.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTISTS = "artists";
    public static final String PATH_TRACKS = "tracks";

    // Inner class that defines the table contents of the artists table
    public static final class ArtistEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;

        public static Uri buildArtistsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "artists";

        // The artists setting string is what will be sent to the spotify api
        public static final String COLUMN_ARTISTS_SETTING = "artists";
        public static final String COLUMN_ARTIST_NAME = "artists_name";
        public static final String COLUMN_ARTISTS_IMAGE = "artists_image";
        public static final String COLUMN_ARTISTS_ID = "artists_id";
    }

    /* Inner class that defines the table contents of the tracks table */
    public static final class TracksEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKS;


        public static Uri buildTracksUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildArtistsTracks(String ArtistsSetting) {
            return CONTENT_URI.buildUpon().appendPath(ArtistsSetting).build();
        }

        public static Uri buildArtistsTracksWithImage(String locationSetting, String image) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_ALBUM_IMAGE, image).build();
        }

        public static String getArtistsSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME = "tracks";

        public static final String COLUMN_ARTISTS_KEY = "artists_id"; // foreign key
        public static final String COLUMN_TRACKS_NAME = "tracks_name";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_ALBUM_IMAGE = "album_image";
        public static final String COLUMN_TRACKS_ID = "album_id";

    }
}
