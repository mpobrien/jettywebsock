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
	private static final Logger log = Logger.getLogger( WebSocketTesting.class );

	private final GameManager runningGames;

	@Inject 
	public WebSocketTesting( GameManager runningGames ){
		this.runningGames = runningGames;
	}

	private final Set<PlayerConnection> players = Collections.synchronizedSet( new HashSet<PlayerConnection>() );

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("regular old get");
		//getServletContext().getNamedDispatcher("default").forward(request,response);
	}

	protected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol){
		log.error("connection!");
		return new PlayerConnection( this.runningGames );
	}

}
