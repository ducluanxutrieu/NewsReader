package com.ducluanxutrieu.ducluan.appnewsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayAdapter arrayAdapter;
    static ArrayList<String> listTitle;
    static ArrayList<String> listContent;
    DownloadTask task;
    static SQLiteDatabase articleDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listTitle = new ArrayList<>();
        listContent = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listTitle);
        task = new DownloadTask();


        articleDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);

        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId, INTEGER, title VARCHAR, content VARCHAR)");



        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        }catch (Exception e){
            e.printStackTrace();
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("url", listContent.get(position));
                startActivity(intent);
            }
        });

        UpdateListView();
    }



    static public void UpdateListView(){
        Cursor c = articleDB.rawQuery("SELECT * FROM articles", null);
        int title = c.getColumnIndex("title");
        int url = c.getColumnIndex("url");
        //Log.i("title temp", articleDB.execSQL("SELECT * FROM articles", null) + "");

        if (c.moveToFirst()){
            listTitle.clear();
            listContent.clear();

            do {
                listTitle.add(c.getString(title));
                //Log.i("title temp", c.getString(title));
                listContent.add(c.getString(url));
            }while (c.moveToFirst());
        }
        arrayAdapter.notifyDataSetChanged();
    }


}
