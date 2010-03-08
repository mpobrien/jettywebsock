package jettywebsock.ctrlrs;
import com.mob.web.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;

@At("^/websock/$")
public class WebSocketTestPage extends Controller{
	Logger log = Logger.getLogger( WebSocketTestPage.class );

    @Override
    public WebResponse get(HttpServletRequest req, HttpServletResponse res){
		return responses.render("old.html");

        //return new StringWebResponse("Hello World!");

    }

}
