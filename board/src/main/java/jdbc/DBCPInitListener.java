package jdbc;

import java.io.IOException;
import java.io.StringReader;
import java.sql.DriverManager;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DBCPInitListener implements ServletContextListener {	//웹 어플리케이션이 시작될때 실행되는 리스너 클래스

	@Override
	public void contextInitialized(ServletContextEvent sce) {	//웹어플리케이션 실행되면 처음 실행되는 메서드
		String poolConfig = sce.getServletContext().getInitParameter("poolConfig"); //web.xml의 파라미터를 받아옴
		Properties prop = new Properties();
		try {
			prop.load(new StringReader(poolConfig));			//파라미터를 프로퍼티에 저장
		}catch (IOException e) {
			throw new RuntimeException("config load fail", e);
		}
		loadJDBCDriver(prop);		//드라이버를 읽어오는 매서드
		initConnectionPool(prop);	//커넥션풀을 연결하는 매서드
	}
	
	private void loadJDBCDriver(Properties prop) {
		String driverClass = prop.getProperty("jdbcdriver");	//프로퍼티에서 jdbcdriver키의 값을 받아옴
		try {
			Class.forName(driverClass);				//(오라클 jar파일에 들어있는)드라이버 클래스를 읽어옴
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("fail to load JDBC Driver", e);
		}
	}
	
	private void initConnectionPool(Properties prop) {
		try {
			String jdbcUrl = prop.getProperty("jdbcUrl");
			String username = prop.getProperty("dbUser");
			String pw = prop.getProperty("dbPass");				//프로퍼티에서 필요한 값을 받아옴
			
			ConnectionFactory connFactory = new DriverManagerConnectionFactory(jdbcUrl,username,pw);
			
			PoolableConnectionFactory poolableConnFactory = new PoolableConnectionFactory(connFactory, null);
			String validationQuery = prop.getProperty("validationQuery");	// validationQuery = select 1
			if(validationQuery != null && !validationQuery.isEmpty()) {		//validationQuery값이 있다면
				poolableConnFactory.setValidationQuery(validationQuery);	
				//커넥션이 유효한지 확인할때 사용하는 쿼리로 지정
			}
			
			//커넥션 풀의 설정정보를 생성
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			poolConfig.setTimeBetweenEvictionRunsMillis(1000L*60L*5L);	//커넥션 검사주기
			poolConfig.setTestWhileIdle(true);							//커넥션 유효성 검사유무
			int minIdle = getIntProperty(prop, "minIdle", 5);			
			poolConfig.setMinIdle(minIdle);								//커넥션 최소개수 	>> 3
			int maxTotal = getIntProperty(prop, "maxTotal", 50);
			poolConfig.setMaxTotal(maxTotal);							//커넥션 최대개수	>> 30
			
			//커넥션 풀 생성
			GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnFactory, poolConfig);
			poolableConnFactory.setPool(connectionPool);
			
			//PoolingDriver 클래스 읽어옴
			Class.forName("org.apache.commons.dbcp2.PoolingDriver");
			PoolingDriver driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			String poolName = prop.getProperty("poolName"); 	// poolName = board		
			driver.registerPool(poolName,connectionPool);		//이름이 "board"인 커넥션풀 등록
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int getIntProperty(Properties prop, String propName, int defaultValue) {
		String value = prop.getProperty(propName);
		if(value == null) return defaultValue;
		return Integer.parseInt(value);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
}
