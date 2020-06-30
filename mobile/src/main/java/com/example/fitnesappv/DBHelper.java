package com.example.fitnesappv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class DBHelper extends SQLiteOpenHelper {

    public static String[] GroupArr = {
        " ",
        "Ноги",
        "Спина",
        "Грудь",
        "Руки",
        "Плечи"};
    public static String[]  ComplexArr = {
        " ",
        "День1",
        "День2",
        "День3"
    };

    public static final int DATABASE_VERSION = 1;
    private static String DB_PATH = "";
    public static final String DB_NAME = "exercises.db";
    public static final String TABLE_EXERCISE = "exerciseTable";
    private SQLiteDatabase mDataBase;
    private Context mContext;
    private boolean mNeedUpdate = false;

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "_name";
    public static final String KEY_APPROACH = "_approach";
    public static final String KEY_GROUP = "_group";
    public static final String KEY_COMPLEX = "_complex";
    public  static final  String KEY_CONTENT = "_content";
    public  static final  String KEY_IMAGES = "_images_path";
    public  static final  String KEY_USERRECORD = "_user_record";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    /*public void updateDataBase() throws IOException {
        File dbFile = new File(DB_PATH + DB_NAME);
        if (dbFile.exists() && openDataBase()) {
            mDataBase.execSQL("create temporary table tmp_user_table (_id integer, _name text, _approach integer, _group text, _complex text, _images_path text);");
            mDataBase.execSQL("insert into tmp_user_table select _id, _name, _approach, _group, _complex, _images_path from userTable;");
            mDataBase.execSQL("drop table userTable;");
            dbFile.delete();
        }
        copyDataBase();
    }*/

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            updateExerciseTable(mContext, db);
        }
    }

    private void updateExerciseTable(Context context, SQLiteDatabase db) {
        // Подготовка к копированию обновленной базы данных из папки ресурсов
        InputStream is;
        OutputStream os;
        final String tempNewDbName = "temp_exercises.db";
        int buffer_size = 4096;
        byte[] buffer = new byte[buffer_size];
        String newDBPath = context.getDatabasePath(tempNewDbName).getPath();


        File tempDBFile = new File(newDBPath);
        // Если скопированная версия обновленной базы данных существует, удаляем ее
        if (tempDBFile.exists()) {
            tempDBFile.delete();
        }

        File newDBFileDirectory = tempDBFile.getParentFile();
        // На всякий случай создаем каталог баз данных
        if (!newDBFileDirectory.exists()) {
            newDBFileDirectory.mkdirs();
        }

        try {
            is = context.getAssets().open(DB_NAME);
            os = new FileOutputStream(tempDBFile);
            int bytes_read;
            while ((bytes_read = is.read(buffer,0, buffer_size)) > 0) {
                os.write(buffer);
            }
            os.flush();
            os.close();
            is.close();

        }catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ouch updated database not copied - processing stopped - see stack-trace above.");
        }

        try {
            db.delete(TABLE_EXERCISE, KEY_USERRECORD + " = ?", new String[]{"0"});
            db.execSQL("ATTACH DATABASE '" + DB_PATH + tempNewDbName + "' AS tempDb");
            db.execSQL("INSERT INTO main." + TABLE_EXERCISE + " SELECT * FROM tempDb." + TABLE_EXERCISE);
            db.execSQL("DETACH DATABASE tempDb"); // закрываем подключение второй базы данных

            db.setTransactionSuccessful();
            db.endTransaction();
            tempDBFile.delete();  // Удаляем скопированную базу данных, она больше не требуется
        } catch (SQLException ex){
            Log.println(Log.ERROR, "err", ex.getMessage());
        } finally {
            db.close(); // закрываем основное соединение
            close();
        }



    }
}
