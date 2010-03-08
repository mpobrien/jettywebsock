package jettywebsock.ctrlrs;
import jettywebsock.tetris.*;
import com.google.inject.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.mob.web.*;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;
import org.jredis.ri.alphazero.JRedisClient;
import static org.jredis.ri.alphazero.support.DefaultCodec.*;

@At("^/twoplayer/?([0-9a-zA-Z\\-]*)/?$")
public class TwoPlayerController extends Controller{
	private static final Logger log = Logger.getLogger( HomeController.class );
	private final GameManager runningGames;
	private String gameId = null;

	public void preprocess(HttpServletRequest request, HttpServletResponse response){
		if( args != null && !args.isEmpty() ){
			this.gameId = args.get(0);
		}
	}

	@Inject
	public TwoPlayerController( GameManager runningGames ){
		this.runningGames = runningGames;
	}

    @Override
    public WebResponse get(HttpServletRequest req, HttpServletResponse res){
		if( this.gameId != null ){
			return responses.render("home2.html", ImmutableMap.of("gameId", this.gameId.toString()) );
		}else{
			return responses.render("home2.html");
		}
    }

    @Override
    public WebResponse post(HttpServletRequest req, HttpServletResponse res){
		UUID newGameId = UUID.randomUUID();
		this.runningGames.createGame( newGameId.toString() );
		//TODO issue a redirect here!
		return responses.render("home2.html", ImmutableMap.of("gameId", newGameId.toString()));
    }

}
