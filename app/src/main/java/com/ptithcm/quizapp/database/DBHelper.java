package com.ptithcm.quizapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;


import com.ptithcm.quizapp.model.Answer;
import com.ptithcm.quizapp.model.Question;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by NgocTri on 11/7/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "QuizAppDB.db";
    public static final String DBLOCATION = "/data/data/com.ptithcm.quizapp/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DBHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public ArrayList<Question> getListQuestion(String currentSort) {
        Question qs = null;
        ArrayList<Question> qsList = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM Questions ORDER BY " + currentSort, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            qs = new Question(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), null);
            qsList.add(qs);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return qsList;
    }

    public boolean deleteQuestions(ArrayList<String> ids) {
        openDatabase();
        try {
            mDatabase.execSQL("DELETE FROM Answers WHERE questionID IN (" + TextUtils.join(", ", ids) + ")");
            mDatabase.execSQL("DELETE FROM Questions WHERE questionID IN (" + TextUtils.join(", ", ids) + ")");
            closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        }

        return true;
    }

    public boolean addQuestion_Answer(Question qs, ArrayList<Answer> answers) {
        boolean check = true;
        openDatabase();

        byte[] imageInPytes = null;
        try {
            Bitmap bitmap = qs.getQuestionImage();
            imageInPytes = getBytes(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put("questionContent", qs.getQuestionContent().toString());
            contentValues.put("questionType", Integer.valueOf(qs.getQuestionType()));
            contentValues.put("questionLevel", Integer.valueOf(qs.getQuestionLevel()));
            contentValues.put("exactAnswer", qs.getExactAnswer().toString());
            contentValues.put("questionImage", imageInPytes);

            mDatabase.insert("Questions", null, contentValues);
            String sql;
//            sql = "INSERT INTO Questions VALUES(null," + "'" + qs.getQuestionContent().toString() +
//                    "'," + qs.getQuestionType().toString() +
//                    "," + qs.getQuestionLevel().toString() +
//                    ",'" +qs.getExactAnswer().toString() + "',null)";
//            mDatabase.execSQL(sql);
            String qsID = getMaxIDQuestion();
            for (Answer ans : answers) {
                sql = "INSERT INTO Answers VALUES(null,'" + ans.getAnswerContent() + "'," + qsID + ")";
                mDatabase.execSQL(sql);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            check = false;
        }
        closeDatabase();
        return check;
    }

    public String getMaxIDQuestion() {
        String sql = "SELECT MAX(QuestionID) FROM Questions";
        Cursor cursor = mDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        String qsID = cursor.getString(0).toString();
        cursor.close();
        return qsID;
    }

    public boolean updateQuestion_Answer(Question qs, ArrayList<Answer> ans_new) {
        boolean check = true;
        byte[] imageInPytes = null;
        try {
            Bitmap bitmap = qs.getQuestionImage();
            imageInPytes = getBytes(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ArrayList<Answer> ans_old = new ArrayList<>(getAnswersByQuestionID(qs.getQuestionID()));
            openDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("questionContent", qs.getQuestionContent().toString());
            contentValues.put("questionType", Integer.valueOf(qs.getQuestionType()));
            contentValues.put("questionLevel", Integer.valueOf(qs.getQuestionLevel()));
            contentValues.put("exactAnswer", qs.getExactAnswer().toString());
            contentValues.put("questionImage", imageInPytes);
            mDatabase.update("Questions", contentValues, "questionID = " + qs.getQuestionID().toString(), null);
            String id;
            if(ans_old.size() <= ans_new.size()) {
                for (int i = 0; i < ans_old.size(); i++) {
                    contentValues = new ContentValues();
                    id = ans_old.get(i).getAnswerID();
                    contentValues.put("answerID", id);
                    contentValues.put("answerContent", ans_new.get(i).getAnswerContent());
                    contentValues.put("questionID", qs.getQuestionID());
                    mDatabase.update("Answers",contentValues,"answerID = " + id,null);
                }
                for (int i = ans_old.size(); i < ans_new.size(); i++) {
                    contentValues = new ContentValues();
                    id = ans_new.get(i).getAnswerID();
                    contentValues.put("answerID", id);
                    contentValues.put("answerContent", ans_new.get(i).getAnswerContent());
                    contentValues.put("questionID", qs.getQuestionID());
                    mDatabase.insert("Answers",null,contentValues);
                }
            }
            else {
                for (int i = 0; i < ans_new.size(); i++) {
                    contentValues = new ContentValues();
                    id = ans_old.get(i).getAnswerID();
                    contentValues.put("answerID", id);
                    contentValues.put("answerContent", ans_new.get(i).getAnswerContent());
                    contentValues.put("questionID", qs.getQuestionID());
                    mDatabase.update("Answers",contentValues,"answerID = " + id,null);
                }
                for (int i = ans_new.size(); i < ans_old.size(); i++) {
                    mDatabase.delete("Answers","answerID = " + ans_old.get(i).getAnswerID(),null);
                }
            }
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            check = false;
        }
        closeDatabase();
        return check;
    }


    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Question getQuestionByID(String questionID) {
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM Questions WHERE questionID = " + questionID, null);
        Question qs = new Question();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            byte[] byteArray = cursor.getBlob(5);
            if (byteArray != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                qs = new Question(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), bm);

            } else {
                qs = new Question(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), null);

            }

            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return qs;
    }

    public ArrayList<Answer> getAnswersByQuestionID(String questionID) {
        openDatabase();
        ArrayList<Answer> list = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM Answers WHERE questionID = " + questionID, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Answer(cursor.getString(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return list;
    }

}
