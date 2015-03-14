package remi.remotecontrol.rs;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

import remi.discovery.Discovery;
import remi.discovery.ServiceInfo;

@SuppressWarnings("restriction")
@Path("/remote/")
public class RemoteService {

	private static final int SERVER_PORT = 9997;
	private static String SERVICE_NAME = "remi.RemoteControl";
	private static String URI_TEMPLATE = "http://localhost/";
	
	static Logger logger = LogManager.getLogger(RemoteService.class);
	
	private Robot r = null;
	
	public RemoteService() throws AWTException {
		r = new Robot();
		//screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	}

	
	@Path("version/{text}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String onVersion(@PathParam("version") String version) {
		logger.debug("Version: " + version);
		return "";
	}
	
	@Path("type/{text}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String onText(@PathParam("text") String text) {
		type(text);
		logger.debug("type: " + text);
		return "";
	}
	
	@Path("key/{key}/")
	@Produces(MediaType.TEXT_PLAIN)
	public String onKey(@PathParam("key") Integer key) {
		r.keyPress(key);
		r.keyRelease(key);
		logger.debug("key: " + key);
		return "";
	}

	
	@Path("mouse/{x}/{y}")
	@Produces(MediaType.TEXT_PLAIN)
	public String onMouse(@PathParam("x") Double x,@PathParam("y") Double y ) {
		
		logger.debug("mouse: " + x +"/" + y);
		
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		int cx = (int) b.getX();
		int cy = (int) b.getY();
		r.mouseMove((int)Math.round(cx+x), (int)Math.round(cy+y));
		
		return "";
	}
	
	@Path("scroll/{x}")
	@Produces(MediaType.TEXT_PLAIN)
	public String onScroll(@PathParam("x") Double x) {
		
		logger.debug("scroll: " + x );
		
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		int cx = (int) b.getX();
		r.mouseWheel((int)Math.round(cx+x));
		
		return "";
	}
	
	
	@Path("click/")
	@Produces(MediaType.TEXT_PLAIN)
	public String onClick() {
		//java.awt.event.InputEvent.BUTTON1_MASK   // left mouse button 
		//java.awt.event.InputEvent.BUTTON2_MASK   // middle mouse button
		//java.awt.event.InputEvent.BUTTON3_MASK   // right mouse button
		
		r.mousePress( InputEvent.BUTTON1_MASK );
        r.mouseRelease( InputEvent.BUTTON1_MASK );
        
		logger.debug("click");
		return "";
	}

	@Path("doubleclick/")
	@Produces(MediaType.TEXT_PLAIN)
	public String onDoubleClick() {
		//java.awt.event.InputEvent.BUTTON1_MASK   // left mouse button 
		//java.awt.event.InputEvent.BUTTON2_MASK   // middle mouse button
		//java.awt.event.InputEvent.BUTTON3_MASK   // right mouse button
		
		r.mousePress( InputEvent.BUTTON1_MASK );
        r.mouseRelease( InputEvent.BUTTON1_MASK );
        try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        r.mousePress( InputEvent.BUTTON1_MASK );
        r.mouseRelease( InputEvent.BUTTON1_MASK );
        
		logger.debug("click");
		return "";
	}

	@Path("longclick/")
	@Produces(MediaType.TEXT_PLAIN)
	public String onLongClick() {
		//java.awt.event.InputEvent.BUTTON1_MASK   // left mouse button 
		//java.awt.event.InputEvent.BUTTON2_MASK   // middle mouse button
		//java.awt.event.InputEvent.BUTTON3_MASK   // right mouse button
		
		r.mousePress( InputEvent.BUTTON3_MASK );
        r.mouseRelease( InputEvent.BUTTON3_MASK );
        
		logger.debug("longclick");
		return "";
	}
	
	
	public static void main(String[] args) throws AWTException, InterruptedException {
		logger.info("Staring server...");
		final ServiceInfo service = new ServiceInfo(SERVICE_NAME);
		URI baseUri = UriBuilder.fromUri(URI_TEMPLATE).port(SERVER_PORT).build();
		ResourceConfig config = new ResourceConfig(RemoteService.class);
		final HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
		logger.info("Server started.");
		Runtime.getRuntime().addShutdownHook(new Thread( new Runnable() {
			@Override
			public void run() {
				Discovery.unregisterService(service);
				logger.info("Stopping server...");
				server.stop(Thread.MAX_PRIORITY);
				logger.info("Server stopped.");
			}
		}));
		service.getParameters().put("port", Integer.toString(SERVER_PORT));
		Discovery.registerService(service);
	}
	
	public void type(String characters) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection( characters );
		clipboard.setContents(stringSelection, null);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_CONTROL);
	}
	
}
