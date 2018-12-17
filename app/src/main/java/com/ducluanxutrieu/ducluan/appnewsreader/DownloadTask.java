package com.ducluanxutrieu.ducluan.appnewsreader;

import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        URL url;
        String result = "";
        HttpURLConnection httpURLConnection = null;

        try {
            url = new URL(urls[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();

            while (data != -1){
                char current = (char) data;
                result += current;
                data = inputStreamReader.read();
            }

            JSONArray jsonArray = new JSONArray(result);

            int NumberOfItems = 20;
            if (jsonArray.length() < 20){
                NumberOfItems = jsonArray.length();
            }

            MainActivity.articleDB.execSQL("DELETE FROM articles");

            for (int i = 0; i < NumberOfItems; i++){
               // Log.i("title and URL", i + "cc");
                String articleID = jsonArray.getString(i);
                String articleTitle = "";
                String articleUrl = "";

                url = new URL("https://hacker-news.firebaseio.com/v0/item/"+ articleID +".json?print=pretty");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);

                data = inputStreamReader.read();
                String article = "";

                while (data != -1){
                    char current = (char) data;
                    article += current;
                    data = inputStreamReader.read();
                }


                JSONObject jsonObject = new JSONObject(article);

                if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                    articleTitle = jsonObject.getString("title");
                    articleUrl = jsonObject.getString("url");
                    //Log.i("title and URL", articleTitle + ", URL: " + articleUrl);
                    //onPostExecute(articleTitle);
/*                    url = new URL(articleUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    inputStream = httpURLConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);
                    data = inputStreamReader.read();
                    String articleContent = "";
                    int temp = 0;
                    while (data != -1){
                        char current = (char) data;
                        articleContent += current;
                        data = inputStreamReader.read();
                        if (temp % 1000 == 0){
                            Log.i("title and URL", temp +"");
                        }
                        temp ++;
                    }*/
                    //Log.i("title and URL", "Temp1");

                    //Log.i("HTML", articleContent);

                    String sql = "INSERT INTO articles (articleId, title, url) VALUES (?, ?, ?)";
                    SQLiteStatement statement = MainActivity.articleDB.compileStatement(sql);
                    statement.bindString(1, articleID);
                    statement.bindString(2, articleTitle);
                    statement.bindString(3, articleUrl);
                    statement.execute();
                    Log.i("titles", articleTitle);
                }

            }

            //Log.i("Result DownloadTask", result);
            //Log.i("title and URL", result + "Result");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
       /* MainActivity.listTitle.add(s);
        //Log.i("title and URL", "listTile: " + MainActivity.listTitle.toString());
        MainActivity.arrayAdapter.notifyDataSetChanged();*/
        //Log.i("title and URL", "listTile: " + MainActivity.listTitle.toString());
       MainActivity.UpdateListView();
    }
}
