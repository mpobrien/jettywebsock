package jettywebsock.ctrlrs;
import java.util.*;
import org.eclipse.jetty.websocket.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.concurrent.*;
import com.google.inject.*;
import com.google.inject.servlet.*;

@Singleton
public class WebSocketTesting extends WebSocketServlet {

	private final Set<ChatWebSocket> members = Collections.synchronizedSet( new HashSet<ChatWebSocket>() );

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("regular old get");
		//getServletContext().getNamedDispatcher("default").forward(request,response);
	}

	protected WebSocket doWebSocketConnect(HttpServletRequest request, String protocol){
		return new ChatWebSocket();
	}

	class ChatWebSocket implements WebSocket {

		private Outbound outbound;
		public void onConnect(Outbound outbound) {
			this.outbound = outbound;
			members.add(this);
		}

		public void onMessage(byte frame, byte[] data,int offset, int length) {}

		public void onMessage(byte frame, String data) {
			for (ChatWebSocket member : members) {
				try {
					member.outbound.sendMessage(frame,data);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onDisconnect() {
			members.remove(this);
		}
	}//}}}
}
