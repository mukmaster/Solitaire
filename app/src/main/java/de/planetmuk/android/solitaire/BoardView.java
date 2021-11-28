package de.planetmuk.android.solitaire;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

	public BoardView(Context context) {
		super(context);

		// enable view to get focus, also in touch mode
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	// onDraw method creates graphics content on a canvas
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(GameActivity.TAG, "onDraw - method");

		// create a Paint object containing style properties for the background
		Paint bgPaint = new Paint();
		bgPaint.setColor(getResources().getColor(R.color.board_background));

		// draw background (a full screen rectangle) to the canvas
		canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

		// draw title text and footer text
		Paint textPaint = new Paint();
		textPaint.setColor(getResources().getColor(R.color.titletext_color));
		textPaint.setAntiAlias(true);
		// Normale Schriftgröße: 3% der Displayhöhe
		final float TEXTSIZE = getHeight()*0.03f;
		// Kopfzeile: Doppelt hohe Schrift
		textPaint.setTextSize(TEXTSIZE*2f);
		textPaint.setTextAlign(Align.CENTER);
		canvas.drawText("SOLITAIRE", getWidth()/2, 4f*TEXTSIZE, textPaint);
		// Fusszeile: Normale Schrift
		textPaint.setTextSize(TEXTSIZE);
		textPaint.setTextAlign(Align.LEFT);
		canvas.drawText(getResources().getString(R.string.nr_of_moves) + " "
				+ GameData.moves, TEXTSIZE, hView - TEXTSIZE, textPaint);
		textPaint.setTextAlign(Align.RIGHT);
		canvas.drawText(getResources().getString(R.string.possible_moves) + " "
				+ GameData.possibleMoves, getWidth()-TEXTSIZE, hView - TEXTSIZE, textPaint);

		// draw board from tiles
		Paint tileFgrdPaint = new Paint();
		tileFgrdPaint.setColor(Color.GRAY);
		Paint occTilePaint = new Paint();
		occTilePaint.setColor(Color.RED);
		Paint emptyTilePaint = new Paint();
		emptyTilePaint.setColor(Color.BLACK);
		Rect tileFgrd = new Rect();
		for (byte row = 0; row < GameData.NR_OF_ROWS; row++)
			for (byte col = 0; col < GameData.NR_OF_COLS; col++) {
				float leftUpperX = hBorder + row * t;
				float leftUpperY = vBorder + col * t;
				tileFgrd.set((int) (leftUpperX + 1), (int) (leftUpperY + 1),
						(int) (leftUpperX + t - 1), (int) (leftUpperY + t - 1));
				if (GameData.gameBoard[row][col] != GameData.BACKGROUND) {
					canvas.drawRect(tileFgrd, tileFgrdPaint);
				}
			}
		for (byte row = 0; row < GameData.NR_OF_ROWS; row++)
			for (byte col = 0; col < GameData.NR_OF_COLS; col++) {
				switch (GameData.gameBoard[row][col]) {
				case GameData.BACKGROUND:
					break;
				case GameData.EMPTY:
					canvas.drawCircle(hBorder + 0.5f * t + col * t, vBorder
							+ 0.5f * t + row * t, t / 4.0f, emptyTilePaint);
					break;
				case GameData.OCCUPIED:
					canvas.drawCircle(hBorder + 0.5f * t + col * t, vBorder
							+ 0.5f * t + row * t, t / 4.0f, occTilePaint);
					break;
				}
			}

		// draw the selection
		Paint selectedPaint = new Paint();
		selectedPaint.setColor(0x33ff0000);
		canvas.drawRect(selectedRect, selectedPaint);
	}

	private float t; // height of one square tile
	private float hBorder, vBorder;
	private int hView, wView;

	@Override
	protected void onSizeChanged(int wNew, int hNew, int wOld, int hOld) {
		hView = hNew;
		wView = wNew;
		int shorter, longer;
		if (hView > wView) {
			shorter = wView;
			longer = hView;
			t = shorter / (GameData.NR_OF_COLS + 1.0f);
			hBorder = 0.5f * t;
			vBorder = (longer - t * (GameData.NR_OF_ROWS + 1.0f)) / 2.0f;
		} else {
			shorter = hView;
			longer = wView;
			t = shorter / (GameData.NR_OF_COLS + 1.0f);
			hBorder = (longer - shorter + 1.0f) / 2.0f;
			vBorder = 15.0f;
		}
		getRect(selectedRow, selectedCol, selectedRect);
		Log.d(GameActivity.TAG, "BoardView - onSizeChanged to h=" + hNew
				+ ", w=" + wNew + ", t=" + t + ", hBorder=" + hBorder
				+ ", vBorder=" + vBorder);
		super.onSizeChanged(wNew, hNew, wOld, hOld);
	}

	private int selectedRow = 3;
	private int selectedCol = 3;
	private final Rect selectedRect = new Rect();
	private int selectedState = GameData.EMPTY;

	private void getRect(int row, int col, Rect rect) {
		rect.set((int) (hBorder + row * t + 1.0f),
				(int) (vBorder + col * t + 1.0f),
				(int) (hBorder + row * t + t - 1.0f), (int) (vBorder + col * t
						+ t - 1.0f));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(GameActivity.TAG, "onKeyDown: keycode=" + keyCode + ", event="
				+ event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			select(selectedRow, selectedCol - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			select(selectedRow, selectedCol + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			select(selectedRow - 1, selectedCol);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			select(selectedRow + 1, selectedCol);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		select((int) ((event.getY() - vBorder - 1f) / t), (int) ((event.getX()
				- hBorder - 1f) / t));
		Log.d(GameActivity.TAG, "onTouchEvent: row=" + selectedRow + ", col="
				+ selectedCol);
		return true;
	}

	private void select(int x, int y) {
		int newRow, newCol;
		newRow = Math.min(Math.max(x, 0), GameData.NR_OF_ROWS - 1);
		newCol = Math.min(Math.max(y, 0), GameData.NR_OF_COLS - 1);
		switch (GameData.gameBoard[newRow][newCol]) {
		case GameData.BACKGROUND: /* do not select background! */
			break;
		case GameData.EMPTY:
			if (selectedState == GameData.OCCUPIED
					&& GameData.moveFromTo((byte) selectedRow,
							(byte) selectedCol, (byte) newRow, (byte) newCol)) {
				// successful move
				selectedRow = newRow;
				selectedCol = newCol;
				selectedState = GameData.OCCUPIED;
			}
			break;
		case GameData.OCCUPIED:
			selectedState = GameData.OCCUPIED;
			// no move, just a new field selected
			selectedRow = newRow;
			selectedCol = newCol;
			break;
		}
		getRect(selectedCol, selectedRow, selectedRect);
		// invalidate (and therefore redraw) whole screen - not the best solution, but it works
		invalidate(new Rect(0, 0, wView, hView));
	}
}