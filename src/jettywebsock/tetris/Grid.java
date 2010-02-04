package jettywebsock.tetris;
import java.util.*;

public class Grid{

    public static final int NUMROWS = 20;
    public static final int NUMCOLS = 10;

    private BitSet[] board;

	private final int numRows, numCols;

    public Grid(){
		this.numRows = NUMROWS;
		this.numCols = NUMCOLS;
        this.board = new BitSet[numRows];
        for( int i=0; i<board.length; i++ ){
            board[i] = new BitSet(numCols);
        }
    }

	public Grid(int squareSize, String s){
		this.numRows = squareSize;
		this.numCols = squareSize;
        this.board = new BitSet[squareSize];
        for( int i=0; i<board.length; i++ ){
            board[i] = new BitSet(squareSize);
			for( int j=0; j<squareSize; j++ ){
				if( s.charAt( (i*squareSize) + j ) == '1' ){
					board[i].set( j );
				}
			}
        }
	}

    public Grid(int rows, int cols){
		this.numRows = rows;
		this.numCols = cols;
        this.board = new BitSet[rows];
        for( int i=0; i<board.length; i++ ){
            board[i] = new BitSet(cols);
        }
    }

    public boolean get(int row, int column){
        return board[row].get( column );
    }
    
    public void set(int row, int column){
        board[row].set( column );
    }
    
	public String toString(){
		StringBuilder output = new StringBuilder("");
		for( int i = 0; i <numRows; i++){
			for( int j=0; j<numCols; j++){
				output.append( get(i, j) ? "X" : "." );
			}
			output.append("\n");
		}
		return output.toString();
	}

}
