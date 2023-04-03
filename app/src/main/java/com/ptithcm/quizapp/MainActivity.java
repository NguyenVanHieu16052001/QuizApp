package com.ptithcm.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.ptithcm.quizapp.adapter.CustomAdapterQuestion;
import com.ptithcm.quizapp.database.DBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DatabaseReference mDatabase;
    private SearchView svSearch_tb;
    private SQLiteDatabase database = null;
    private String currentSort = "questionID";
    private DBHelper dbHelper;

    FloatingActionButton fabAddNewQS;
    RecyclerView rvListQS;
    CustomAdapterQuestion adapterQS;
    SwipeRefreshLayout srfRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        svSearch_tb = (SearchView) menu.findItem(R.id.svSearch_tb).getActionView();
        svSearch_tb.setQueryHint("Search Here....");
        svSearch_tb.setMaxWidth(Integer.MAX_VALUE);
        svSearch_tb.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterQS.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterQS.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void setEvent() {
        copyData();
        getData(currentSort);
        fabAddNewQS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewQuestion.class);
                startActivity(intent);
            }
        });
        srfRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(currentSort);
                srfRefresh.setRefreshing(false);
            }
        });
    }

    private void getData(String currentSort) {
        adapterQS = new CustomAdapterQuestion(this, dbHelper.getListQuestion(currentSort));
        rvListQS.setAdapter(adapterQS);
        adapterQS.notifyDataSetChanged();
    }

    private void setControl() {
        rvListQS = findViewById(R.id.rvListQS);
        fabAddNewQS = findViewById(R.id.fabtbAdd);
        srfRefresh = findViewById(R.id.srfRefresh);
        dbHelper = new DBHelper(this);
    }

    private void copyData() {
        dbHelper.getReadableDatabase();
        File dbFile = getDatabasePath(dbHelper.DBNAME);
        if (dbFile.exists()) {
//            dbFile.delete();
            return;
        }
        try {
            InputStream myInput = getAssets().open(dbHelper.DBNAME);
            String outFile = getApplicationInfo().dataDir + "/databases/" + dbHelper.DBNAME;
            File f = new File(getApplicationInfo().dataDir + "/databases/");
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream myOutPut = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = myInput.read(buffer)) > 0) {
                myOutPut.write(buffer, 0, len);
            }
            myOutPut.flush();
            myInput.close();
            myOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Loi sao chep DB!", Toast.LENGTH_SHORT).show();
        }
        database = openOrCreateDatabase(dbHelper.DBNAME, MODE_PRIVATE, null);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnExit_tb:
                adapterQS.showWarningDialog(MainActivity.this.getResources().getString(R.string.exit));
                break;
            case R.id.btnSortByID: {
                currentSort = "questionID";
                getData(currentSort);
                break;
            }
            case R.id.btnSortByContent: {
                currentSort = "questionContent";
                getData(currentSort);
                break;
            }
            case R.id.btnSortByLever: {
                currentSort = "questionLever";
                getData(currentSort);
                break;
            }
            case R.id.btnAdd_tb: {
                Intent intent = new Intent(MainActivity.this, NewQuestion.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!svSearch_tb.isIconified()) {
            svSearch_tb.setIconified(true);
            return;
        }
        super.onBackPressed();

    }
}