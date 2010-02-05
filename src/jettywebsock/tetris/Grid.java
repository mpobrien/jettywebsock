package jettywebsock.tetris;
import java.util.*;
import org.apache.log4j.*;


public class Grid{
	Logger log = Logger.getLogger( Grid.class );

    public static final int NUMROWS = 20;
    public static final int NUMCOLS = 10;
	public enum Direction{ TOP, BOTTOM, LEFT, RIGHT; }

    private BitSet[] board;

	private final int numRows, numCols;

    public Grid(){//{{{
		this.numRows = NUMROWS;
		this.numCols = NUMCOLS;
        this.board = new BitSet[numRows];
        for( int i=0; i<board.length; i++ ){
            board[i] = new BitSet(numCols);
        }
    }//}}}

	public Grid(int squareSize, String s){//{{{
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
	}//}}}

    public Grid(int rows, int cols){//{{{
		this.numRows = rows;
		this.numCols = cols;
        this.board = new BitSet[rows];
        for( int i=0; i<board.length; i++ ){
            board[i] = new BitSet(cols);
        }
    }//}}}

    public boolean get(int row, int column){//{{{
        return board[row].get( column );
    }//}}}
    
    public void set(int row, int column){//{{{
        board[row].set( column );
    }//}}}
    
	public String toString(){//{{{
		StringBuilder output = new StringBuilder("");
		for( int i = 0; i <numRows; i++){
			for( int j=0; j<numCols; j++){
				output.append( get(i, j) ? "X" : "." );
			}
			output.append("\n");
		}
		return output.toString();
	}//}}}

	public List<Integer> encodeFull(){//{{{
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		for( int i=this.numRows-1; i>=0; i--){
			BitSet row = this.board[i];
			int encoded = 0;
			if( row.cardinality() != 0 ){
				for( int j=row.nextSetBit(0); j >= 0; j=row.nextSetBit(j+1) ){
					encoded |= (1 << j);
				}
			}
			resultList.add( encoded );
		}
		return resultList;
	}//}}}

	public List<Integer> encode(){//{{{
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		for( int i=this.numRows-1; i>=0; i--){
			BitSet row = this.board[i];
			if( row.cardinality() == 0 ) return resultList; //TODO empty() function?
			int encoded = 0;
			for( int j=row.nextSetBit(0); j >= 0; j=row.nextSetBit(j+1) ){
				encoded |= (1 << j);
			}
			resultList.add( encoded );
		}
		return resultList;
	}//}}}

	public int spaceCount( Direction d ){//{{{
		int count = 0;
		int incr = -1, startIndex = this.board.length - 1, stopIndex = -1;
		if( d == Direction.TOP || d == Direction.BOTTOM ){
			boolean top = (d == Direction.TOP);
			for( int i =  top ? 0 : this.board.length-1;
			         i != (top ? this.board.length : - 1);
					 i += top ? 1 : -1){
				if( this.board[i].cardinality() == 0 ) count++; //TODO empty() function?
				else break;
			}
			return count;
		}else{
			boolean left = (d == Direction.LEFT );
			for( int j = ( left ? 0 : numCols-1 ); j != ( left ? numCols : -1 ); j += ( left ? 1 : -1 ) ){
				for( int i=0; i < this.board.length; i++ ){
					if( get(i, j) ) return count;
				}
				count++;
			}
			return count;
		}
	}//}}}

	public int getNumRows(){ return this.numRows; }

	public int getNumCols(){ return this.numCols; }

	public BitSet getRowAt( int row ){//{{{
		return this.board[ row ];
	}//}}}

	public void addGrid( Grid g, int x, int y) throws Exception{ // throws an exception if the add is illegal//{{{
		for( int i=0; i < g.getNumRows(); i++ ){
			for( int j=0; j < g.getNumCols(); j++){
				if( x + j < 0 || x + j >= numCols ){
					if( g.get(i, j) ) throw new Exception("illegal add!");
					else continue;
				}
				if( y + i < 0 || y + i >= numRows ){
					if( g.get(i, j) ) throw new Exception("illegal add");
					else continue;
				}
				if( g.get(i,j) ){
					if( this.get(y+i, x+j ) ) throw new Exception("illegal add");
					else this.set(y+i, x+j);
				}
			}
		}
	}//}}}

}
