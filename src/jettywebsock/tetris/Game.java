package jettywebsock.tetris;
import jettywebsock.tetris.*;
import jettywebsock.ctrlrs.*;
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

public class Game{

	private static final Logger log = Logger.getLogger( Game.class );

	public enum GameState{//{{{
		EMPTY,
		WAITING_ONEPLAYER,
		FULL;
	}//}}}

    private PlayerConnection player1 = null;
    private PlayerConnection player2 = null;
    private final String gameId;
	private GameState state;

    public Game(String gameId){
        this.gameId = gameId;
		this.state = GameState.EMPTY;
    }

	public synchronized void addPlayer(PlayerConnection player) throws Exception{
		log.error("adding player: " + player);
		log.error("currently have: " +  this.player1 + " " + this.player2);
		if( this.player1 == null ){
			log.error("filling player1 spot");
			this.player1 = player;
			if( this.player2 == null ){
				this.state = GameState.WAITING_ONEPLAYER;
			}else{
				this.state = GameState.FULL;
			}
		}else if( this.player2 == null ){

			log.error("filling player2 spot");
			this.player2 = player;
			this.state = GameState.FULL;
		}else{
			throw new Exception("game already full."); // game already full, throw exception
		}

		log.error("now " +  this.player1 + " " + this.player2);
		if( this.player1 != null && this.player2 != null ){
			log.error("game is full and ready!");
			this.startGame();
		}
	}

	private void startGame(){
		player1.setState( PlayerConnection.ConnectionState.INGAME );
		player2.setState( PlayerConnection.ConnectionState.INGAME );
		player1.sendPiece();
		player2.sendPiece();
	}

	public void updateBoard(PlayerConnection player, Grid board){
		log.error("updating");
		if( player != player1 ){
			player1.sendBoard(board);
		}else if(player != player2){
			player2.sendBoard(board);
		}else{
			log.error("no update");
		}
	}

}
