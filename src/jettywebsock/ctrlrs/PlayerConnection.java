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

public class PlayerConnection implements WebSocket {
	private static final Logger log = Logger.getLogger( PlayerConnection.class );

	public enum ConnectionState{//{{{
		UNCONNECTED,
		CONNECTED,
		INGAME;
	}//}}}

	private ConnectionState currentState;
	private boolean inGame = false;
	private Outbound outbound;
	private Grid playerGrid = new Grid();
	private Piece nextPiece;
	public static final Pattern updatePattern = Pattern.compile("x(\\-?\\d{1,2})y(\\-?\\d{1,2})i(\\d)");
	public static final Pattern connectPattern = Pattern.compile("g([0-9a-zA-Z\\-]*)");
	private final GameManager gm;
	private Game game;

	public void setState(ConnectionState state){//{{{
		this.currentState = state;
	}//}}}

	public PlayerConnection( GameManager gm ){//{{{
		this.gm = gm;
		this.currentState = ConnectionState.UNCONNECTED;
	}//}}}

	public void onConnect(Outbound outbound) {//{{{
		this.outbound = outbound;
		this.currentState = ConnectionState.CONNECTED;
		gm.addPlayer( this );
	}//}}}

	public void onMessage(byte frame, byte[] data, int offset, int length) {}

	public synchronized void sendPiece(){
		int nextPieceIndex = Piece.randomPieceNum();
		nextPiece = Piece.PIECES.get( nextPieceIndex );
		try{
			this.outbound.sendMessage( (byte)0, "p" + Integer.toString(nextPieceIndex) );
		}catch(Exception e){
			log.error("problem: ", e);
		}
	}

	public void onMessage(byte frame, String data) {//{{{
		log.error("got: " + data);
		if( currentState != ConnectionState.INGAME ){
			log.error("in here 1");
			Matcher cm = connectPattern.matcher(data);
			if( currentState == ConnectionState.CONNECTED && cm.matches() ){
				String gameId = cm.group(1);
				try{
					this.game = gm.putPlayerInGame( this, gameId );
				}catch(Exception e){
					log.error("error occurred this guy trying to get into game.", e);
					this.outbound.disconnect(); //TODO clean up references to this object.
				}
				log.error("in the game now: " + gameId);
				return;
			}else{
				this.outbound.disconnect(); //something bogus going on - disconnect this player
				//TODO clean up references to this object.
			}
		}
		Matcher m = updatePattern.matcher(data);
		if( m.matches() ){
			Integer x = Integer.parseInt( m.group(1) );
			Integer y = Integer.parseInt( m.group(2) );
			Integer i = Integer.parseInt( m.group(3) );
			try{
				this.playerGrid.addGrid( nextPiece.getGrid(i), x, y);
				this.playerGrid.clearLines();
				this.sendPiece();
				this.game.updateBoard(this, this.playerGrid );
			}catch(Exception e){
				log.error("CHEATER", e);
				log.error(" nextPiece: \n" + nextPiece );
			}
		}else{
			log.error("nope.");
		}
	}//}}}

	public void onDisconnect() {//{{{
		gm.removePlayer( this );
		//players.remove(this);
	}//}}}

	public void sendBoard(Grid g){
		try{
			String message = g.stringEncoded();
			this.outbound.sendMessage( (byte)0, "o" + message );
		}catch(Exception e){
			log.error(":(", e);
		}
	}

}//}}}
