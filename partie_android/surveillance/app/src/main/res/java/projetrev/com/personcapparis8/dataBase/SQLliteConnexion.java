package projetrev.com.personcapparis8.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLliteConnexion extends SQLiteOpenHelper {

    public static final String TABLE_PERSON = "persons";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IMAGE = "imagename";
    private static final String DATABASE_NAME = "PersonBase.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la base de données
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PERSON + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_DISTANCE
            + " text not null," + COLUMN_DATE + " text not null , " + COLUMN_IMAGE +
            " text not null);";
    public SQLliteConnexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLliteConnexion.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
        onCreate(db);
    }

}
