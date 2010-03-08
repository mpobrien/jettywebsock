package jettywebsock.tetris;
import com.google.inject.*;
import com.google.inject.servlet.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;
import jettywebsock.tetris.*;
import org.apache.log4j.*;
import org.eclipse.jetty.websocket.*;
import jettywebsock.ctrlrs.*;

@Singleton
public class GameManager{
	Map<String, Game> games = new Hashtable<String, Game>(); //threadsafe, muthafucka

	Set<PlayerConnection> unconnectedPlayers = Collections.synchronizedSet( new HashSet<PlayerConnection>() );

	public void createGame(String gameId){
		Game g = new Game(gameId);
		this.games.put(gameId, g);
	}

	public void addPlayer(PlayerConnection pc){
		this.unconnectedPlayers.add( pc );
	}

	public void removePlayer( PlayerConnection pc ){
		//NOT IMPLEMENTED;
	}

	public boolean isGameExisting(String gameId){
		return this.games.containsKey( gameId );
	}
    
	public Game putPlayerInGame(PlayerConnection pc, String gameId) throws Exception{
		Game g = this.games.get(gameId);
		if( g != null ){
			g.addPlayer( pc );
			return g;
		}else{
			throw new Exception("that game doesn't exist");
			// game doesn't exist. exception
		}
		//NOT IMPLEMENTED;
	}
}
