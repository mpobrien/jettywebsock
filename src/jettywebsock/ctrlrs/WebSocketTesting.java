package jettywebsock.ctrlrs;
import java.util.*;
import org.eclipse.jetty.websocket.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.concurrent.*;
import com.google.inject.*;
import com.google.inject.servlet.*;
import jettywebsock.tetris.*;
import org.apache.log4j.*;
import java.util.regex.*;

@Singleton
public class WebSocketTesting extends WebSocketServlet {
	Logger log = Logger.getLogger( WebSocketTesting.class );
	public static final Pattern updatePattern = Pattern.compile("x(\\-?\\d{1,2})y(\\-?\\d{1,2})i(\\d)");

	private final Set<PlayerConnection> players = Collections.synchronizedSet( new HashSet<PlayerConnection>() );

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("regular old get");
		//getServletContext().getNamedDispatcher("default").forward(request,response);
	}

	protected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol){
		System.out.println("web socket connect.");
		return new PlayerConnection();
	}

	class PlayerConnection implements WebSocket {
		private boolean inGame = false;
		private Outbound outbound;
		private Grid playerGrid = new Grid();
		private Piece nextPiece;

		public void onConnect(Outbound outbound) {//{{{
			this.outbound = outbound;
			int nextPieceIndex = Piece.randomPieceNum();
			log.error( "next piece index: " + nextPieceIndex );
			nextPiece = Piece.PIECES.get( nextPieceIndex );
			try{
				this.outbound.sendMessage( (byte)0, "p" + Integer.toString(nextPieceIndex) );
			}catch(Exception e){
				log.error("problem: ", e);
			}
			//players.add(this);
		}//}}}

		public void onMessage(byte frame, byte[] data, int offset, int length) {}

		public void onMessage(byte frame, String data) {
			Matcher m = updatePattern.matcher(data);
			log.error("data: " +data);
			if( m.matches() ){
				Integer x = Integer.parseInt( m.group(1) );
				Integer y = Integer.parseInt( m.group(2) );
				Integer i = Integer.parseInt( m.group(3) );
				try{
					log.error("adding at x:" + x + ", y:" + y + " \n" + nextPiece.getGrid(i));
					this.playerGrid.addGrid( nextPiece.getGrid(i), x, y);
					this.playerGrid.clearLines();
					log.error( this.playerGrid );
					int nextPieceIndex = Piece.randomPieceNum();
					nextPiece = Piece.PIECES.get( nextPieceIndex );
					log.error("next piece is " + nextPieceIndex  + " \n" + nextPiece);
					try{
						this.outbound.sendMessage( (byte)0, "p" + Integer.toString(nextPieceIndex) );
					}catch(Exception e){
						log.error("problem: ", e);
					}
				}catch(Exception e){
					log.error("CHEATER", e);
					log.error(" nextPiece: \n" + nextPiece );
				}
			}else{
				log.error("nope.");
			}
		}

		public void onDisconnect() {
			players.remove(this);
		}
	}//}}}
}
