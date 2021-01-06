package de.planetmuk.android.solitaire;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends Activity {

	// TAG - unique code name for android's logfile
	final static String TAG = "Solitaire Activity";

	//
	private BoardView boardView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// write a start message to android's debug logfile
		Log.d(TAG, "GameActivity - onCreate");

		GameData.resetGameBoard();
		// instantiate a new BoardView object for for the game board
		boardView = new BoardView(this);

		// take the board as activity's content and put focus on it
		setContentView(boardView);
		boardView.requestFocus();
	}
}