<%@ page contentType="text/html; charset=utf-8" %>
<%@ page import="com.sds.rqreport.scheduler.*" %>
<%
/**
 * 서버API함수폴더는 서버단에서 호출하는 함수들을 모아둔 곳이며
 * 간단한 예를 통해 호출방법을 볼수 있다.
 * 서버단의 함수를 밖으로 도출시킨만큼 
 * 사용전에 반드시 보안상 혹은 세션에 관한 처리가 확실하게 보장되어야 한다.
 *
 * 또한 해당 API는 사용 예시만 보여줄뿐
 * 그외의 작업 (보안, 세션)에 대한 추가적인 일을 수행하지 않는다.  
 * 
 * ※ 반드시 필요한 경우에만 사용할것.
 **/
 
/**
 * 함수 이름 : ScheduleExecution의 getResultFile
 * 
 * RQX를 RQV로 만드는 서버 API 이며, 필요한 파라미터로는 
 * doc : 문서이름
 * runvar : 실행변수
 * resultFileName : 결과파일명
 **/

// 호출하기위한 기본 클래스 인스턴스 생성
RQScheduleAPI scheduleAPI = new RQScheduleAPI();
ScheduleExecution scheexe = new ScheduleExecution();
ScheduleRunInfo       sri = new ScheduleRunInfo();

// 필요한 파라미터 셋팅
sri.doc="/RQReport1.rqx";
sri.resultFileName="/RQReport1.rqv";
sri.runvar="";
sri.user="admin";
sri.callAPI=true;

// 실행 
scheexe.getResultFile(scheduleAPI, sri);
%>