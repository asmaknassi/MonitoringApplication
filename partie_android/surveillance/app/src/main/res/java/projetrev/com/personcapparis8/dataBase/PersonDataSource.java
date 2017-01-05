package projetrev.com.personcapparis8.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import projetrev.com.personcapparis8.Point;


public class PersonDataSource {

    // Champs de la base de données
    private SQLiteDatabase database;
    private SQLliteConnexion dbHelper;

    private String[] allColumns = { SQLliteConnexion.COLUMN_ID,
            SQLliteConnexion.COLUMN_DISTANCE,SQLliteConnexion.COLUMN_DATE, SQLliteConnexion.COLUMN_IMAGE};

    public PersonDataSource(Context context) {
        dbHelper = new SQLliteConnexion(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public personClass createPerson(String distance,String datePhoto,String image ) {
        ContentValues values = new ContentValues();
        values.put(SQLliteConnexion.COLUMN_DISTANCE, distance);
        values.put(SQLliteConnexion.COLUMN_DATE, datePhoto);
        values.put(SQLliteConnexion.COLUMN_IMAGE, image);
       // values.put(SQLliteConnexion.COLUMN_IMAGE, uri);
        long insertId = database.insert(SQLliteConnexion.TABLE_PERSON, null,
                values);
        Cursor cursor = database.query(SQLliteConnexion.TABLE_PERSON,
                allColumns, SQLliteConnexion.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        personClass newPerson = cursorToPerson(cursor);
        cursor.close();
        return newPerson;
    }
    public personClass createPerson(String distance) {
        ContentValues values = new ContentValues();
        values.put(SQLliteConnexion.COLUMN_DISTANCE, distance);

        // values.put(SQLliteConnexion.COLUMN_IMAGE, uri);
        long insertId = database.insert(SQLliteConnexion.TABLE_PERSON, null,
                values);
        Cursor cursor = database.query(SQLliteConnexion.TABLE_PERSON,
                allColumns, SQLliteConnexion.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        personClass newPerson = cursorToPerson(cursor);
        cursor.close();
        return newPerson;
    }

    public void deleteContact(personClass contact) {
        long id = contact.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(SQLliteConnexion.TABLE_PERSON, SQLliteConnexion.COLUMN_ID
                + " = " + id, null);
    }

    public LinkedList<personClass> getAllPerson() {
        LinkedList<personClass> persons = new LinkedList<>();

        Cursor cursor = database.query(SQLliteConnexion.TABLE_PERSON,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            personClass newPersons = cursorToPerson(cursor);
            persons.add(newPersons);
           // Log.e("fffffffffff name" + cursor.getString(1), "numero :" + cursor.getString(2));
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return persons;
    }

    private personClass cursorToPerson(Cursor cursor) {
        personClass person = new personClass();
        person.setId(cursor.getLong(0));
        person.setDistance(cursor.getString(1));
        person.setDate(cursor.getString(2));
        person.setImage_name(cursor.getString(3));
        return person;
    }
}
