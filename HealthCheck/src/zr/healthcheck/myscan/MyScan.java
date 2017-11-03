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

//날짜처리
import java.util.Date;
import java.text.SimpleDateFormat;

//통신처리
import java.net.URLEncoder;
import java.net.URLDecoder;


//LOG4J
import org.apache.log4j.Logger;

//SSL
import java.security.cert.*;
import javax.net.SocketFactory;
import javax.net.ssl.*;

//jdk 1.8
//컴파일 javac  -cp "C:\Program Files\Java\jdk1.8.0_112\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.8.0_112\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.8.0_112\bin\mybatis-3.4.2.jar;." SqlSessionManager.java MyScan.java
//PC 실행 java -cp "C:\Program Files\Java\jdk1.8.0_112\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.8.0_112\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.8.0_112\bin\mybatis-3.4.2.jar;." com.ssg.myscan.MyScan

//jdk 1.7
//컴파일 javac  -cp "C:\Program Files\Java\jdk1.7.0_79\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.7.0_79\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.7.0_79\bin\mybatis-3.4.2.jar;." SqlSessionManager.java MyScan.java
//PC 실행 java -cp "C:\Program Files\Java\jdk1.7.0_79\bin\log4j-1.2.17.jar;C:\Program Files\Java\jdk1.7.0_79\bin\ojdbc6.jar;C:\Program Files\Java\jdk1.7.0_79\bin\mybatis-3.4.2.jar;." com.ssg.myscan.MyScan



//서버 실행 /usr/java71_64/jre/bin/java -cp "/webapp/appsic/HealthCheck/log4j-1.2.17.jar;/webapp/appsic/HealthCheck/ojdbc6.jar;/webapp/appsic/HealthCheck/mybatis-3.4.2.jar;.;" com.ssg.myscan.MyScan
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

// 참고 자료 : http://gangzzang.tistory.com/entry/Java-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%82%B9Networking

// JDBC 사용 방법
// 1. JDBC 드라이버 로드 : Class.forName(oracle.jdbc.driver.OracleDriver);
// 2. DB Server 연결 : DriverManager.getConnection(jdbc:oracle:thin:@localhost:XE, scott, tiger)
// 3. SQL 쿼리문 명령 : Statement 또는 PreparedStatement
// 4. 결과 처리 : executeQuery(SELECT 일 때), executeUpdate(UPDATE, INSERT, DELETE)
// 5. 연결 종료 :
 
// 연결 순서 : Connection > Statement 또는 PreparedStatement > ResultSet [ 단, ResultSet 은 SELECT 일때만 사용한다 ]
// 연결 종료 순서 : ResultSet > Statement 또는 PreparedStatement > Connection [ 단, ResultSet 은 SELECT 일때만 사용한다 ]
 

public class MyScan{
	static String  COOKIE;
	//private Logger log = Logger.getLogger(this.getClass());
	final static Logger log = Logger.getLogger(MyScan.class);

	//설정정보
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



		//스캔 사이클 정보
		HashMap<String, Object> mapCycle = new HashMap<String, Object>();
		mapCycle.put("FULL_CNT",0); // 첫 실행부터 수행된 총 사이클 수 
		mapCycle.put("TODAY_CNT",0); // 오늘 실행된 사이클 수
		mapCycle.put("TODAY_DT"," "); // 오늘날짜 YYYYMMDD
		Date tmpDay = new Date();
		SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyyMMdd");
		mapCycle.put("TODAY_DT",String.valueOf( tmpFormat.format(tmpDay) ) );

		//db 오픈
		SqlSessionFactory sqlSessionFactory = SqlSessionManager.getSqlSession();
		SqlSession sqlSession = sqlSessionFactory.openSession(false); //false : autoCommit를 하지 않겠다는 의미




		try{
			 
			log.info("	Test.getDomainList-------------------------------1");

			//연속 실패 횟수 저장
			HashMap<String, Object> almFailCntMap = new HashMap<String, Object>();


			//무한 루프
			while( 1==1 ){
				
				//사이클수
				mapCycle.put("FULL_CNT", ((int) mapCycle.get("FULL_CNT")) + 1);

				tmpDay = new Date();
				if( !( (String)mapCycle.get("TODAY_DT") ).equals(String.valueOf( tmpFormat.format(tmpDay) )) ){ //날짜가 하루 경과되면 카운트 초기화
					mapCycle.put("TODAY_DT",String.valueOf( tmpFormat.format(tmpDay) ) );
					mapCycle.put("TODAY_CNT",0);
				}
				mapCycle.put("TODAY_CNT", ((int) mapCycle.get("TODAY_CNT")) + 1);

				//SUM 변수
				int CHK_DOMAIN_CNT = 0;
				int CHK_URL_CNT = 0;


				//변수 초기화
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

				// 도메인 가져오기
				log.info("	Test.getDomainChkList-------------------------------2");
				if(args.length > 0){
					log.info("	args[0] : " + args[0]);

					param.put("DOMAIN_SEQ", args[0]);

					listDomain = sqlSession.selectList("Test.getDomainList",param) ;
				}else{
					listDomain = sqlSession.selectList("Test.getDomainChkList") ;
				}
		

				//스캔 시작 시간
				long scanStartTime = System.currentTimeMillis();

				//스캔 시작 로그 INSERT
				effectCnt = sqlSession.insert("Test.insScan",paramScan);
				log.info("	SEQ_HC_SCAN.currval : " + paramScan.get("LAST_SCAN_SEQ"));

				
				
				// 도메인 리스트 사용하기
				log.info("	listDomain.size()..." + listDomain.size());


				for(int i=0; i < listDomain.size(); i++){

					//도메인별로 쿠키 초기화 ( 도메인 내에서는 쿠키 유지 )
					COOKIE = "";
	
					CHK_DOMAIN_CNT++;

					HashMap mapDomain = (HashMap)listDomain.get(i) ;
					 
					DOMAIN_SEQ	= Integer.parseInt(String.valueOf(mapDomain.get(	"DOMAIN_SEQ"	)));
					SYSTEM_NM	= getHan((String) mapDomain.get(	"SYSTEM_NM"		) + "");
					DOMAIN		= getHan((String) mapDomain.get(	"DOMAIN"		) + ""); 
					ADD_DT		= getHan((String) mapDomain.get(	"ADD_DT"		) + "");
					EMPNO		= getHan((String) mapDomain.get(	"EMPNO"		) + "");
					log.info("	" + DOMAIN_SEQ + ":" + SYSTEM_NM + ":" + DOMAIN + ":" + ADD_DT + ":"+ EMPNO );


					//도메인 로그 INSERT 하기
					paramResult = new HashMap<String, Object>();
					paramResult.put("DOMAIN_SEQ", DOMAIN_SEQ);

					effectCnt = sqlSession.insert("Test.insResult",paramResult) ;
					log.info("	LAST_RESULT_SEQ : " + paramResult.get("LAST_RESULT_SEQ"));
					log.info("	(Test.insResult) effectCnt : " + effectCnt);



					//도메인 SSL_PORT가 정의된 경우 SSL_PORT로 인증서 만료일 검사
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

						log.info("		도메인 SSL 만료일 갱신 (Test.updDomainSsl)");

						//알람 기간에 도달 한 경우 오늘 첫 검사일경우에만 담당자 알람 처리
						log.info("		isNumeric(SSL_ALM_PERIOD) : " + isNumeric( String.valueOf(mapDomain.get("SSL_ALM_PERIOD")) ));
						log.info("		(int)resSsl.get(SSL_DUE_DAY) : " + resSsl.get("SSL_DUE_DAY") );
						log.info("		Integer.parseInt(String.valueOf(mapDomain.get(SSL_ALM_PERIOD))) : " + String.valueOf(mapDomain.get("SSL_ALM_PERIOD")) );


						if( isNumeric( String.valueOf(mapDomain.get("SSL_ALM_PERIOD")) )  &&	isNumeric( String.valueOf(resSsl.get("SSL_DUE_DAY")) ) &&
							(int)resSsl.get("SSL_DUE_DAY") <=  Integer.parseInt(String.valueOf(mapDomain.get("SSL_ALM_PERIOD")))
							){
							log.info("		### RESULT 만료가 도래해서 블라섬톡 알람 ");
							HashMap<String,String> msg =  new HashMap<String, String>();  
							msg.put("header", "HTTP SCAN");
							msg.put("receiver_id", EMPNO);
							msg.put("message", "■ " + SYSTEM_NM + "(" + DOMAIN + ") ■");
							msg.put("message_body","SSL 인증서 만료까지 " + paramResult.get("SSL_DUE_DAY") + "일(Expired Date:" + paramResult.get("SSL_EXPIRE_DT") + ") 남았습니다." );

							//오늘 첫번째 DOMAIN 스캔 시에만 블라섬 PUSH를 전송하기
							mapResultCnt = sqlSession.selectOne("Test.getResult_TodayCnt",mapDomain);
							if(mapResultCnt != null && isNumeric( String.valueOf(mapResultCnt.get("CNT")) )  && Integer.parseInt(String.valueOf(mapResultCnt.get("CNT"))) == 1 ){
								log.info("		■■■ Send Push (First). ");
								HashMap<String,Object> resPush = goAlarmPush(msg); 
							}else{
								log.info("		■■■ Send No Push.");
							}
						}
						//결과 이력정보에 업데이트 하기
						log.info("		### Test.updResultSsl ");
						effectCnt = sqlSession.update("Test.updResultSsl",paramResult);

						//domain 기준정보에 ssl만료일 업데이트 하기
						log.info("		### Test.updDomainSsl ");
						effectCnt = sqlSession.update("Test.updDomainSsl",paramResult); //SSL 만료일 DB UPDATE
					}

					//해당 도메인의 검사 할 URL LIST 불러오기
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

						//HTTP 응답MAP 선언
						HashMap<String,Object> resMap;
						
						//리다이렉션인 경루 http 헤더 location 받아와서 다시 한번더 http 보내기
						if( "302".equals(mapUrl.get("EXPECT_RES_CD").toString()) ){
							log.info("	EXPECT_RES_CD 302 재전송 시작 ");

							resMap= goHttp(mapDomain, mapUrl);
							if( "302".equals(resMap.get("RES_CD").toString()) ){
								//쿠키 넣기
								COOKIE = String.valueOf(resMap.get("COOKIE"));

								FULL_URL = mapUrl.get("FULL_URL").toString();//기존 full경로
								mapUrl.put("FULL_URL", resMap.get("LOCATION"));//새 location으로 정보 재전송하기
								mapUrl.put("OLD_FULL_URL", FULL_URL);//기존 경로 재사용 가능해서 저장해 놓기

								//재전송해서 다시 값 가져오기
								resMap= goHttp(mapDomain, mapUrl);

							}else{
								//에러 처리 해야 함
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

						//결과 상세 저장하기
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

						//(INVALID_CD) 페이지 유효성 검사
						//500 기본검사 : 페이지 500에러
						//302,404 기본검사 : 페이지 500에러
						//100 BYTE 범위 MIN
						//110 BYTE 범위 MAX
						//120 TIMEOUT 범위 초과
						//130 CONTENT_IN_STR 해당 문자열이 포함되어야 함
						//140 CONTENT_HASH 해쉬값이 일치하는지 검사
						//999 알수없는 에러
						log.info("	VALID.............................................start");
						log.info("		RES_CD = " + resMap.get("RES_CD"));

						PUSH_MSG = "";
						if("200".equals(resMap.get("RES_CD").toString())){
							log.info("		200");

							//결과값 검사
							if( mapUrl.get("BYTE_MIN") != null && Integer.parseInt(mapUrl.get("BYTE_MIN").toString()) > ((StringBuffer)resMap.get("RES_BODY")).length()){
								log.info("			Not Valid ( Cause : BYTE_MIN )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "100");
								PUSH_MSG = "최소바이트(" + mapUrl.get("BYTE_MIN") + ")보다 응답 결과값 바이트(" + ((StringBuffer)resMap.get("RES_BODY")).length() + ")가 작습니다.";
							}else if( mapUrl.get("BYTE_MAX") != null && Integer.parseInt(mapUrl.get("BYTE_MAX").toString()) < ((StringBuffer)resMap.get("RES_BODY")).length()){
								log.info("			Not Valid ( Cause : BYTE_MAX )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "110");
								PUSH_MSG = "최대바이트(" + mapUrl.get("BYTE_MAX") + ")보다 응답 결과값 바이트(" + ((StringBuffer)resMap.get("RES_BODY")).length() + ")가 큽니다.";
							}else if( mapUrl.get("RES_TIME_MAX") != null && Integer.parseInt(mapUrl.get("RES_TIME_MAX").toString()) < Integer.parseInt(resMap.get("RES_TIME").toString()) ){
								log.info("			Not Valid ( Cause : TIMEOUT )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "120");
								PUSH_MSG = "최대응답시간[ms](" + mapUrl.get("RES_TIME_MAX") + ")보다 실제 응답시간(" + resMap.get("RES_TIME") + ")이 큽니다.";

							//포함되거나 일치하지 안으면 에러
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
								PUSH_MSG = "응답결과 BODY에 포함되어야 할 유효문자열(" + mapUrl.get("CONTENT_IN_STR") + ")이 없습니다.";
							}else if( mapUrl.get("CONTENT_HASH") != null && resMap.get("CONTENT_HASH") != encryptSHA512(((StringBuffer)resMap.get("RES_BODY")).toString()) ){
								log.info("			Not Valid ( Cause : CONTENT_HASH )");
								param.put("SUCCESS_YN", "N");
								param.put("INVALID_CD", "140");
								PUSH_MSG = "사전 정의딘 BODY의 해쉬값(" + mapUrl.get("CONTENT_HASH") + ")과 응답 받은 BODY의 해쉬값(" + encryptSHA512(((StringBuffer)resMap.get("RES_BODY")).toString()) + ")이 일치하지 않습니다.";
							}else{
								log.info("			ELSE");
								param.put("SUCCESS_YN", "Y");
							}

							//PUSH 메시지에 날짜 추가
							Date tNow = new Date();
							SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
							log.info(format.format(tNow)); // 20090529
							PUSH_MSG = PUSH_MSG + "\n" + format.format(tNow).toString();


						}else if("500".equals(resMap.get("RES_CD").toString())){
							log.info("		500");

							PUSH_MSG = "500 페이지 에러 발생";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "500");
						}else if("302".equals(resMap.get("RES_CD").toString())){
							log.info("		302");

							PUSH_MSG = "302 리다이렉트로 페이지 이동 필요 (Location:" + resMap.get("LOCATION") + ")";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "302");
						}else if("404".equals(resMap.get("RES_CD").toString())){
							log.info("		404");

							PUSH_MSG = "404 페이지 없음";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "404");
						}else{
							log.info("		ELSE");

							PUSH_MSG = "999 알 수 없는 에러 발생";

							param.put("SUCCESS_YN", "N");
							param.put("INVALID_CD", "999");
						}

						param.put("PUSH_MESSAGE_BODY", putHan( PUSH_MSG ));


						//블라섬 테스트 발송
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
							msg.put("message", "■ " + SYSTEM_NM + "(" + DOMAIN + ") ■");

							param.put("PUSH_HEADER",putHan(msg.get("header")));
							param.put("PUSH_RECEIVER_ID",msg.get("receiver_id"));
							param.put("PUSH_MESSAGE",putHan(msg.get("message")));
							//param.put("PUSH_MESSAGE_BODY",putHan(msg.get("message_body")));


							//해당 URL 연속 실패 횟수 관리
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


							//블톡 메시지
							if( "Y".equals(param.get("SUCCESS_YN").toString()) ){
								msg.put("message_body", "결과=성공\n경로=[" + mapUrl.get("URL_SEQ").toString() + "/" + mapUrl.get("HOST").toString() + "/" + getHan(mapUrl.get("URL_NM").toString()) + "]" + mapUrl.get("FULL_URL").toString() + "\n사유=" + PUSH_MSG);
							}else{
							msg.put("message_body", "결과=실패(연속횟수:" + almFailCntMap.get( String.valueOf( mapUrl.get("URL_SEQ") ) ) + ")\n경로=[" + mapUrl.get("URL_SEQ").toString() + "/" + mapUrl.get("HOST").toString() + "/" + getHan(mapUrl.get("URL_NM").toString()) + "]" + mapUrl.get("FULL_URL").toString() + "\n사유=" + PUSH_MSG);
							}



							//연속 실패 횟수가 DB정의된 횟수와 같으면 PUSH발송
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

								//PUSH 발송후 알랔 카운트 초기화처리
								almFailCntMap.put(	String.valueOf( mapUrl.get("URL_SEQ") ), null);

							}
						}else{
							//해당 URL 성공하면 연속 실패 횟수 초기화
							log.info("	B555");
							almFailCntMap.put(	String.valueOf( mapUrl.get("URL_SEQ") ), null);
						}

						//결과 DB 저장
						effectCnt = sqlSession.insert("Test.insResultD",param) ;
						log.info("	(Test.insResultD) effectCnt : " + effectCnt);
					

					}// for(int k=0; k < listUrl.size(); k++){		 

					//해당 도메인 점걸 결과 단위 update
					effectCnt = sqlSession.update("Test.updResult",paramResult);


				}//for(int i=0; i < listDomain.size(); i++){

				
				//스캔 시작 로그 INSERT
			    long elapsedTime = System.currentTimeMillis() - scanStartTime;
				paramScan.put("FULL_SCAN_TIME",elapsedTime);
				effectCnt = sqlSession.update("Test.updScan",paramScan);

				sqlSession.commit();

				//잠시 대기 합니다.
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

			}//무한 while loop end

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
		msg.put("message", "HEALTH SCAN 데몬 오류 발생");
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




		//리턴 객체
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("RTN_CD","000");
		resMap.put("RTN_DATA",0); // 남은 날짜 리턴
		resMap.put("RTN_MSG","");
		resMap.put("RES_CD","");
		resMap.put("ERROR_CD","");
		resMap.put("ERROR_MSG","");
		resMap.put("ERROR_TRACE","");

		//SSL포트 정의 여부 검사
		log.info("	isNumeric SSL_PORT check" );

		if( !isNumeric(String.valueOf(tDomain.get("SSL_PORT"))) ){
			log.info("	SSL 포트가 정의되지 않았습니다.");

			resMap.put("RTN_CD","510");
			resMap.put("RTN_MSG","비정상");
			resMap.put("ERROR_CD","NO SSL PORT");
			resMap.put("ERROR_MSG","SSL포트 미정의");
			return resMap;
		}

		//host port세팅
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
				//오류 나면 초기화 시키기
			}
			resMap.put("RTN_CD","530");
			resMap.put("RTN_MSG","비정상");
			resMap.put("ERROR_CD","NOT SSL");
			resMap.put("ERROR_MSG","Plaintext connection");

			log.info("[SSLException] Plaintext connection"); // SSL 아니고 그냥 HTTP 통신임
		}catch(IOException e){
			if(socket != null){
				try{
					socket.close();
					socket = null;
				}catch(IOException ex){}
				//오류 나면 초기화 시키기
			}
			resMap.put("RTN_CD","540");
			resMap.put("RTN_MSG","비정상");
			resMap.put("ERROR_CD","NOT SSL");
			resMap.put("ERROR_MSG","[IOException] Plaintext connection");

			log.info(e.getMessage());
			log.info(e.getStackTrace());


			log.info("[IOException] Plaintext connection"); // SSL 아니고 그냥 HTTP 통신임
		}


		if(socket != null){
			log.info("111");

			Certificate[] certs = null;
			try{
				certs = socket.getSession().getPeerCertificates();
			}catch(SSLPeerUnverifiedException e){
				log.info(e.getMessage());
				log.info(e.getStackTrace());
				log.info("[SSLPeerUnverifiedException] "); // SSL 아니고 그냥 HTTP 통신임

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

					//날짜 검사
					Date expDate = ( (X509Certificate) cert).getNotAfter();
					long expTime = expDate.getTime();

					int dayOffSet = (int) ((expTime - curTime) / (1024 * 60 * 60 * 24));
					log.info("dayOffSet:" + dayOffSet); //SSL 갱신까지 남은 날짜
					resMap.put("SSL_DUE_DAY",dayOffSet); 

					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					log.info("expDate" + format.format(expDate)); // 20090529

					resMap.put("SSL_EXPIRE_DT",format.format(expDate)); //만료일 리턴

					try {
						( (X509Certificate) cert).checkValidity();
						log.info("Certificate is active for current date");

						resMap.put("RTN_CD","200");
						resMap.put("RTN_MSG","정상");

					} catch(CertificateNotYetValidException cee) {
						resMap.put("RTN_CD","550");
						resMap.put("RTN_MSG","비정상");
						resMap.put("ERROR_CD","SSL EXPIRED");
						resMap.put("ERROR_MSG","Certificate is expired");

						log.info("Certificate Not Yet Valid");
					} catch(CertificateExpiredException cee) {
						resMap.put("RTN_CD","560");
						resMap.put("RTN_MSG","비정상");
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

		//리턴 객체
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


		//응답 시간
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

			//★ 연결
			urlConn.connect();

			//POST일때 입력 파라미터 전송
			if("POST".equals(tUrl.get("METHOD")) && tUrl.get("QUERY_STRING") != null){
				OutputStream os = urlConn.getOutputStream();
				os.write(((String)tUrl.get("QUERY_STRING")).getBytes("UTF-8"));
				os.flush();
				os.close();
			}

			int responseCode = urlConn.getResponseCode();
			resMap.put("RES_CD",responseCode);

			if ( responseCode != 200 ) {
				log.info("	[responseCode].......................200 실패 (" + responseCode + ")");
			}else{
				log.info("	[responseCode].......................200 성공");
			}

			// 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
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
				resMap.put("RTN_MSG","발송 성공");
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


		//url관련 객체
		HttpURLConnection urlConn = null;
		BufferedReader br = null;
		OutputStream os = null;
		URL url = null;
		Map<String, String> qsMap = null;
		String qsStr = "";


		//리턴 객체
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



		//응답 시간
		long startTime = System.currentTimeMillis();



		try{
			log.info("Starting MyTest");

			//db에서 URL받아와서 세팅하기
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

			//파라미터가 있으면 파라미터 인코딩 처리
			if( (  (String)tUrl.get("FULL_URL") ).indexOf("?") > 0 && "Y".equals((String)tUrl.get("FORCE_QS_ENC_YN"))){
				//쿼리스트링 맵으로 받기
				qsMap = splitQuery(url);

				//맵을 다시 쿼리스트링 문자열로 변경하기
				qsStr = mapToQuerystring(qsMap);
				log.info("	qsStr : "+ qsStr);

				//쿼리 스트링 분리
				String tmpUrl[] = ((String)tUrl.get("FULL_URL")).split("\\?");

				//최종 URL 세팅하기
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



			//응답시 수신 가능 문서 형태
			if( tUrl.get("RES_CONTENT_TYPE") != null && tUrl.get("RES_CONTENT_TYPE").toString() != "" ){
				log.info("	선언적 컨텐츠 타입 (Accept Content-Type) : " + tUrl.get("RES_CONTENT_TYPE").toString());

				urlConn.setRequestProperty ("Accept",tUrl.get("RES_CONTENT_TYPE").toString());
			}

			//JSON일때는 컨텐츠 타입 변경
			if( "POST".equals(tUrl.get("METHOD")) && tUrl.get("REQ_CONTENT_TYPE") != null && tUrl.get("REQ_CONTENT_TYPE").toString() != "" ){
				log.info("	선언적 컨텐츠 타입 (Request Content-Type) : " + tUrl.get("REQ_CONTENT_TYPE").toString());

				urlConn.setRequestProperty("Content-Type",tUrl.get("REQ_CONTENT_TYPE").toString());
				urlConn.setRequestProperty("Content-Length", String.valueOf(((String)tUrl.get("QUERY_STRING")).getBytes().length));
				
				//★ 연결
				urlConn.connect();

				os = urlConn.getOutputStream();
				os.write(((String)tUrl.get("QUERY_STRING")).getBytes());
				os.flush();
				os.close();

			}else if("POST".equals(tUrl.get("METHOD")) && tUrl.get("QUERY_STRING") != null){
				//POST일때 입력 파라미터 전송
				urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				urlConn.setRequestProperty("Content-Length", String.valueOf(((String)tUrl.get("QUERY_STRING")).getBytes().length));

				//★ 연결
				urlConn.connect();

				os = urlConn.getOutputStream();
				os.write(((String)tUrl.get("QUERY_STRING")).getBytes());
				os.flush();
				os.close();

			}else{
				//GET일때는 CONTENT_TYPE필요없음

				//★ 연결
				urlConn.connect();

			}
			//요청 컨텐츠 사이즈
			log.info("	요청 컨텐츠 사이즈 : " + urlConn.getContentLength());
	


			int responseCode = urlConn.getResponseCode();
			resMap.put("RES_CD",responseCode);

			if ( responseCode != 200 ) {
				log.info("	[responseCode].......................200 실패(" + responseCode + ")");

				if( responseCode == 302 ){
					log.info("	302 Header Location : " + urlConn.getHeaderField("Location") );
					resMap.put("LOCATION",urlConn.getHeaderField("Location").toString());
				}

			}else{
				log.info("	[responseCode].......................200 성공");
			}

			// 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
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

	//Map을 다시 QeuryString으로 변경하기
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


	//QueryString의 키 쌍으로 분리하기 (단일 값)
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

	//QueryString의 키 쌍으로 분리하기 (덜티 값)
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

	//출처: http://revf.tistory.com/105 [RevFactory 프로젝트 - 세상을 더 이롭게 바꾸는 작업]
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
				log.info("	[responseCode].......................200 실패 (" + responseCode + ")");
			}else{
				log.info("	[responseCode].......................200 성공");
			}

			// 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
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

- URL 내용을 읽어오거나, URL 주소에 GET / POST로 데이터를 전달 할 때 사용함
- 웹 페이지나 서블릿에 데이터를 전달 수  있음
- URL --> openConnection() --> URLConnection  --> getInputStream --> InputStream (내용읽음)
- URL 의 OpenStream() : URL의 입력 스트림만 개설 (차이점)
- URLConnection : URL의 입력, 출력 스트림 개설

Construct 
protected URLConnection(URL) : 직접 생성 불가능 , OpenConnection으로 연결함
    
Method
addRequestProperty(String a, String b) : 키(a) 와 값(b)을 가지고 요청할 수 있는 
                               Properity 값을 미리 설정해 놓음. 특정 키값을 가지고 읽을 수 있도록 함
connect() : 연결 된 곳에 접속 할때 (connect() 호출해야 실제 통신 가능함)
getAllowUserInteraction() : 연결 된 곳에 사용자가 서버와 통신 할 수 있는 환경 확인(boolean)
                               in/output이 해당 서버 , 연결 포트로 가능한지 확인함
getContent() : content 값을 리턴 받음 (inputStream 값을 리턴 함)
getContent(Class[]) : 위 내용을 class[] 배열 값을 입력함
getContentEncoding() : 인코딩 타입을 String으로 리턴함
getContentLength() : content 길이 (-1 이면 정상적으로 값이 넘어오지 않았음)
getContentType() : content 가 http로 되어 있는지 타입 (ex: http-type )
getDate() : content의 날짜 (new Date(~~) 으로 변환해 줘야 함 / Long 리턴)
getDefaultAllowUserInteraction(): 기본적으로 User와 통신 가능한 상태인지 (boolean)
getDefaultUserCaches() : cache를 사용할 것 인지 (boolean)
getDoInput() : Server에서 온 데이터를 입력 받을 수 있는 상태인지 (본인 상태-default : true)
getDoOutput() : Server에서 온 데이터를 출력 할수 있는 상태인지
                          (Client 상태 -default : false)
getExpiration() : 유효 기간
getFileNameMap() : File Name Map
getHeaderField(int) : Head Field 값 받아옴 (http Head 값)
getHeaderFiled(String) :
getLastModified() : 마지막 수정 시간 

getInputStream() : InputStrema 값을 뽑아냄
getOutputStream() : OutputStream 값을 뽑아냄

setDoInput(boolean) : Server 통신에서 입력 가능한 상태로 만듬 
setDoOutput(boolean) : Server 통신에서 출력 가능한 상태로 만듬
 - Server와 통신을 하고자 할때는 반드시 위 두 method를 true로 해 놔야 함


출처: http://ggoreb.tistory.com/114 [나는 초보다]



*/