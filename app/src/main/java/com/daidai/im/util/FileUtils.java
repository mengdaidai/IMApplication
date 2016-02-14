package com.daidai.im.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.daidai.im.entity.FileEntity;

import java.io.File;

/**
 * Created by songs on 2016/2/12.
 */
public class FileUtils {
    public static FileEntity getFileEntity(Context context, Uri uri) {
        //FileEntity e = new FileEntity();

       /* if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data","_size","_id","_display_name"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int path_index = cursor.getColumnIndexOrThrow("_data");
                int size_index = cursor.getColumnIndexOrThrow("_size");
                int id_index = cursor.getColumnIndexOrThrow("_id");
                int display_index = cursor.getColumnIndexOrThrow("_display_name");
                if (cursor.moveToFirst()) {
                    String path = cursor.getString(path_index);
                    int size = cursor.getInt(size_index);
                    int id = cursor.getInt(id_index);
                    String display_name = cursor.getString(display_index);
                    e.setFile_length(size);
                    e.setFile_name(path);
                    e.setFile_suffix(path.substring(path.indexOf('.')+1));
                    return e;
                }
            } catch (Exception e1) {
                // Eat it
            }
        }*/

       /* else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }*/


        // DocumentProvider
        if ( DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            /*if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }*/
            // DownloadsProvider
            if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        /*else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }*/

        return null;
    }

    public static FileEntity getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        FileEntity e = new FileEntity();

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { "_data","_size" };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int path_index = cursor.getColumnIndexOrThrow(column);
                int size_index = cursor.getColumnIndexOrThrow("_size");
                String path = cursor.getString(path_index);
                int size = cursor.getInt(size_index);
                e.setFile_length(size);
                e.setFile_name(path);
                String suffix = path.substring(path.lastIndexOf('.')+1);
                e.setFile_suffix(suffix);
                return e;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
