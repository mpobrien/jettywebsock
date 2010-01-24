package jettywebsock.ctrlrs;
import com.mob.web.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.*;

@At("^/$")
public class HomeController extends Controller{
	Logger log = Logger.getLogger( HomeController.class );

    @Override
    public WebResponse get(HttpServletRequest req, HttpServletResponse res){
		return responses.render("home.html");

        //return new StringWebResponse("Hello World!");

    }

}
