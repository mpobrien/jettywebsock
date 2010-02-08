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
			output.append( getRowAsString(i) );
			output.append("\n");
		}
		return output.toString();
	}//}}}

	public String getRowAsString( int rowNum ){
		StringBuilder output = new StringBuilder("");
		for( int j=0; j<numCols; j++){
			output.append( get(rowNum, j) ? "X" : "." );
		}
		return output.toString();
	}

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
		//int spaceLeft = g.spaceCount( Direction.LEFT );
		//int spaceRight = g.spaceCount( Direction.RIGHT );
		for( int i=0; i<g.getNumRows(); i++ ){
			if( y + i < 0 || y + i >= numRows && g.getRowAt(i).cardinality() > 0 ){
				throw new Exception("illegal add - out of vertical bound! " + i); // out of vertical bound
			}
// 			if( x < 0 ){
// 				if( spaceLeft + x < 0 ){
// 					throw new Exception("illegal add! - out of left horizontal bound");
// 				}
// 			}
// 			if( x + g.getNumCols() > this.getNumCols() ){
// 		       if( g.getRowAt(i).nextSetBit( x + g.getNumCols() - this.getNumCols() + 1 ) >= 0 ){
// 				   //log.error("x: " + x + " g.getNumCols(): " + g.getNumCols() + " 
// 					throw new Exception("illegal add! - out of right horizontal bound");
// 			   }
// 			}
			for( int j=0; j < g.getNumCols(); j++){
				if( g.get(i,j) ){
					if( x+j < 0 ) throw new Exception("illegal add  - out of left bound");
					if( x+j >= this.getNumCols() ) throw new Exception("illegal add  - out of right bound"); 
					if( this.get(y+i, x+j) ) throw new Exception("illegal add "  + x + " " + y + " " + i + " " + j + "\n" + g);
					else this.set(y+i, x+j);
				}
			}

			/*
			if( ){
			}
			BitSet rowPart = this.getRowAt(y+i).get(Math.max(0,x), x + g.getNumCols() );
			if( rowPart.intersects( g.getRowAt( i ) ) ){
				throw new Exception("illegal add!");
			}else if( y + i < 0 || y + i >= numRows && g.getRowAt( i ).cardinality() > 0 ){
				throw new Exception("illegal add!");
			}else if( x < 0 && (x + spaceLeft<0) ){
				throw new Exception("illegal add!");
			}else if( x + g.getNumCols() >= this.getNumCols() ){
				if( (x+g.getNumCols() - this.getNumCols()) - spaceRight < 0 ){
					throw new Exception("illegal add!");
				}
			}*/
		}
            /*for( i=0; i<m2.length; i++ ){
                if( ( m1[y+i] & (m2[i] << x )) > 0 ){
                    return false;
                }
				if( y+i < 0 || y+i >= numRows ){
					if( m2[i] > 0 ){
                        return false
                    }
				}
                mask =  (1 << (-x)) - 1;
                if( x < 0 ){
                    if( m2[i] & mask > 0 ){
                        return false;
                    }
                }
                if( x + w >= numCols ){
                    if(  (( mask << (numCols - w) ) & m2[i] ) > 0 ) return false
                }
            }


		for( int i=0; i < g.getNumRows(); i++ ){

			BitSet rowPart = this.get(x, this.numCols );
			if( rowPart.intersects( g.getRowAt( i ) );

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
		}*/
	}//}}}

	public void clearLines(){
		int numRowsEmpty = board.length - 1;
		BitSet[] newBoard = new BitSet[board.length];
		for( int i = board.length - 1; i >= 0; i-- ){
			if( board[i].cardinality() >= this.numCols ) continue;
			newBoard[ numRowsEmpty ] = board[i];
			numRowsEmpty--;
		}
        while( numRowsEmpty >= 0 ){
			newBoard[ numRowsEmpty-- ] = new BitSet();
		}
		this.board = newBoard;
	}

}
