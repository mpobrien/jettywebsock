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

@Singleton
public class WebSocketTesting extends WebSocketServlet {
	Logger log = Logger.getLogger( WebSocketTesting.class );

	private final Set<PlayerConnection> players = Collections.synchronizedSet( new HashSet<PlayerConnection>() );

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("regular old get");
		//getServletContext().getNamedDispatcher("default").forward(request,response);
	}

	protected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol){
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
// 			for (PlayerConnection member : members) {
// 				try {
// 					member.outbound.sendMessage(frame,data);
// 				} catch(IOException e) {
// 					e.printStackTrace();
// 				}
// 			}
		}

		public void onDisconnect() {
			players.remove(this);
		}
	}//}}}
}
