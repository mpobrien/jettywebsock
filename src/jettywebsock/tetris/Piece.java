package jettywebsock.tetris;
import java.util.*;
import com.google.common.collect.*;
import com.google.common.base.*;

public class Piece{

	//static data//{{{
    public static final int[] PIECE_SIZES = {3, 2, 3, 4, 3, 3, 3};
    public static final String[] PIECE_DATA = { "111010000001011001000010111100110100",
                                                "1111",
                                                "010010110100111000011010010000111001",
                                                "00001111000000000100010001000100",
                                                "000111100110010010001111000010010011",
                                                "001011010110011000",
                                                "011110000010011001" };
    public static final List<Piece> PIECES;//}}}
    
	// Setup piece data.   {{{ 
    static{
		ImmutableList.Builder<Piece> piecesList = new ImmutableList.Builder<Piece>();
		for( int i = 0; i< PIECE_SIZES.length; i++){
			int size = PIECE_SIZES[i];
			String pieceStr = PIECE_DATA[i];
			int numGrids =  pieceStr.length() / (size * size);
			int gridPartSize = size*size;
			Grid[] gridSet = new Grid[numGrids];
			int j=0, r=0;
			String pieceSlice;
			while( j < pieceStr.length() ){
				pieceSlice = pieceStr.substring(j, j + gridPartSize);
				gridSet[ r ] = new Grid( size, pieceSlice );
				j += gridPartSize;
				r++;
			}
			piecesList.add( new Piece( gridSet ) );
		}
		PIECES = piecesList.build();
    }//}}}

	private Grid[] grids;
    private static Random randGen = new Random( System.currentTimeMillis() );

    public Piece( Grid[] grids ){//{{{
		this.grids = grids;
	}//}}}

	public String toString(){//{{{
		StringBuilder output = new StringBuilder("");
		for( int i=0; i < this.grids[0].getNumRows(); i++ ){
			for( Grid grid : this.grids ){
				output.append( grid.getRowAsString(i) + "   " );
			}
			output.append("\n");
		}
		return output.toString();
	}//}}}

	public static void main(String args[]){//{{{
		System.out.println( PIECES.size() );
		for( Piece p : PIECES ){
			System.out.println( p );
			System.out.println( "------------------" );
		}
	}//}}}

	public static int randomPieceNum(){//{{{
		return randGen.nextInt( PIECES.size() );
	}//}}}

	public Grid getGrid(int gridNum){
		if( gridNum < 0 || gridNum > this.grids.length - 1) throw new IllegalArgumentException("invalid gridNum.");
		return this.grids[gridNum];
	}

	public String describe(){
		String res = "";
		for( Grid g : this.grids ){
			res += "[";
			for( int r = 0; r < g.getNumRows(); r++ ){
				int b = 0;
				for( int c = g.getRowAt(r).nextSetBit(0); c>=0; c=g.getRowAt(r).nextSetBit(c+1) ){
					b |= (1 << c);
				}
				res += Integer.toString( b );
			}
			res += "],";
		}
		return res;
	}

}
