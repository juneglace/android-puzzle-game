package kr.ac.ks.ap;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
public class View_PuzzleGameActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        DB=openOrCreateDatabase("DB_ClickGame2", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        DB.execSQL("create table IF NOT EXISTS TB_ClickGame (_id integer primary key autoincrement, username text, elapsedTime long, gameDate text)");
        imageButton_exit=(ImageButton) findViewById(R.id.imageButton_exit);
        imageButton_settings=(ImageButton) findViewById(R.id.imageButton_settings);
        imageButton_start=(ImageButton) findViewById(R.id.imageButton_start);
        imageButton_ranking=(ImageButton) findViewById(R.id.imageButton_ranking);
        imageButton_exit.setOnClickListener(listener);
        imageButton_settings.setOnClickListener(listener);
        imageButton_start.setOnClickListener(listener);
        imageButton_ranking.setOnClickListener(listener);
    }
    View.OnClickListener listener=new View.OnClickListener() { 
  public void onClick(View v) {
   switch(v.getId()){
   case R.id.imageButton_exit:
    finish();
    break;
   case R.id.imageButton_settings:
       startActivity(new Intent(View_PuzzleGameActivity.this, kr.ac.ks.ap.Settings.class));
    break;
   case R.id.imageButton_ranking:
       startActivity(new Intent(View_PuzzleGameActivity.this, kr.ac.ks.ap.RankingActivity.class));
    break;
   case R.id.imageButton_start:
       startActivity(new Intent(View_PuzzleGameActivity.this, kr.ac.ks.ap.GameActivity.class));
    break;
   }
  }
 };
 static SQLiteDatabase DB;
 ImageButton  imageButton_start, imageButton_settings, imageButton_exit, imageButton_ranking;
}
