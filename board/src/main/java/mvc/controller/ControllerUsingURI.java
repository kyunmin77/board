package mvc.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import mvc.command.NullHandler;

public class ControllerUsingURI extends HttpServlet {
	private Map<String, CommandHandler> commandHandlerMap = new HashMap<>();
	// Map형 <키:스트링, 값:CommandHandler>
	
	public void init() throws ServletException{
		String configFile = getInitParameter("configFile"); 
		// > configFile에는 /WEB-INF/commandHandler.properties 라는 경로가 들어있음
		Properties prop = new Properties();		//Properties는 Map과달리 <문자열,문자열>로 이루어진 컬렉션!
		String configFilePath = getServletContext().getRealPath(configFile); // configFile이 지정한 프로젝트의 실제경로를 찾겠다.
		try(FileReader fr = new FileReader(configFilePath)){ //실제경로(configFilePath)의 파일을 읽음
			prop.load(fr);		//fr =  hello=mvc.hello.HelloHandler
		}catch(IOException e) {
			throw new ServletException(e);
		}
		Iterator keyIter = prop.keySet().iterator(); // iterator> 컬랙션을 읽는 '통합된'방법
		while(keyIter.hasNext()) {
			String command = (String)keyIter.next();
			String handlerClassName = prop.getProperty(command);
			
			try {// 아래 두줄: 문자열로 클래스만들기
				Class<?> handlerClass = Class.forName(handlerClassName);
				//클래스 객체를 만든다 == 클래스를 만든다!  >> handlerClassName이 가리키는 클래스를 handlerClass에 넣고
				CommandHandler handlerInstance = (CommandHandler)handlerClass.newInstance(); //인터페이스 다형성
				//handlerClass를 인스턴스화 해줌
				
				commandHandlerMap.put(command, handlerInstance); // (hello, 인스턴스 주소) : 맵에다 다시 저장함.
			}catch(ClassNotFoundException | InstantiationException | IllegalAccessException e){
				throw new ServletException(e);
			}
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		process(req,res);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		process(req,res);
	}
	
	private void process(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		String command = req.getRequestURI();
		if(command.indexOf(req.getContextPath()) == 0) {
			command = command.substring(req.getContextPath().length());
		}
		CommandHandler handler = commandHandlerMap.get(command);
		if (handler == null) {
			handler = new NullHandler();
		}
		String viewPage = null;
		try {
			viewPage = handler.process(req, res); // handler는 HelloHandler 객체 >> req에 데이터를 저장하고 jsp주소를 반환
		}catch(Throwable e) {
			throw new ServletException(e);
		}
		
		if(viewPage != null) {
			RequestDispatcher dispatcher = req.getRequestDispatcher(viewPage);
			dispatcher.forward(req, res);	// req에 데이터를 가지고 jsp로 이동
		}
	}
	
}
