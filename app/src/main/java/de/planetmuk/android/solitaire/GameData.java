package de.planetmuk.android.solitaire;

public class GameData {

	final static byte BACKGROUND = -1;
	final static byte EMPTY = 0;
	final static byte OCCUPIED = 1;

	final static byte NR_OF_ROWS = 7;
	final static byte NR_OF_COLS = 7;

	final static byte[][] gameBoard = new byte[NR_OF_ROWS][NR_OF_COLS];

	// row, col for the last field skipped over
	private static byte skipRow, skipCol;

	public static int moves;
	public static int possibleMoves;

	// initialize game board (background, marbles etc.)
	public static void resetGameBoard() {

		// set state of every gameBoard field to BACKGROUND, OCCUPIED or EMPTY
		for (int row = 0; row < NR_OF_ROWS; row++)
			for (int col = 0; col < NR_OF_COLS; col++)
				if (row < 2 && (col < 2 || col > 4) || row > 4
						&& (col < 2 || col > 4))
					gameBoard[row][col] = BACKGROUND;
				else
					gameBoard[row][col] = OCCUPIED;
		gameBoard[3][3] = EMPTY;

		skipRow = -1;
		skipCol = -1;

		// set total number of possible moves to its initial value
		possibleMoves = 4;

		// set number of moves to its initial value
		moves = 0;

	}

	public static boolean moveFromTo(byte fromRow, byte fromCol, byte toRow,
			byte toCol) {
		if (fromRow == toRow) {
			skipRow = fromRow;
			if (toCol > fromCol)
				skipCol = (byte) (fromCol + 1);
			else
				skipCol = (byte) (fromCol - 1);
		} else {
			skipCol = fromCol;
			if (toRow > fromRow)
				skipRow = (byte) (fromRow + 1);
			else
				skipRow = (byte) (fromRow - 1);
		}
		/*
		 * check all preconditions for a move: valid row AND col for origin AND
		 * target, move goes 2up OR 2down OR 2right OR 2left origin AND skipped
		 * field are OCCUPIED AND target is EMPTY
		 */
		if (fromRow >= 0
				&& fromRow < NR_OF_ROWS
				&& toRow >= 0
				&& toCol < NR_OF_COLS
				&& (Math.abs(toRow - fromRow) == 2 && fromCol == toCol || Math
						.abs(toCol - fromCol) == 2 && fromRow == toRow)
				&& gameBoard[fromRow][fromCol] == OCCUPIED
				&& gameBoard[skipRow][skipCol] == OCCUPIED
				&& gameBoard[toRow][toCol] == EMPTY) {
			// let's move ...
			gameBoard[fromRow][fromCol] = EMPTY;
			gameBoard[skipRow][skipCol] = EMPTY;
			gameBoard[toRow][toCol] = OCCUPIED;
			moves++;

			// re-compute possibleMoves
			possibleMoves = 0;
			for (byte row = 0; row < NR_OF_ROWS; row++)
				for (byte col = 0; col < NR_OF_COLS; col++)
					if (gameBoard[row][col] == OCCUPIED)
						possibleMoves += getNrOfPossibilities(row, col);
			return true;
		} else {
			skipRow = -1;
			skipCol = -1;
			return false;
		}
	}

	private static byte getNrOfPossibilities(byte row, byte col) {
		// check the neighbours around if they are possible target fields
		byte nr = 0;
		if (row - 2 >= 0 && gameBoard[row - 2][col] == EMPTY
				&& gameBoard[row - 1][col] == OCCUPIED)
			nr++;
		if (row + 2 < NR_OF_ROWS && gameBoard[row + 2][col] == EMPTY
				&& gameBoard[row + 1][col] == OCCUPIED)
			nr++;
		if (col - 2 >= 0 && gameBoard[row][col - 2] == EMPTY
				&& gameBoard[row][col - 1] == OCCUPIED)
			nr++;
		if (col + 2 < NR_OF_COLS && gameBoard[row][col + 2] == EMPTY
				&& gameBoard[row][col + 1] == OCCUPIED)
			nr++;
		return nr;
	}
}