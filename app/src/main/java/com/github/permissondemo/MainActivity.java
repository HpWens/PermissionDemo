package com.github.permissondemo;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

public class MainActivity extends CheckPermissionsActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String[] PROJECTION = new String[]{ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME};

    String mCurFilter;

    static String TAG;

    protected String[] needContactsPermissions = {
            Manifest.permission.READ_CONTACTS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TAG = getClass().getName();

        requestPermission();
    }

    private void requestPermission() {
        if (!mayRequestPermission(needContactsPermissions)) {
            return;
        }
        initLoader();
    }

    @Override
    public void requestPermissionResult(boolean allowPermission) {
        if (allowPermission) {
            initLoader();
        }
    }


    private void initLoader() {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri;
        if (mCurFilter != null) {

            baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI
                    , Uri.encode(mCurFilter));

        } else {
            baseUri = ContactsContract.Contacts.CONTENT_URI;
        }

        String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";

        CursorLoader loader = new CursorLoader(MainActivity.this, baseUri,
                PROJECTION, select, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

        return loader;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String[] names = cursor.getColumnNames();
            for (String str : names) {
                String contacts = cursor.getString(cursor.getColumnIndex(str));
                Log.e(TAG, "contacts=" + contacts);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
