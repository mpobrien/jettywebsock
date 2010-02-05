package jettywebsock.ctrlrs;
import com.mob.web.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;

@At("^/twoplayer/$")
public class TwoPlayerController extends Controller{
	Logger log = Logger.getLogger( HomeController.class );


	//jredis 

    @Override
    public WebResponse get(HttpServletRequest req, HttpServletResponse res){
		return responses.render("home2.html");
    }

    @Override
    public WebResponse post(HttpServletRequest req, HttpServletResponse res){
		String playerName = req.getParameter("playername") + "";
		return responses.render("home2.html");
    }

}
