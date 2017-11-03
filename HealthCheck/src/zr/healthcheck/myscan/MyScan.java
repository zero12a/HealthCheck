package zr.healthcheck.myscan;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.*;



//ojdbc 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.SQLRecoverableException;

//mybatis
import zr.healthcheck.myscan.SqlSessionManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSession;
 
import java.util.*;

//sha512
import java.security.MessageDigest;
//import java.security.Security;
import java.security.NoSuchAlgorithmException;

//��¥ó��
import java.util.Date;
import java.text.SimpleDateFormat;

//���ó��
import java.net.URLEncoder;
import java.net.URLDecoder;


//LOG4J
import org.apache.log4j.Logger;

//SSL
import java.security.cert.*;
import javax.net.SocketFactory;
import javax.net.ssl.*;

//jdk 1.8
//������ javac  -cp "C:\Program Files\Java\jdk1.8.0_112\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.8.0_112\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.8.0_112\bin\mybatis-3.4.2.jar;." SqlSessionManager.java MyScan.java
//PC ���� java -cp "C:\Program Files\Java\jdk1.8.0_112\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.8.0_112\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.8.0_112\bin\mybatis-3.4.2.jar;." com.ssg.myscan.MyScan

//jdk 1.7
//������ javac  -cp "C:\Program Files\Java\jdk1.7.0_79\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.7.0_79\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.7.0_79\bin\mybatis-3.4.2.jar;." SqlSessionManager.java MyScan.java
//PC ���� java -cp "C:\Program Files\Java\jdk1.7.0_79\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.7.0_79\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.7.0_79\bin\mybatis-3.4.2.jar;." com.ssg.myscan.MyScan



//���� ���� /usr/java71_64/jre/bin/java -cp "/webapp/appsic/HealthCheck/log4j-1.2.17.jar;/webapp/appsic/HealthCheck/ojdbc6.jar;/webapp/appsic/HealthCheck/mybatis-3.4.2.jar;.;" com.ssg.myscan.MyScan
// /usr/java71_64/bin/javac -classpath "/webapp/appsic/HealthCheck/log4j-1.2.17.jar;/webapp/appsic/HealthCheck/ojdbc6.jar;/webapp/appsic/HealthCheck/mybatis-3.4.2.jar;." SqlSessionManager.java MyScan.java
// /usr/java71_64/bin/javac -cp "/webapp/appsic/HealthCheck/.jar;." SqlSessionManager.java MyScan.java
// /usr/java71_64/jre/bin/java -cp ".;" com.ssg.myscan.MyScan

///usr/java71_64/bin/javac -cp "/webapp/appsic/HealthCheck/log4j-1.2.17.jar;/webapp/appsic/HealthCheck/ojdbc6.jar;/webapp/appsic/HealthCheck/mybatis-3.4.2.jar;." SqlSessionManager.java

// .profile edit
//export CLASSPATH=$CLASSPATH:/webapp/appsic/HealthCheck/log4j-1.2.17.jar
//export CLASSPATH=$CLASSPATH:/webapp/appsic/HealthCheck/ojdbc6.jar
//export CLASSPATH=$CLASSPATH:/webapp/appsic/HealthCheck/mybatis-3.4.2.jar

// /usr/java71_64/bin/javac -cp /webapp/appsic/HealthCheck/*.jar;. SqlSessionManager.java MyScan.java
// /usr/java71_64/bin/javac -cp ".;/webapp/appsic/HealthCheck/*.jar" SqlSessionManager.java MyScan.java
//	/usr/java71_64/jre/bin/java -classpath "/webapp/appsic/HealthCheck/*.jar;." com.ssg.myscan.MyScan

///usr/java71_64/jre/bin/java com.ssg.myscan.MyScan

// nohup /usr/java71_64/jre/bin/java com.ssg.myscan.MyScan &

// ���� �ڷ� : http://gangzzang.tistory.com/entry/Java-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%82%B9Networking

// JDBC ��� ���
// 1. JDBC ����̹� �ε� : Class.forName(oracle.jdbc.driver.OracleDriver);
// 2. DB Server ���� : DriverManager.getConnection(jdbc:oracle:thin:@localhost:XE, scott, tiger)
// 3. SQL ������ ��� : Statement �Ǵ� PreparedStatement
// 4. ��� ó�� : executeQuery(SELECT �� ��), executeUpdate(UPDATE, INSERT, DELETE)
// 5. ���� ���� :
 
// ���� ���� : Connection > Statement �Ǵ� PreparedStatement > ResultSet [ ��, ResultSet �� SELECT �϶��� ����Ѵ� ]
// ���� ���� ���� : ResultSet > Statement �Ǵ� PreparedStatement > Connection [ ��, ResultSet �� SELECT �϶��� ����Ѵ� ]
 

public class MyScan{
	static String  COOKIE;
	//private Logger log = Logger.getLogger(this.getClass());
	final static Logger log = Logger.getLogger(MyScan.class);

	//��������
	final static String HC_MANAGER_EMPNO = "xxxxxx";

	public static void main(String [] args){
		log.info("main()-------------------------------start");
		log.info("	args.length : " + args.length);
		log.info("	args[0] domain_seq");
		log.info("	args[1] url_seq");

		String parameter = "";
		if(log.isDebugEnabled()){
			log.debug("This is debug : " + parameter);
		}

		if(log.isInfoEnabled()){
			log.info("This is info : " + parameter);
		}

		log.warn("This is warn : " + parameter);
		log.error("This is error : " + parameter);
		log.fatal("This is fatal : " + parameter);



		//��ĵ ����Ŭ ����
		HashMap<String, Object> mapCycle = new HashMap<String, Object>();
		mapCycle.put("FULL_CNT",0); // ù ������� ����� �� ����Ŭ �� 
		mapCycle.put("TODAY_CNT",0); // ���� ����� ����Ŭ ��
		mapCycle.put("TODAY_DT"," "); // ���ó�¥ YYYYMMDD
		Date tmpDay = new Date();
		SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyyMMdd");
		mapCycle.put("TODAY_DT",String.valueOf( tmpFormat.format(tmpDay) ) );

		//db ����
		SqlSessionFactory sqlSessionFactory = SqlSessionManager.getSqlSession();
		SqlSession sqlSession = sqlSessionFactory.openSession(false); //false : autoCommit�� ���� �ʰڴٴ� �ǹ�




		try{
			 
			log.info("	Test.getDomainList-------------------------------1");

			//���� ���� Ƚ�� ����
			HashMap<String, Object> almFailCntMap = new HashMap<String, Object>();


			//���� ����
			while( 1==1 ){
				
				//����Ŭ��
				mapCycle.put("FULL_CNT", ((int) mapCycle.get("FULL_CNT")) + 1);

				tmpDay = new Date();
				if( !( (String)mapCycle.get("TODAY_DT") ).equals(String.valueOf( tmpFormat.format(tmpDay) )) ){ //��¥�� �Ϸ� ����Ǹ� ī��Ʈ �ʱ�ȭ
					mapCycle.put("TODAY_DT",String.valueOf( tmpFormat.format(tmpDay) ) );
					mapCycle.put("TODAY_CNT",0);
				}
				mapCycle.put("TODAY_CNT", ((int) mapCycle.get("TODAY_CNT")) + 1);

				//SUM ����
				int CHK_DOMAIN_CNT = 0;
				int CHK_URL_CNT = 0;


				//���� �ʱ�ȭ
				int DOMAIN_SEQ = 0;
				String SYSTEM_NM;
				String DOMAIN;
				String ADD_DT;
				String EMPNO;
				String PUSH_MSG = "";
				String FULL_URL = "";
				int effectCnt = 0;
				HashMap<String, Object> paramScan;
				HashMap<String, Object> paramResult;
				HashMap<String, Object> resSsl;
				HashMap<String, Object> param;
				List listDomain;
				HashMap<String, Object> mapResultCnt;
				paramScan = new HashMap<String, Object>();
				paramResult = new HashMap<String, Object>();
				resSsl = new HashMap<String, Object>();
				param = new HashMap<String, Object>();

				// ������ ��������
				log.info("	Test.getDomainChkList-------------------------------2");
				if(args.length > 0){
					log.info("	args[0] : " + args[0]);

					param.put("DOMAIN_SEQ", args[0]);

					listDomain = sqlSession.selectList("Test.getDomainList",param) ;
				}else{
					listDomain = sqlSession.selectList("Test.getDomainChkList") ;
				}
		

				//��ĵ ���� �ð�
				long scanStartTime = System.currentTimeMillis();

				//��ĵ ���� �α� INSERT
				effectCnt = sqlSession.insert("Test.insScan",paramScan);
				log.info("	SEQ_HC_SCAN.currval : " + paramScan.get("LAST_SCAN_SEQ"));

				
				
				// ������ ����Ʈ ����ϱ�
				log.info("	listDomain.size()..." + listDomain.size());


				for(int i=0; i < listDomain.size(); i++){

					//�����κ��� ��Ű �ʱ�ȭ ( ������ �������� ��Ű ���� )
					COOKIE = "";
	
					CHK_DOMAIN_CNT++;

					HashMap mapDomain = (HashMap)listDomain.get(i) ;
					 
					DOMAIN_SEQ	= Integer.parseInt(String.valueOf(mapDomain.get(	"DOMAIN_SEQ"	)));
					SYSTEM_NM	= getHan((String) mapDomain.get(	"SYSTEM_NM"		) + "");
					DOMAIN		= getHan((String) mapDomain.get(	"DOMAIN"		) + ""); 
					ADD_DT		= getHan((String) mapDomain.get(	"ADD_DT"		) + "");
					EMPNO		= getHan((String) mapDomain.get(	"EMPNO"		) + "");
					log.info("	" + DOMAIN_SEQ + ":" + SYSTEM_NM + ":" + DOMAIN + ":" + ADD_DT + ":"+ EMPNO );


					//������ �α� INSERT �ϱ�
					paramResult = new HashMap<String, Object>();
					paramResult.put("DOMAIN_SEQ", DOMAIN_SEQ);

					effectCnt = sqlSession.insert("Test.insResult",paramResult) ;
					log.info("	LAST_RESULT_SEQ : " + paramResult.get("LAST_RESULT_SEQ"));
					log.info("	(Test.insResult) effectCnt : " + effectCnt);



					//������ SSL_PORT�� ���ǵ� ��� SSL_PORT�� ������ ������ �˻�
					resSsl = (HashMap) getSsl(mapDomain);
					log.info("	SSL RTN_CD : " + resSsl.get("RTN_CD"));
					log.info("		RTN_MSG : " + resSsl.get("RTN_MSG"));
					log.info("		ERROR_CD : " + resSsl.get("ERROR_CD"));
					log.info("		ERROR_MSG : " + resSsl.get("ERROR_MSG"));
					
					if(!"000".equals( resSsl.get("RTN_CD") ) && String.valueOf(resSsl.get("SSL_EXPIRE_DT")).length() == 8 ) {
						paramResult.put("SSL_EXPIRE_DT",resSsl.get("SSL_EXPIRE_DT"));
						paramResult.put("SSL_DUE_DAY",resSsl.get("SSL_DUE_DAY"));
						paramResult.put("SSL_ERR_CD",resSsl.get("ERROR_CD"));
						paramResult.put("SSL_ERR_MSG",resSsl.get("ERROR_MSG"));

						log.info("		������ SSL ������ ���� (Test.updDomainSsl)");

						//�˶� �Ⱓ�� ���� �� ��� ���� ù �˻��ϰ�쿡�� ����� �˶� ó��
						log.info("		isNumeric(SSL_ALM_PERIOD) : " + isNumeric( String.valueOf(mapDomain.get("SSL_ALM_PERIOD")) ));
						log.info("		(int)resSsl.get(SSL_DUE_DAY) : " + resSsl.get("SSL_DUE_DAY") );
						log.info("		Integer.parseInt(String.valueOf(mapDomain.get(SSL_ALM_PERIOD))) : " + String.valueOf(mapDomain.get("SSL_ALM_PERIOD")) );


						if( isNumeric( String.valueOf(mapDomain.get("SSL_ALM_PERIOD")) )  &&	isNumeric( String.valueOf(resSsl.get("SSL_DUE_DAY")) ) &&
							(int)resSsl.get("SSL_DUE_DAY") <=  Integer.parseInt(String.valueOf(mapDomain.get("SSL_ALM_PERIOD")))
							){
							log.info("		### RESULT ���ᰡ �����ؼ� ����� �˶� ");
							HashMap<String,String> msg =  new HashMap<String, String>();  
							msg.put("header", "HTTP SCAN");
							msg.put("receiver_id", EMPNO);
							msg.put("message", "�� " + SYSTEM_NM + "(" + DOMAIN + ") ��");
							msg.put("message_body","SSL ������ ������� " + paramResult.get("SSL_DUE_DAY") + "��(Expired Date:" + paramResult.get("SSL_EXPIRE_DT") + ") ���ҽ��ϴ�." );

							//���� ù��° DOMAIN ��ĵ �ÿ��� ��� PUSH�� �����ϱ�
							mapResultCnt = sqlSession.selectOne("Test.getResult_TodayCnt",mapDomain);
							if(mapResultCnt != null && isNumeric( String.valueOf(mapResultCnt.get("CNT")) )  && Integer.parseInt(String.valueOf(mapResultCnt.get("CNT"))) == 1 ){
								log.info("		���� Send Push (First). ");
								HashMap<String,Object> resPush = goAlarmPush(msg); 
							}else{
								log.info("		���� Send No Push.");
							}
						}
						//��� �̷������� ������Ʈ �ϱ�
						log.info("		### Test.updResultSsl ");
						effectCnt = sqlSession.update("Test.updResultSsl",paramResult);

						//domain ���������� ssl������ ������Ʈ �ϱ�
						log.info("		### Test.updDomainSsl ");
						effectCnt = sqlSession.update("Test.updDomainSsl",paramResult); //SSL ������ DB UPDATE
					}

					//�ش� �������� �˻� �� URL LIST �ҷ�����
					List listUrl = null;
					if(args.length == 2){
						log.info("	args[1] : " + args[1]);

						paramResult.put("URL_SEQ", args[1]);

						listUrl = sqlSession.selectList("Test.getUrlUse",paramResult);
					}else{
						listUrl = sqlSession.selectList("Test.getUrlUseList",paramResult);
					}
			
					
					for(int k=0; k < listUrl.size(); k++){

						CHK_URL_CNT++;

						HashMap<String,Object> mapUrl = (HashMap)listUrl.get(k);

						//HTTP ����MAP ����
						HashMap<String,Object> resMap;
						
						//�����̷����� ��� http ��� location �޾ƿͼ� �ٽ� �ѹ��� http ������
						if( "302".equals(mapUrl.get("EXPECT_RES_CD").toString()) ){
							log.info("	EXPECT_RES_CD 302 ������ ���� ");

							resMap= goHttp(mapDomain, mapUrl);
							if( "302".equals(resMap.get("RES_CD").toString()) ){
								//��Ű �ֱ�
								COOKIE = String.valueOf(resMap.get("COOKIE"));

								FULL_URL = mapUrl.get("FULL_URL").toString();//���� full���
								mapUrl.put("FULL_URL", resMap.get("LOCATION"));//�� location���� ���� �������ϱ�
								mapUrl.put("OLD_FULL_URL", FULL_URL);//���� ��� ���� �����ؼ� ������ ����

								//�������ؼ� �ٽ� �� ��������
								resMap= goHttp(mapDomain, mapUrl);

							}else{
								//���� ó�� �ؾ� ��
							}
						}else{
							resMap= goHttp(mapDomain, mapUrl);  
						}
						log.info("	URL_SEQ : " + mapUrl.get("URL_SEQ"));
						log.info("	[resMap] RTN_CD : " + resMap.get("RTN_CD"));
						log.info("	[resMap] RTN_MSG : " + resMap.get("RTN_MSG"));
						log.info("	[resMap] RES_CD : " + resMap.get("RES_CD"));

						//log.info("	A111");
						log.info("	[resMap] RES_TIME : " + resMap.get("RES_TIME"));
						//log.info("	[resMap] RES_BODY : " + resMap.get("RES_BODY"));
						//log.info("	A222");

						//��� �� �����ϱ�
						param = new HashMap<String, Object>();
						//log.info("	A333");

						param.put("DOMAIN_SEQ", DOMAIN_SEQ);
						param.put("URL_SEQ", mapUrl.get("URL_SEQ"));
						//log.info("	A444");

						param.put("RES_CD", resMap.get("RES_CD"));
						param.put("RES_TIME", resMap.get("RES_TIME"));
						param.put("RES_BODY", ((StringBuffer)resMap.get("RES_BODY")).toString());
						//log.info("	A555");

						param.put("RES_SIZE", ((StringBuffer)resMap.get("RES_BODY")).length());
						param.put("RES_HASH", encryptSHA512(((StringBuffer)resMap.get("RES_BODY")).toString()));
						param.put("SUCCESS_YN", "Y");
						param.put("INVALID_CD", "1");
						param.put("ERROR_CD", resMap.get("ERROR_CD"));
						param.put("ERROR_MSG", resMap.get("ERROR_MSG"));
						param.put("ERROR_TRACE", resMap.get("ERROR_TRACE"));

						//(INVALID_CD) ������ ��ȿ�� �˻�
						//500 �⺻�˻� : ������ 500����
						//302,404 �⺻�˻� : ������ 500����
						//100 BYTE ���� MIN
						//110 BYTE ���� MAX
						//120 TIMEOUT ���� �ʰ�
						//130 CONTENT_IN_STR �ش� ���ڿ��� ���ԵǾ�� ��
						//140 CONTENT_HASH �ؽ����� ��ġ�ϴ��� �˻�
						//999 �˼����� ����
						log.info("	VALID.............................................start");
						log.info("		RES_CD = " + resMap.get("RES_CD"));

						PUSH_MSG = "";
						if("200".equals(resMap.get("RES_CD").toString())){
							log.info("		200");

							//����� �˻�
							if( mapUrl.get("BYTE_MIN") != null && Integer.parseInt(mapUrl.get("BYTE_MIN").toString()) > ((StringBuffer)resMap.get("RES_BODY")).length()){
								log.info("			Not Valid ( Cause : BYTE_MIN )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "100");
								PUSH_MSG = "�ּҹ���Ʈ(" + mapUrl.get("BYTE_MIN") + ")���� ���� ����� ����Ʈ(" + ((StringBuffer)resMap.get("RES_BODY")).length() + ")�� �۽��ϴ�.";
							}else if( mapUrl.get("BYTE_MAX") != null && Integer.parseInt(mapUrl.get("BYTE_MAX").toString()) < ((StringBuffer)resMap.get("RES_BODY")).length()){
								log.info("			Not Valid ( Cause : BYTE_MAX )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "110");
								PUSH_MSG = "�ִ����Ʈ(" + mapUrl.get("BYTE_MAX") + ")���� ���� ����� ����Ʈ(" + ((StringBuffer)resMap.get("RES_BODY")).length() + ")�� Ů�ϴ�.";
							}else if( mapUrl.get("RES_TIME_MAX") != null && Integer.parseInt(mapUrl.get("RES_TIME_MAX").toString()) < Integer.parseInt(resMap.get("RES_TIME").toString()) ){
								log.info("			Not Valid ( Cause : TIMEOUT )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "120");
								PUSH_MSG = "�ִ�����ð�[ms](" + mapUrl.get("RES_TIME_MAX") + ")���� ���� ����ð�(" + resMap.get("RES_TIME") + ")�� Ů�ϴ�.";

							//���Եǰų� ��ġ���� ������ ����
							}else if( mapUrl.get("CONTENT_IN_STR") != null 
								&& 
									!(
										((StringBuffer)resMap.get("RES_BODY")).toString().indexOf(mapUrl.get("CONTENT_IN_STR").toString()) > 0 
										||
										((StringBuffer)resMap.get("RES_BODY")).toString().equals(mapUrl.get("CONTENT_IN_STR").toString())  
									)
								
								){
								log.info("			Not Valid ( Cause : CONTENT_IN_STR )");
								log.info("			 - length of RES.BODY = " + ((StringBuffer)resMap.get("RES_BODY")).toString().length() );
								log.info("			 - length of CONTENT_IN_STR = " + mapUrl.get("CONTENT_IN_STR").toString().length() );
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "130");
								PUSH_MSG = "������ BODY�� ���ԵǾ�� �� ��ȿ���ڿ�(" + mapUrl.get("CONTENT_IN_STR") + ")�� �����ϴ�.";
							}else if( mapUrl.get("CONTENT_HASH") != null && resMap.get("CONTENT_HASH") != encryptSHA512(((StringBuffer)resMap.get("RES_BODY")).toString()) ){
								log.info("			Not Valid ( Cause : CONTENT_HASH )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "140");
								PUSH_MSG = "���� ���ǵ� BODY�� �ؽ���(" + mapUrl.get("CONTENT_HASH") + ")�� ���� ���� BODY�� �ؽ���(" + encryptSHA512(((StringBuffer)resMap.get("RES_BODY")).toString()) + ")�� ��ġ���� �ʽ��ϴ�.";
							}else{
								log.info("			ELSE");
								param.put("SUCCESS_YN", "Y");
							}

							//PUSH �޽����� ��¥ �߰�
							Date tNow = new Date();
							SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
							log.info(format.format(tNow)); // 20090529
							PUSH_MSG = PUSH_MSG + "\n" + format.format(tNow).toString();


						}else if("500".equals(resMap.get("RES_CD").toString())){
							log.info("		500");

							PUSH_MSG = "500 ������ ���� �߻�";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "500");
						}else if("302".equals(resMap.get("RES_CD").toString())){
							log.info("		302");

							PUSH_MSG = "302 �����̷�Ʈ�� ������ �̵� �ʿ� (Location:" + resMap.get("LOCATION") + ")";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "302");
						}else if("404".equals(resMap.get("RES_CD").toString())){
							log.info("		404");

							PUSH_MSG = "404 ������ ����";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "404");
						}else{
							log.info("		ELSE");

							PUSH_MSG = "999 �� �� ���� ���� �߻�";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "999");
						}

						param.put("PUSH_MESSAGE_BODY", putHan( PUSH_MSG ));


						//��� �׽�Ʈ �߼�
						log.info("	APP PUSH.............................................start");

						param.put("PUSH_SUCCESS_YN", "-");
						param.put("PUSH_HEADER","");
						param.put("PUSH_RECEIVER_ID","");
						param.put("PUSH_MESSAGE","");
						param.put("PUSH_MESSAGE_BODY","");
						param.put("PUSH_ERROR_CD", "");
						param.put("PUSH_ERROR_MSG",  "");
						param.put("PUSH_ERROR_TRACE",  "");

						if( "N".equals( param.get("SUCCESS_YN").toString()) ){
							HashMap<String,String> msg =  new HashMap<String, String>();  
							msg.put("header", "HTTP SCAN");
							msg.put("receiver_id", EMPNO);
							msg.put("message", "�� " + SYSTEM_NM + "(" + DOMAIN + ") ��");

							param.put("PUSH_HEADER",putHan(msg.get("header")));
							param.put("PUSH_RECEIVER_ID",msg.get("receiver_id"));
							param.put("PUSH_MESSAGE",putHan(msg.get("message")));
							//param.put("PUSH_MESSAGE_BODY",putHan(msg.get("message_body")));


							//�ش� URL ���� ���� Ƚ�� ����
							log.info("	B111");

							if(almFailCntMap.get( String.valueOf( mapUrl.get("URL_SEQ") ) ) == null){
								log.info("	B222");
								almFailCntMap.put(	String.valueOf( mapUrl.get("URL_SEQ") ), 1);
							}else{
								log.info("	B333");
								almFailCntMap.put(
										String.valueOf( mapUrl.get("URL_SEQ") ),
										( (int) almFailCntMap.get( String.valueOf(mapUrl.get("URL_SEQ")) ) ) + 1 
									);
							}							
							log.info("	ALM_FAIL_CNT [DB] : " + mapUrl.get("ALM_FAIL_CNT"));
							log.info("	almFailCntMap : " + almFailCntMap.get( String.valueOf( mapUrl.get("URL_SEQ") ) ));


							//���� �޽���
							if( "Y".equals(param.get("SUCCESS_YN").toString()) ){
								msg.put("message_body", "���=����\n���=[" + mapUrl.get("URL_SEQ").toString() + "/" + mapUrl.get("HOST").toString() + "/" + getHan(mapUrl.get("URL_NM").toString()) + "]" + mapUrl.get("FULL_URL").toString() + "\n����=" + PUSH_MSG);
							}else{
							msg.put("message_body", "���=����(����Ƚ��:" + almFailCntMap.get( String.valueOf( mapUrl.get("URL_SEQ") ) ) + ")\n���=[" + mapUrl.get("URL_SEQ").toString() + "/" + mapUrl.get("HOST").toString() + "/" + getHan(mapUrl.get("URL_NM").toString()) + "]" + mapUrl.get("FULL_URL").toString() + "\n����=" + PUSH_MSG);
							}



							//���� ���� Ƚ���� DB���ǵ� Ƚ���� ������ PUSH�߼�
							if( mapUrl.get("URL_SEQ") != null && String.valueOf(mapUrl.get("ALM_FAIL_CNT")).equals( String.valueOf(almFailCntMap.get( String.valueOf( mapUrl.get("URL_SEQ") ) )) )){
								HashMap<String,Object> resPush = goAlarmPush(msg);  
								log.info("	[resPush] RTN_CD : " + resPush.get("RTN_CD"));
								log.info("	[resPush] RTN_MSG : " + resPush.get("RTN_MSG"));
								log.info("	[resPush] RES_CD : " + resPush.get("RES_CD"));
								log.info("	[resPush] RES_TIME : " + resPush.get("RES_TIME"));
								log.info("	[resPush] RES_BODY : " + resPush.get("RES_BODY"));
								log.info("	[resPush] ERROR_CD : " + resPush.get("ERROR_CD"));
								log.info("	[resPush] ERROR_MSG : " + resPush.get("ERROR_MSG"));
								log.info("	[resPush] ERROR_TRACE : " + resPush.get("ERROR_TRACE"));
								param.put("PUSH_ERROR_CD", resPush.get("ERROR_CD").toString());
								param.put("PUSH_ERROR_MSG",  resPush.get("ERROR_MSG").toString());
								param.put("PUSH_ERROR_TRACE",  resPush.get("ERROR_TRACE").toString());

								if("200".equals(resPush.get("RTN_CD"))){
									param.put("PUSH_SUCCESS_YN", "Y");
								}else{
									param.put("PUSH_SUCCESS_YN", "N");
								}

								//PUSH �߼��� �ˍ� ī��Ʈ �ʱ�ȭó��
								almFailCntMap.put(	String.valueOf( mapUrl.get("URL_SEQ") ), null);

							}
						}else{
							//�ش� URL �����ϸ� ���� ���� Ƚ�� �ʱ�ȭ
							log.info("	B555");
							almFailCntMap.put(	String.valueOf( mapUrl.get("URL_SEQ") ), null);
						}

						//��� DB ����
						effectCnt = sqlSession.insert("Test.insResultD",param) ;
						log.info("	(Test.insResultD) effectCnt : " + effectCnt);
					

					}// for(int k=0; k < listUrl.size(); k++){		 

					//�ش� ������ ���� ��� ���� update
					effectCnt = sqlSession.update("Test.updResult",paramResult);


				}//for(int i=0; i < listDomain.size(); i++){

				
				//��ĵ ���� �α� INSERT
			    long elapsedTime = System.currentTimeMillis() - scanStartTime;
				paramScan.put("FULL_SCAN_TIME",elapsedTime);
				effectCnt = sqlSession.update("Test.updScan",paramScan);

				sqlSession.commit();

				//��� ��� �մϴ�.
				log.info("	\n\n\n");



				log.info("###############################################################");
				log.info("	CHK_DOMAIN_CNT = "  + CHK_DOMAIN_CNT);
				log.info("	CHK_URL_CNT = "  + CHK_URL_CNT);
				log.info("	\n");
				log.info("	CYCLE.FULL_CNT = "  + mapCycle.get("FULL_CNT") );
				log.info("	CYCLE.TODAY_CNT = "  + mapCycle.get("TODAY_CNT") );
				log.info("	CYCLE.TODAY_DT = "  + mapCycle.get("TODAY_DT") );
				log.info("###############################################################");
				log.info("	\n");
				log.info("	Sleep 30 seconds. " );
				log.info("	\n");

				Thread.sleep(30000);

			}//���� while loop end

		}catch (Exception e) {
			sqlSession.rollback();

			log.info("	[Exception]-------------------------------");

			log.info(e.getMessage());
			log.info(e.getStackTrace());

			managerAlarm("Exception line : "+ String.valueOf(  (new Throwable()).getStackTrace()[0].getLineNumber()  ) );
		}finally{
			sqlSession.close() ;
		}
		 
		
		log.info("main()-------------------------------end");
	}
	

	public static void managerAlarm(String tmp){
		HashMap<String,String> msg =  new HashMap<String, String>();  
		msg.put("header", "HTTP SCAN");
		msg.put("receiver_id", HC_MANAGER_EMPNO);
		msg.put("message", "HEALTH SCAN ���� ���� �߻�");
		msg.put("message_body", tmp);

		goAlarmPush(msg); 
	}


	public static boolean isNumeric(String s) {  
		return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  

	public static HashMap<String,Object> getSsl(HashMap tDomain){
		log.info("getSsl()-------------------------------start");
		log.info("	DOMAIN:" + tDomain.get("DOMAIN"));
		log.info("	SSL_PORT:" + tDomain.get("SSL_PORT"));




		//���� ��ü
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("RTN_CD","000");
		resMap.put("RTN_DATA",0); // ���� ��¥ ����
		resMap.put("RTN_MSG","");
		resMap.put("RES_CD","");
		resMap.put("ERROR_CD","");
		resMap.put("ERROR_MSG","");
		resMap.put("ERROR_TRACE","");

		//SSL��Ʈ ���� ���� �˻�
		log.info("	isNumeric SSL_PORT check" );

		if( !isNumeric(String.valueOf(tDomain.get("SSL_PORT"))) ){
			log.info("	SSL ��Ʈ�� ���ǵ��� �ʾҽ��ϴ�.");

			resMap.put("RTN_CD","510");
			resMap.put("RTN_MSG","������");
			resMap.put("ERROR_CD","NO SSL PORT");
			resMap.put("ERROR_MSG","SSL��Ʈ ������");
			return resMap;
		}

		//host port����
		log.info("	host, port var setting" );
        String host = String.valueOf( tDomain.get("DOMAIN") );
        int port = Integer.parseInt( String.valueOf(tDomain.get("SSL_PORT")) );
		log.info("	host, port var setting ok" );

		SocketFactory factory = SSLSocketFactory.getDefault();
        log.info("010");
		SSLSocket socket = null;

		try{
			socket = (SSLSocket) factory.createSocket(host, port);
	        log.info("011");

			socket.startHandshake();
	        log.info("012");

		}catch(SSLException e){
			if(socket != null){
				try{
					socket.close();
					socket = null;
				}catch(IOException ex){}
				//���� ���� �ʱ�ȭ ��Ű��
			}
			resMap.put("RTN_CD","530");
			resMap.put("RTN_MSG","������");
			resMap.put("ERROR_CD","NOT SSL");
			resMap.put("ERROR_MSG","Plaintext connection");

			log.info("[SSLException] Plaintext connection"); // SSL �ƴϰ� �׳� HTTP �����
		}catch(IOException e){
			if(socket != null){
				try{
					socket.close();
					socket = null;
				}catch(IOException ex){}
				//���� ���� �ʱ�ȭ ��Ű��
			}
			resMap.put("RTN_CD","540");
			resMap.put("RTN_MSG","������");
			resMap.put("ERROR_CD","NOT SSL");
			resMap.put("ERROR_MSG","[IOException] Plaintext connection");

			log.info(e.getMessage());
			log.info(e.getStackTrace());


			log.info("[IOException] Plaintext connection"); // SSL �ƴϰ� �׳� HTTP �����
		}


		if(socket != null){
			log.info("111");

			Certificate[] certs = null;
			try{
				certs = socket.getSession().getPeerCertificates();
			}catch(SSLPeerUnverifiedException e){
				log.info(e.getMessage());
				log.info(e.getStackTrace());
				log.info("[SSLPeerUnverifiedException] "); // SSL �ƴϰ� �׳� HTTP �����

			}

			log.info("222");

			Date curDate = new Date();
			long curTime = curDate.getTime();


			log.info("Certs retrieved: " + certs.length);
			for (Certificate cert : certs) {
				//log.info("Certificate is: " + cert);

				log.info("333");

				if(cert instanceof X509Certificate) {

					log.info("444");

					//��¥ �˻�
					Date expDate = ( (X509Certificate) cert).getNotAfter();
					long expTime = expDate.getTime();

					int dayOffSet = (int) ((expTime - curTime) / (1024 * 60 * 60 * 24));
					log.info("dayOffSet:" + dayOffSet); //SSL ���ű��� ���� ��¥
					resMap.put("SSL_DUE_DAY",dayOffSet); 

					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					log.info("expDate" + format.format(expDate)); // 20090529

					resMap.put("SSL_EXPIRE_DT",format.format(expDate)); //������ ����

					try {
						( (X509Certificate) cert).checkValidity();
						log.info("Certificate is active for current date");

						resMap.put("RTN_CD","200");
						resMap.put("RTN_MSG","����");

					} catch(CertificateNotYetValidException cee) {
						resMap.put("RTN_CD","550");
						resMap.put("RTN_MSG","������");
						resMap.put("ERROR_CD","SSL EXPIRED");
						resMap.put("ERROR_MSG","Certificate is expired");

						log.info("Certificate Not Yet Valid");
					} catch(CertificateExpiredException cee) {
						resMap.put("RTN_CD","560");
						resMap.put("RTN_MSG","������");
						resMap.put("ERROR_CD","SSL EXPIRED");
						resMap.put("ERROR_MSG","Certificate is expired");

						log.info("Certificate is expired");
					}

					break; //exit for
				}
			}
		}// if socket is not null


		log.info("getSsl()-------------------------------end");

		return resMap;
	}


	public static String putHan(String asis){
		//log.info("putHan()-------------------------------");

		String tobe = "";

		if(asis == null){
			return tobe;
		}else{
			try
			{
				tobe = new String(asis.getBytes("KSC5601"), "8859_1");
			}catch (Exception e) {
				log.info("	[Exception]-------------------------------");

				log.info(e.getMessage());
				log.info(e.getStackTrace());
			}

			return tobe;
		}
	}


	public static String getHan(String asis){
		//log.info("getHan()-------------------------------");

		String tobe = "";

		if(asis == null){
			return tobe;
		}else{
			try
			{
				tobe = new String(asis.getBytes("8859_1"), "KSC5601");
			}catch (Exception e) {
				log.info("	[Exception]-------------------------------");

				log.info(e.getMessage());
				log.info(e.getStackTrace());
			}

			return tobe;
		}
	}
 


	public static HashMap<String,Object> goAlarmPush(HashMap tMsg){
		log.info("goBlossomPush()-------------------------------");
		log.info("	[tMsg]receiver_id :" + tMsg.get("receiver_id"));
		log.info("	[tMsg]header :" + tMsg.get("header"));
		log.info("	[tMsg]message :" + tMsg.get("message"));
		log.info("	[tMsg]message_body :" + tMsg.get("message_body"));
		

		HttpURLConnection urlConn = null;
		BufferedReader br;
		URL url = null;

		//���� ��ü
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("RTN_CD","000");
		resMap.put("RTN_MSG","");
		resMap.put("RES_TIME","");
		resMap.put("ERROR_CD","");
		resMap.put("ERROR_MSG","");
		resMap.put("ERROR_TRACE","");


		HashMap<String, Object> tUrl;
		tUrl = new HashMap<String, Object>();
		tUrl.put("FULL_URL", "http://10.253.12.135/Website/Custom/Mobile/SPNSRequestPushSrv_Multi.asmx/RequestPush");
		tUrl.put("METHOD", "POST");
		tUrl.put("QUERY_STRING", "system_code=BC8B8281-0C08-4C24-8842-AFBC52513A78&system_key=10000&receiver_id=" + tMsg.get("receiver_id") + "&header=" + tMsg.get("header") + "&message=" + tMsg.get("message") + "&message_body=" + tMsg.get("message_body") + "&sender_id=p905z1&applink_yn=N");
		tUrl.put("TIMEOUT", 3000);

		StringBuffer resBody = new StringBuffer();


		//���� �ð�
		long startTime = System.currentTimeMillis();

		try{
			log.info("	Starting MyTest");
			//blossom api v4
			
			url = new URL((String)tUrl.get("FULL_URL"));
			
			//log.info("	111");
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setRequestMethod((String)tUrl.get("METHOD"));//POST
			//log.info("	222");
			urlConn.setConnectTimeout( Integer.parseInt(String.valueOf(tUrl.get("TIMEOUT"))) );//
			//log.info("	333");
			urlConn.setReadTimeout( Integer.parseInt(String.valueOf(tUrl.get("TIMEOUT")))  );//
			//log.info("	444");

			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);
			urlConn.setRequestProperty ("Content-Type","application/x-www-form-urlencoded");

			//�� ����
			urlConn.connect();

			//POST�϶� �Է� �Ķ���� ����
			if("POST".equals(tUrl.get("METHOD")) && tUrl.get("QUERY_STRING") != null){
				OutputStream os = urlConn.getOutputStream();
				os.write(((String)tUrl.get("QUERY_STRING")).getBytes("UTF-8"));
				os.flush();
				os.close();
			}

			int responseCode = urlConn.getResponseCode();
			resMap.put("RES_CD",responseCode);

			if ( responseCode != 200 ) {
				log.info("	[responseCode].......................200 ���� (" + responseCode + ")");
			}else{
				log.info("	[responseCode].......................200 ����");
			}

			// ������� �޽����� ���̸�ŭ ���۸� �����Ͽ� �о���̰�, "EUC-KR"�� ���ڵ��ؼ� �о���δ�.
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));
			//br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String input;		
			log.info("	[RESPONSE CONTENT]---------------------------------------------------");
			while ((input = br.readLine()) != null){

				resBody.append(input);

				//log.info( input.getBytes("UTF-8") );
				//log.info( input );
			}
			br.close();
			resMap.put("RES_BODY",resBody);

			log.info("	[resPush] RES_BODY indexOf : " + resBody.toString().indexOf(">OK<") );
			if( resBody.toString().indexOf(">OK<") > 0){
				resMap.put("RTN_CD","200");		
				resMap.put("RTN_MSG","�߼� ����");
			}else{
				resMap.put("RTN_CD","500");		
				resMap.put("ERROR_CD","ResponseBodyNotOk");
				resMap.put("ERROR_MSG","RES_BODY indexOf not '>OK<'.");
			}

		    long elapsedTime = System.currentTimeMillis() - startTime;
			resMap.put("RES_TIME",elapsedTime);


			log.info("Done");

		}catch(MalformedURLException e){
			resMap.put("RTN_CD","500");		
			resMap.put("ERROR_CD","MalformedURLException");
			resMap.put("ERROR_MSG",e.getMessage());
			resMap.put("ERROR_TRACE",e.getStackTrace());

			log.info("	[MalformedURLException]-------------------------------");

			log.info(e.getMessage());
			log.info(e.getStackTrace());
		}catch(IOException e){
			resMap.put("RTN_CD","500");		
			resMap.put("ERROR_CD","IOException");
			resMap.put("ERROR_MSG",e.getMessage());
			resMap.put("ERROR_TRACE",e.getStackTrace());

			log.info("	[IOException]-------------------------------");
			log.info(e.getMessage());
			log.info(e.getStackTrace());
		}catch(Exception e){
			resMap.put("RTN_CD","500");		
			resMap.put("ERROR_CD","Exception");
			resMap.put("ERROR_MSG",e.getMessage());
			resMap.put("ERROR_TRACE",e.getStackTrace());

			log.info("	[Exception]-------------------------------");
			log.info(e.getMessage());
			log.info(e.getStackTrace());
		}

		return resMap;

	}



	public static HashMap<String,Object> goHttp(HashMap tDomain,HashMap tUrl){
		log.info("goHttp()-------------------------------");
		log.info("	tDomain.DOMAIN_SEQ : "+ tDomain.get("DOMAIN_SEQ"));
		log.info("	tDomain.CHARSET : "+ tDomain.get("CHARSET"));
		log.info("	tDomain.TIMEOUT : "+ tDomain.get("TIMEOUT"));
		log.info("	tUrl.URL_SEQ : "+ tUrl.get("URL_SEQ"));
		log.info("	tUrl.METHOD : "+ tUrl.get("METHOD"));
		log.info("	tUrl.FULL_URL : "+ tUrl.get("FULL_URL"));
		log.info("	tUrl.QUERY_STRING : "+ tUrl.get("QUERY_STRING"));


		//url���� ��ü
		HttpURLConnection urlConn = null;
		BufferedReader br = null;
		OutputStream os = null;
		URL url = null;
		Map<String, String> qsMap = null;
		String qsStr = "";


		//���� ��ü
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		StringBuffer resBody = new StringBuffer();
		resMap.put("RTN_CD","000");
		resMap.put("RTN_MSG","");
		resMap.put("RES_CD","");
		resMap.put("RES_TIME","");
		resMap.put("RES_BODY",resBody);
		resMap.put("ERROR_CD","");
		resMap.put("ERROR_MSG","");
		resMap.put("ERROR_TRACE","");



		//���� �ð�
		long startTime = System.currentTimeMillis();



		try{
			log.info("Starting MyTest");

			//db���� URL�޾ƿͼ� �����ϱ�
			url = new URL((String)tUrl.get("FULL_URL"));
			log.info("	url INIT_FULL_URL = "+ url.toString());
			log.info("	url.getPath = "+ url.getPath());
			log.info("	url.getQuery = "+ url.getQuery());
			log.info("	url.protocol = " + url.getProtocol());
			log.info("	url.authority = " + url.getAuthority());
			log.info("	url.host = " + url.getHost());
			log.info("	url.port = " + url.getPort());
			log.info("	url.path = " + url.getPath());
			log.info("	url.query = " + url.getQuery());
			log.info("	url.filename = " + url.getFile());
			log.info("	url.ref = " + url.getRef());

			//�Ķ���Ͱ� ������ �Ķ���� ���ڵ� ó��
			if( (  (String)tUrl.get("FULL_URL") ).indexOf("?") > 0 && "Y".equals((String)tUrl.get("FORCE_QS_ENC_YN"))){
				//������Ʈ�� ������ �ޱ�
				qsMap = splitQuery(url);

				//���� �ٽ� ������Ʈ�� ���ڿ��� �����ϱ�
				qsStr = mapToQuerystring(qsMap);
				log.info("	qsStr : "+ qsStr);

				//���� ��Ʈ�� �и�
				String tmpUrl[] = ((String)tUrl.get("FULL_URL")).split("\\?");

				//���� URL �����ϱ�
				if(qsStr != null && !"".equals(qsStr)){
					url = new URL(tmpUrl[0] + "?" + qsStr);
				}
				log.info("	url LAST FULL_URL : "+ url.toString());
				log.info("	url.LAST getQuery = "+ url.getQuery());
			}



			//log.info("	111");
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setRequestMethod((String)tUrl.get("METHOD"));//POST
			//log.info("	222");
			urlConn.setConnectTimeout( Integer.parseInt(String.valueOf(tDomain.get("TIMEOUT"))) );//
			//log.info("	333");
			urlConn.setReadTimeout( Integer.parseInt(String.valueOf(tDomain.get("TIMEOUT")))  );//
			//log.info("	444");

			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);
			urlConn.setInstanceFollowRedirects(false);

			urlConn.setRequestProperty ("Cookie",COOKIE);

			/*
			urlConn.setRequestProperty ("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0");
			urlConn.setRequestProperty ("Connection","keep-alive");
			urlConn.setRequestProperty ("Accept-Encoding","gzip, deflate");
			urlConn.setRequestProperty ("Accept-Language","ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3");
			urlConn.setRequestProperty ("Cookie","a=1");
			*/



			//����� ���� ���� ���� ����
			if( tUrl.get("RES_CONTENT_TYPE") != null && tUrl.get("RES_CONTENT_TYPE").toString() != "" ){
				log.info("	������ ������ Ÿ�� (Accept Content-Type) : " + tUrl.get("RES_CONTENT_TYPE").toString());

				urlConn.setRequestProperty ("Accept",tUrl.get("RES_CONTENT_TYPE").toString());
			}

			//JSON�϶��� ������ Ÿ�� ����
			if( "POST".equals(tUrl.get("METHOD")) && tUrl.get("REQ_CONTENT_TYPE") != null && tUrl.get("REQ_CONTENT_TYPE").toString() != "" ){
				log.info("	������ ������ Ÿ�� (Request Content-Type) : " + tUrl.get("REQ_CONTENT_TYPE").toString());

				urlConn.setRequestProperty("Content-Type",tUrl.get("REQ_CONTENT_TYPE").toString());
				urlConn.setRequestProperty("Content-Length", String.valueOf(((String)tUrl.get("QUERY_STRING")).getBytes().length));
				
				//�� ����
				urlConn.connect();

				os = urlConn.getOutputStream();
				os.write(((String)tUrl.get("QUERY_STRING")).getBytes());
				os.flush();
				os.close();

			}else if("POST".equals(tUrl.get("METHOD")) && tUrl.get("QUERY_STRING") != null){
				//POST�϶� �Է� �Ķ���� ����
				urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConn.setRequestProperty("Content-Length", String.valueOf(((String)tUrl.get("QUERY_STRING")).getBytes().length));

				//�� ����
				urlConn.connect();

				os = urlConn.getOutputStream();
				os.write(((String)tUrl.get("QUERY_STRING")).getBytes());
				os.flush();
				os.close();

			}else{
				//GET�϶��� CONTENT_TYPE�ʿ����

				//�� ����
				urlConn.connect();

			}
			//��û ������ ������
			log.info("	��û ������ ������ : " + urlConn.getContentLength());
	


			int responseCode = urlConn.getResponseCode();
			resMap.put("RES_CD",responseCode);

			if ( responseCode != 200 ) {
				log.info("	[responseCode].......................200 ����(" + responseCode + ")");

				if( responseCode == 302 ){
					log.info("	302 Header Location : " + urlConn.getHeaderField("Location") );
					resMap.put("LOCATION",urlConn.getHeaderField("Location").toString());
				}

			}else{
				log.info("	[responseCode].......................200 ����");
			}

			// ������� �޽����� ���̸�ŭ ���۸� �����Ͽ� �о���̰�, "EUC-KR"�� ���ڵ��ؼ� �о���δ�.
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),(String)tDomain.get("CHARSET")));
			//br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String input;		
			log.info("	[RESPONSE CONTENT]---------------------------------------------------");
			while ((input = br.readLine()) != null){

				resBody.append(input);

				//log.info( input.getBytes("UTF-8") );
				if( "Y".equals( tUrl.get("DEBUG_YN").toString() ) ){
					log.info( input );
				}
				
			}
			br.close();
			resMap.put("RES_BODY",resBody);
		    long elapsedTime = System.currentTimeMillis() - startTime;
			resMap.put("RES_TIME",elapsedTime);
			resMap.put("RTN_CD",String.valueOf(responseCode));
			resMap.put("COOKIE",String.valueOf(getCookie(urlConn)));

			if(responseCode != 200){
				resMap.put("ERROR_CD","HttpResponseCodeError");
				resMap.put("ERROR_MSG","responseCode:" + responseCode);
			}

			log.info("Done");
		}catch(MalformedURLException e){
			resMap.put("RTN_CD","500");
			resMap.put("ERROR_CD","MalformedURLException");
			resMap.put("ERROR_MSG",e.getMessage());
			resMap.put("ERROR_TRACE",e.getStackTrace());

			log.info("	[MalformedURLException]-------------------------------");

			log.info(e.getMessage());
			log.info(e.getStackTrace());
		}catch(IOException e){
			resMap.put("RTN_CD","500");
			resMap.put("ERROR_CD","IOException");
			resMap.put("ERROR_MSG",e.getMessage());
			resMap.put("ERROR_TRACE",e.getStackTrace());

			log.info("	[IOException]-------------------------------");
			log.info(e.getMessage());
			log.info(e.getStackTrace());
		}catch(Exception e){
			resMap.put("RTN_CD","500");
			resMap.put("ERROR_CD","Exception");
			resMap.put("ERROR_MSG",e.getMessage());
			resMap.put("ERROR_TRACE",e.getStackTrace());

			log.info("	[Exception]-------------------------------");
			log.info(e.getMessage());
			log.info(e.getStackTrace());
		}finally{

			if(br != null){
				try{br.close();}catch(Exception e){}
			}
			if(os != null){
				try{os.flush();os.close();}catch(Exception e){}
			}
			if(urlConn != null){
				try{urlConn.disconnect();}catch(Exception e){}
			}
			
		}


		return resMap;
	}

	//Map�� �ٽ� QeuryString���� �����ϱ�
	public static String mapToQuerystring(Map<String,String> queryString){
		log.info("	mapToQuerystring()-------------------------------");

		StringBuilder sb = new StringBuilder();
		try{

			for(Map.Entry<String, String> e : queryString.entrySet()){
			  if(sb.length() > 0){
				  sb.append('&');
			  }
			  sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
			}	
		}catch(UnsupportedEncodingException e){
			log.info("		[UnsupportedEncodingException]-------------------------------");
			log.info(e.getMessage());
			log.info(e.getStackTrace());

		}catch(Exception e){
			log.info("		[Exception]-------------------------------");
			log.info(e.getMessage());
			log.info(e.getStackTrace());

		}
		return sb.toString();
	}


	//QueryString�� Ű ������ �и��ϱ� (���� ��)
	public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
		log.info("	splitQuery()-------------------------------");

		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = url.getQuery();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}

	//QueryString�� Ű ������ �и��ϱ� (��Ƽ ��)
	public static Map<String, List<String>> splitQueryMulti(URL url) throws UnsupportedEncodingException {
	  final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
	  final String[] pairs = url.getQuery().split("&");
	  for (String pair : pairs) {
		final int idx = pair.indexOf("=");
		final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
		if (!query_pairs.containsKey(key)) {
		  query_pairs.put(key, new LinkedList<String>());
		}
		final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
		query_pairs.get(key).add(value);
	  }
	  return query_pairs;
	}


    public final static String encryptSHA512(String target) {
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-512");
            sh.update(target.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : sh.digest()) sb.append(Integer.toHexString(0xff & b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}

	//��ó: http://revf.tistory.com/105 [RevFactory ������Ʈ - ������ �� �̷Ӱ� �ٲٴ� �۾�]
	private static String getCookie(HttpURLConnection conn) {       
        Map<String,List<String>> m = conn.getHeaderFields();
       
        if(!m.containsKey("Set-Cookie")) {
           return "";
        }
       
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
       
        for(String cookie : m.get("Set-Cookie")) {
            if(isFirst)
                isFirst = false;
            else
                sb.append(";");
            sb.append(cookie);
        }
        return sb.toString();
    }


	public static void goGet(){
		log.info("goGet()-------------------------------");

		HttpURLConnection urlConn = null;
		BufferedReader br;

		try{
			log.info("Starting MyTest");
			URL url = new URL("http://sec.shinsegae.com");
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setRequestMethod("GET");//POST
			urlConn.setConnectTimeout(1000);//
			urlConn.setReadTimeout(3000);//


			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setRequestProperty ("Content-Type","application/x-www-form-urlencoded");

			int responseCode = urlConn.getResponseCode();
			if ( responseCode != 200 ) {
				log.info("	[responseCode].......................200 ���� (" + responseCode + ")");
			}else{
				log.info("	[responseCode].......................200 ����");
			}

			// ������� �޽����� ���̸�ŭ ���۸� �����Ͽ� �о���̰�, "EUC-KR"�� ���ڵ��ؼ� �о���δ�.
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));
			//br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String input;		
			log.info("	[RESPONSE CONTENT]---------------------------------------------------");
			while ((input = br.readLine()) != null){
				//log.info( input.getBytes("UTF-8") );
				//log.info( input );
			}
			br.close();


			log.info("Done");

		}catch(MalformedURLException e){
		  log.info("	[MalformedURLException]-------------------------------");

		  log.info(e.getMessage());
		  log.info(e.getStackTrace());
		}catch(IOException e){
		  log.info("	[IOException]-------------------------------");

		  log.info(e.getMessage());
		  log.info(e.getStackTrace());
		}catch(Exception e){
		  log.info("	[Exception]-------------------------------");

		  log.info(e.getMessage());
		  log.info(e.getStackTrace());
		}





	}
}




/*

java.net.URLConnection

- URL ������ �о���ų�, URL �ּҿ� GET / POST�� �����͸� ���� �� �� �����
- �� �������� ������ �����͸� ���� ��  ����
- URL --> openConnection() --> URLConnection  --> getInputStream --> InputStream (��������)
- URL �� OpenStream() : URL�� �Է� ��Ʈ���� ���� (������)
- URLConnection : URL�� �Է�, ��� ��Ʈ�� ����

Construct 
protected URLConnection(URL) : ���� ���� �Ұ��� , OpenConnection���� ������
    
Method
addRequestProperty(String a, String b) : Ű(a) �� ��(b)�� ������ ��û�� �� �ִ� 
                               Properity ���� �̸� ������ ����. Ư�� Ű���� ������ ���� �� �ֵ��� ��
connect() : ���� �� ���� ���� �Ҷ� (connect() ȣ���ؾ� ���� ��� ������)
getAllowUserInteraction() : ���� �� ���� ����ڰ� ������ ��� �� �� �ִ� ȯ�� Ȯ��(boolean)
                               in/output�� �ش� ���� , ���� ��Ʈ�� �������� Ȯ����
getContent() : content ���� ���� ���� (inputStream ���� ���� ��)
getContent(Class[]) : �� ������ class[] �迭 ���� �Է���
getContentEncoding() : ���ڵ� Ÿ���� String���� ������
getContentLength() : content ���� (-1 �̸� ���������� ���� �Ѿ���� �ʾ���)
getContentType() : content �� http�� �Ǿ� �ִ��� Ÿ�� (ex: http-type )
getDate() : content�� ��¥ (new Date(~~) ���� ��ȯ�� ��� �� / Long ����)
getDefaultAllowUserInteraction(): �⺻������ User�� ��� ������ �������� (boolean)
getDefaultUserCaches() : cache�� ����� �� ���� (boolean)
getDoInput() : Server���� �� �����͸� �Է� ���� �� �ִ� �������� (���� ����-default : true)
getDoOutput() : Server���� �� �����͸� ��� �Ҽ� �ִ� ��������
                          (Client ���� -default : false)
getExpiration() : ��ȿ �Ⱓ
getFileNameMap() : File Name Map
getHeaderField(int) : Head Field �� �޾ƿ� (http Head ��)
getHeaderFiled(String) :
getLastModified() : ������ ���� �ð� 

getInputStream() : InputStrema ���� �̾Ƴ�
getOutputStream() : OutputStream ���� �̾Ƴ�

setDoInput(boolean) : Server ��ſ��� �Է� ������ ���·� ���� 
setDoOutput(boolean) : Server ��ſ��� ��� ������ ���·� ����
 - Server�� ����� �ϰ��� �Ҷ��� �ݵ�� �� �� method�� true�� �� ���� ��


��ó: http://ggoreb.tistory.com/114 [���� �ʺ���]



*/