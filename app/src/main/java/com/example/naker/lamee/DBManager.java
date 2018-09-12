package com.example.naker.lamee;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {
    public final static String TABLE_NAME = "USER";
    public final static String TABLE_NAME2 = "QNA";
    public final static String DATABASE_NAME = "lamee.db";
    public final int DB_VERSION = 1;

    SQLiteDatabase database;

    private static DBManager manager;
    private Context context;

    private DBManager(Context context){
        this.context = context;
    }

    public void open(){
        LameeHelper helper = new LameeHelper(context);
        database = helper.getWritableDatabase();
    }

    public void close(){
        if(database != null){
            database.close();
        }
    }

    public static DBManager getManager(Context context) {
        if(manager == null){
            manager = new DBManager(context);
        }
        return manager;
    }

    public Cursor rawQuery(String sql){
        return database.rawQuery(sql,null);
    }

    public boolean execSQl(String sql){
        try{
            database.execSQL(sql);
            return true;
        } catch(Exception e){
            Log.e("TAG",e.toString());
        }
        return false;
    }

    public Map<String,String> getData( String where){
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+where;

        Cursor cursor = rawQuery(sql);
        cursor.moveToNext();

        Map<String,String> map = new HashMap<>();
        map.put("_id",cursor.getInt(0)+"");
        map.put("name",cursor.getString(1));
        map.put("age",cursor.getString(2));

        return map;
    }

    public boolean insertUser(String username, int age){
        String sql = "INSERT INTO "+DBManager.TABLE_NAME+"(name, age) VALUES('"+username+"',"+age+")";

        return execSQl(sql);
    }

    public boolean insertRelation(String question, String name, String relation, int key){
        String sql = "INSERT INTO "+TABLE_NAME2+"(user_id,question,name,relation) VALUES("+key+"," +
                "'"+question+"'," +
                "'"+name+"'," +
                "'"+relation+"'" +
                ")";

        return execSQl(sql);
    }

    public List<Map<String,String>> getRelations(int key){
        String sql = "SELECT * FROM "+TABLE_NAME2+" WHERE user_id = "+key;

        Cursor result = rawQuery(sql);
        int cnt = result.getCount();

        List<Map<String,String>> list = new ArrayList<>();

        for(int i=0; i<cnt; i++){
            result.moveToNext();

            Map<String,String> map = new HashMap<>();
            map.put("id",result.getInt(0)+"");
            map.put("user_id",result.getInt(1)+"");
            map.put("question",result.getString(2));
            map.put("name",result.getString(3));
            map.put("relation",result.getString(4));

            list.add(map);
        }

        result.close();
        return list;
    }

    public List<Map<String,String>> duplicationList(String name){
        String sql = "SELECT "+TABLE_NAME+".name, GROUP_CONCAT("+TABLE_NAME2+".name,','), GROUP_CONCAT(relation,','), "+TABLE_NAME+"._id FROM "+TABLE_NAME+" join " +
                TABLE_NAME2+" on "+TABLE_NAME+"._id = "+TABLE_NAME2+".user_id WHERE "+TABLE_NAME+".name = '"+name+"' GROUP BY "+TABLE_NAME+".name";

        Cursor cursor = rawQuery(sql);

        int cnt = cursor.getCount();

        List<Map<String,String>> result = new ArrayList<>();

        for(int i=0; i<cnt;i++){
            cursor.moveToNext();
            Map<String,String> map = new HashMap<>();

            map.put("name",cursor.getString(0));
            map.put("people",cursor.getString(1));
            map.put("relations",cursor.getString(2));
            map.put("_id",cursor.getInt(3)+"");

            result.add(map);
        }

        return result;
    }

    private class LameeHelper extends SQLiteOpenHelper {


        public LameeHelper(Context context) {
            super(context,DATABASE_NAME,null,DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE "+TABLE_NAME+"(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name text," +
                    "age INTEGER" +
                    ")";

            String sql2 = "CREATE TABLE "+TABLE_NAME2+"(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "question text," +
                    "name text," +
                    "relation text," +
                    "FOREIGN KEY(user_id) REFERENCES "+TABLE_NAME+"(_id)" +
                    ")";

            db.execSQL(sql);
            db.execSQL(sql2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            String sql = "DROP TABLE if exists "+TABLE_NAME;
            db.execSQL(sql);
            onCreate(db);
        }
    }

}
