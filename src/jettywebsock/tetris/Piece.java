package jettywebsock.tetris;
import java.util.*;

public class Piece{

    public static final int[] PIECE_SIZES = {3, 2, 3, 4, 3, 3, 3};
    public static final String[] PIECE_DATA = { "111010000001011001000010111100110100",
                                                "1111",
                                                "010010110100111000011010010000111001",
                                                "00001111000000000100010001000100",
                                                "000111100110010010001111000010010011",
                                                "001011010110011000",
                                                "011110000010011001" };
    public static final List<Piece> PIECES = new ArrayList();
    static{
		for( int i = 0; i< PIECE_SIZES.length; i++){
			int size = PIECE_SIZES[i];
			String pieceStr = PIECE_DATA[i];
			int numGrids =  pieceStr.length() / (size * size);
			int gridPartSize = size*size;
			Grid[] gridSet = new Grid[numGrids];
// 			for( int j=0, int r=0, String pieceSlice = pieceStr.substring(0,gridPartSize); j<pieceStr.length(); j+=gridPartSize, r++, pieceSlice=pieceStr.substring(j,j+gridPartSize)){
// 				gridSet[ r ] = new Grid( size, pieceSlice );
// 			} 

			int j=0, r=0;
			String pieceSlice;
			while( j < pieceStr.length() ){
				pieceSlice = pieceStr.substring(j, j + gridPartSize);
				gridSet[ r ] = new Grid( size, pieceSlice );
				j += gridPartSize;
				r++;
			}
			PIECES.add( new Piece( gridSet ) );
		}

    }


	private Grid[] grids;

    public Piece( Grid[] grids ){
		this.grids = grids;
	}

	public String toString(){
		StringBuilder output = new StringBuilder("");
		for( Grid grid : this.grids ){
			output.append( grid.toString() + "\n" );
		}
		return output.toString();
	}

	public static void main(String args[]){
		System.out.println( PIECES.size() );
		for( Piece p : PIECES ){
			System.out.println( p );
			System.out.println( "------------------" );
		}
	}

}
