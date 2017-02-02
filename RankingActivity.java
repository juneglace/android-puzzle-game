package kr.ac.ks.ap;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
public class RankingActivity extends Activity {
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.ranking);
  ListView listView1=(ListView) findViewById(R.id.listView1);
  Cursor  cursor=DB.rawQuery("select _id, username, gameDate from TB_ClickGame order by elapsedTime asc", null);
  SimpleCursorAdapter adapter=new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, new String []{"username", "gameDate"}, new int []{android.R.id.text1, android.R.id.text2});
  listView1.setAdapter(adapter);
 }
 SQLiteDatabase DB=View_PuzzleGameActivity.DB;
}
