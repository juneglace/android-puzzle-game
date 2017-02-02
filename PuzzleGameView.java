package kr.ac.ks.ap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
public class PuzzleGameView extends View {
 Context context;
 Bitmap puzzle_background_pinocchio;
 int gLT; // gridLineThickness = getWidth()/60;
 int bW, bH; // boxWidth, boxHeight
 String difficulty;
 int COLS, ROWS;
 Box box[];
 int blankBoxIndex;
 SharedPreferences pref;
 long delayMillis = 1000;
 long startTime = 0, endTime = 0;
 public PuzzleGameView(Context context) {
  super(context);
  this.context = context;
  this.pref = PreferenceManager.getDefaultSharedPreferences(context);
  this.difficulty = pref.getString("clickgame_difficulty", "easy");
  if (difficulty.equals("easy")) {
   COLS = 4;
   ROWS = 3;
  }
  if (difficulty.equals("medium")) {
   COLS = 5;
   ROWS = 4;
  }
  if (difficulty.equals("hard")) {
   COLS = 6;
   ROWS = 5;
  }
  
   box = new Box[COLS * ROWS];
   blankBoxIndex = box.length - 1;
 }
 @Override
 protected void onSizeChanged(int w, int h, int oldw, int oldh) {
  super.onSizeChanged(w, h, oldw, oldh);
  puzzle_background_pinocchio = BitmapFactory.decodeResource(getResources(),
    R.drawable.puzzle_background_pinocchio);
  puzzle_background_pinocchio = Bitmap.createScaledBitmap(
    puzzle_background_pinocchio, w, h, false);
  gLT = getWidth() / 60;
  bW = (getWidth() - (COLS + 1) * gLT) / COLS;
  bH = (getHeight() - (ROWS + 1) * gLT) / ROWS;
  int i = 0;
  for (int r = 0; r < ROWS; r++) {
   for (int c = 0; c < COLS; c++) {
    int left = (gLT + bW) * c + gLT;
    int top = (gLT + bH) * r + gLT;
    int right = left + bW;
    int bottom = top + bH;
    box[i] = new Box();
    box[i].rect = new Rect(left, top, right, bottom);
    box[i].bitmap = Bitmap.createBitmap(puzzle_background_pinocchio,
      left, top, bW, bH);
    i++;
   }
  }
  shuffle();
  startTime = System.currentTimeMillis();
  handler.sendEmptyMessageDelayed(0, delayMillis);
 }
 Handler handler = new Handler() {
  public void handleMessage(Message msg) {
   sendEmptyMessageDelayed(0, delayMillis);
  }
 };
 private void shuffle() {
  Random random = new Random();
  do {
   for (int i = 0; i < 10; i++) {
    swap(box[random.nextInt(box.length - 1)], box[blankBoxIndex]);
   }
  } while (isCompleted() == true);
  invalidate();
 }
 protected void onDraw(Canvas canvas) {
  
  Paint paint = new Paint();
  paint.setColor(Color.CYAN);
  canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
  paint.setColor(Color.BLACK);
  for (int i = 0; i < box.length; i++) {
   if (i == blankBoxIndex) {
    canvas.drawRect(box[i].rect.left, box[i].rect.top,
      box[i].rect.right, box[i].rect.bottom, paint);
   } else {
    canvas.drawBitmap(box[i].bitmap, box[i].rect.left,
      box[i].rect.top, null);
   }
  }
  if (isCompleted() == false)
   return;
  showDialog();
 }
 private void showDialog() {
  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
  endTime = System.currentTimeMillis();
  long elapsedTime = endTime - startTime;
  SQLiteDatabase DB = View_PuzzleGameActivity.DB;
  String username = pref.getString("clickgame_username", "guest");
  String gameDate = String.format("%,3d", elapsedTime)
    + " ms."
    + "\t\t"
    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
      System.currentTimeMillis()));
  DB.execSQL("insert into TB_ClickGame (username, elapsedTime, gameDate) values ('"
    + username + "', " + elapsedTime + ", '" + gameDate + "')");
  dialog.setTitle("Congratulations!");
  dialog.setMessage(username + " did it in "
    + String.format("%,3d", elapsedTime) + " ms.");
  dialog.setPositiveButton("Continue",
    new DialogInterface.OnClickListener() {
     public void onClick(DialogInterface dialog, int which) {
      shuffle();
      dialog.dismiss();
     }
    });
  dialog.setNegativeButton("Cancel",
    new DialogInterface.OnClickListener() {
     public void onClick(DialogInterface dialog, int which) {
      dialog.dismiss();
       ((GameActivity)context).finish();
     }
    });
  dialog.show();
 }
 private boolean isCompleted() {
  for (int r = 0; r < ROWS - 1; r++) {
   for (int c = 0; c < COLS; c++) {
    if (box[r * COLS + c].rect.top >= box[(r + 1) * COLS + c].rect.top)
     return false;
   }
  }
  for (int c = 0; c < COLS - 1; c++) {
   for (int r = 0; r < ROWS; r++) {
    if (box[r * COLS + c].rect.left >= box[r * COLS + (c + 1)].rect.left)
     return false;
   }
  }
  return true;
 }

 public boolean onTouchEvent(MotionEvent event) {
  if (event.getAction() == MotionEvent.ACTION_DOWN) {
   for (int i = 0; i < box.length; i++) {
    if (box[i].rect
      .contains((int) event.getX(), (int) event.getY())) {
     swap(box[i], box[blankBoxIndex]);
     break;
    }
   }
   invalidate();
  }
  return true;
 }
 private void swap(Box box1, Box box2) {
  if (box1.rect.left == box2.rect.left
    && Math.abs(box1.rect.top - box2.rect.top) < 1.5 * bH
    || box1.rect.top == box2.rect.top
    && Math.abs(box1.rect.left - box2.rect.left) < 1.5 * bW) {
   Rect rect1 = new Rect(box1.rect);
   box1.rect = box2.rect;
   box2.rect = rect1;
  }
 }
}
