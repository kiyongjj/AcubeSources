package com.sds.acube.jstor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.sds.acube.jstor.FasooPackager;
import com.sds.acube.jstor.SoftCampPackager;

public class JSTORApi extends JSTORSocket
{
   public Socket m_socket = null;

   public BufferedInputStream m_buffSock_In = null;

   public BufferedOutputStream m_buffSock_Out = null;

   public DataInputStream m_dataSock_In = null;

   public DataOutputStream m_dataSock_Out = null;

   public int m_nErrCode; // 에러코드

   public String m_sErrMsg; // 에러 메세지 */

   public String[] m_sRegFileIDArr; // 파일 등록이 성공했을 경우, 반환되는 파일 아이디 배열

   public String[] m_sNewCpyFileIDArr; // 복사 성공한 파일의 New File ID

   public String[][] m_sVolInfoArr; // 볼륨정보

   public String[] m_sOutDrmFilePath;

   public String m_drmType;

   public Hashtable m_drmConfig;
   
   //protected static Logger logger = null;
   
   //DRM 관련 클래스 
   protected FasooPackager m_fasooPackager = null;
   protected SoftCampPackager m_softCampPackager = null;

   /**
    * <pre>
    *                     STORServ 연결 Api
    *                     @param String sIPAddr =&gt; STORServ IP 또는 Host Name(단, 64자 이상은 넘지 말 것)
    *                     @param int nPortNo =&gt; STORServ Port Number
    *                     @return int =&gt; 연결 성공(Connection ID), 연결 실패(-1)
    *                     함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_Connect(String sIPAddr, int nPortNo)
   {
      return -1;
   }

   /**
    * <pre>
    *                    STORserv 연결 종료 Api
    *                    @param int nConnID =&gt; Connection ID
    *                    @return void 
    * </pre>
    */
   public void JSTOR_Disconnect(int nConnID)
   {
   }

   /**
    * <pre>
    *                   STORServ 파일등록 서비스 Api
    *                   @param int nConnID =&gt; Connection ID
    *                   @param int nNumOfFile =&gt; 파일등록 서비스 대상 파일의 갯수 
    *                   @param String sInfoRegArr[i][0] =&gt; 등록할 File Path 
    *                   @param String sInfoRegArr[i][1] =&gt; 등록 대상 Volume ID 
    *                   @param String sInfoRegArr[i][2] =&gt; 등록시 적용할 Fileter ID
    *                   (ex : 0 -&gt; NONE, 1 -&gt; 압축(GZIP 방식), 2 -&gt; 암호화, 3 -&gt; 압축 + 암호화)
    *                   @param String sInfoRegArr[i][3] =&gt; FileCDate(option) 사용하지 않을 시에는 반드시 null 을 대입할 것
    *                   @param int nOption =&gt; 파일등록시 확장 옵션 지정
    *                   (ex : 0 -&gt; NONE, 10 - FASOO DRM 암호화된 파일일 경우, 복호(Extract)후 저장)) 
    *                   @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus), 성공시 JSTOR_getRegFileID() 를 호출할 것.
    *                   함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileReg(int nConnID, int nNumOfFile, String[][] sInfoRegArr, int nOption)
   {
      return -1;
   }

   /**
    * <pre>
    *                  STORServ 파일등록 서비스 Api (중계(Broker) 전송 방식)
    *                  @param int nConnID =&gt; Connection ID
    *                  @param int nNumOfFile =&gt; 파일등록 서비스 대상 파일의 갯수 
    *                  @param Vector[] vInfoRegArr =&gt; 파일등록 정보
    *                  vInfoRegArr[i].elementAt(0) =&gt; [String] 등록 대상 파일의 Full Path (등록할 파일이 존재하는 컴퓨터에서의 File Full Path)
    *                  vInfoRegArr[i].elementAt(1) =&gt; [Integer] 등록할 대상 저장서버의 Volume ID
    *                  vInfoRegArr[i].elementAt(2) =&gt; [Integer] 등록시 적용할 Filter ID
    *                  (ex : 0 -&gt; NONE, 1 -&gt; 압축(GZIP 방식), 2 -&gt; 암호화, 3 -&gt; 압축 + 암호화)
    *                  vInfoRegArr[i].elementAt(3) =&gt; [String] 파일 등록시 강제로 설정할 File Create Date
    *                  (저장서버 DB 에 각 파일의 생성일자(14자리)를 기록하는 필드가 있음, 필요없으면 SKIP)
    *                  (이 때는 파일이 저장서버에 등록되는 일시가 기본으로 지정됨)
    *                  @param Vector vInfoBrokerage =&gt; Brokerage 정보
    *                  vInfoBrokerage.elementAt(0) =&gt; [String] 등록할 파일이 있는 컴퓨터의 IP 또는 Host Name (SFTPD 가 설치되어 있어야 함)
    *                  vInfoBrokerage.elementAt(1) =&gt; [Integer] 위 컴퓨터의 SFTPD Port
    *                  vInfoBrokerage.elementAt(2) =&gt; [Integer] 파일전송 중개 옵션 
    *                  (ex : 0 -&gt; 컴퓨터간의 직접 전송, 1 -&gt; Broker 역할을 하는 컴퓨터를 통한 간접 전송)
    *                  (Broker 역할을 하는 컴퓨터는 &quot;JSTOR JNI Java Class API&quot; 를 호출하는 컴퓨터&quot; 를 말한다)
    *                  vInfoBrokerage.elementAt(3) =&gt; [String] 파일전송 중개시 필요한 임시 디렉토리
    *                  (ex : 중개 옵션 0 -&gt; 저장서버가 설치된 컴퓨터에 임시 디렉토리를 설정해야 함)
    *                  (ex : 중개 옵션 1 -&gt; Broker 역할을 하는 컴퓨터에 임시 디렉토리를 설정)
    *                  @param int nGUIFlag =&gt; GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음 (Deprecated)
    *                  (ex : 0 -&gt; NO, 1 -&gt; YES) 
    *                  @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus), 성공시 JSTOR_getRegFileID() 를 호출할 것.
    *                  함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_BrokerFileReg(int nConnID, int nNumOfFile, Vector[] vInfoRegArr, Vector vInfoBrokerage, int nGUIFlag)
   {
      return -1;
   }

   /**
    * <pre>
    *                 STORServ 파일반출(가져오기) 서비스 Api
    *                 @param int nConnID =&gt; Connection ID
    *                 @param int nNumOfFile =&gt; 파일 가져오기 서비스 대상 파일의 갯수 
    *                 @param String sInfoGetArr[i][0] =&gt; 저장서버로 부터 가져올 File ID 
    *                 @param String sInfoGetArr[i][1] =&gt; 가져온 파일이 저장될 File Path 
    *                 @param String sInfoGetArr[i][2] =&gt; 가져올 때 적용할 Fileter ID (등록할 때 적용된 Filter ID 와 동일해야 함)
    *                 (ex : 0 -&gt; NONE, 1 -&gt; 압축(GZIP 방식), 2 -&gt; 암호화, 3 -&gt; 압축 + 암호화, -1 -&gt; 자동 검출)
    *                 @param int nGUIFlag =&gt; GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음.
    *                 (ex : 0 -&gt; NO, 1 -&gt; YES) 
    *                 @return int =&gt; 서비스 성공(0), 서비스 실패(음수 ,Minus)
    *                 함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileGet(int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nGUIFlag)
   {
      return -1;
   }

   /**
    * <pre>
    *            STORServ 파일반출(가져오기) 서비스 Api 
    *            - DRM 적용 확장, 현재는 FASOO DRM 만 지원
    *            @param int nConnID =&gt; Connection ID
    *            @param int nNumOfFile =&gt; 파일 가져오기 서비스 대상 파일의 갯수 
    *            @param String sInfoGetArr[i][0] =&gt; 저장서버로 부터 가져올 File ID 
    *            @param String sInfoGetArr[i][1] =&gt; 가져온 파일이 저장될 File Path 
    *            - 일반적으로 DRM 적용시에는, 저장서버로 부터 파일반출이 성공하고 난 후 DRM Packager 의 작업대상 파일(원본)이 됨
    *            @param String sInfoGetArr[i][2] =&gt; 가져올 때 적용할 Fileter ID 
    *            - 등록할 때 적용된 Filter ID 와 동일해야 함
    *            - ex) 0 -&gt; NONE, 1 -&gt; 압축(GZIP 방식), 2 -&gt; 암호화, 3 -&gt; 압축 + 암호화, -1 -&gt; 자동 검출
    *            @param int nGUIFlag =&gt; GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음
    *            - ex) 0 -&gt; NO, 1 -&gt; YES
    *            @param int nDRMEnabled =&gt; DRM 기능을 활성화 시킬지 여부
    *            - ex) 0 -&gt; Disabled, 1 -&gt; Enabled
    *            @param String sDRMType =&gt; 적용할 DRM 모델
    *            - FASOO DRM 적용시 반드시 &quot;FASOO&quot; 를 입력할 것
    *            @param String[][] sEssentialMetaData =&gt; DRM 적용 대상 파일들의 정보 (필수입력)
    *            - sInfoGetArr 에 포함된 파일들 각각에 대해서 설정해 줘야 함
    *            @param String sEssentialMetaData[i][0] =&gt; Fasoo DRM(FSD) Packager ADK Home Directory
    *            @param String sEssentialMetaData[i][1] =&gt; FSD 서버 ID, Packager activation 이 완료된 서버에서, Packager 가 사용하는 키 파일들에 대한 정보를 알려주는 용도로 사용
    *            - 16진수 문자열 16자리
    *            @param String sEssentialMetaData[i][2] =&gt; Secure Container 로 만들어질 파일을 지정, Secucure Container 로 생성될 원본 파일에 대한 Full Path 이름을 지정함
    *            - 일반적으로 sInfoGetArr[i][1] 에서 설정한 경로를 그대로 사용함
    *            @param String sEssentialMetaData[i][3] =&gt; 생성될 Secure Container 의 파일이름을 지정, 생성될 Secure Container 의 파일이름에 대한 Full Path 이름을 지정하는데 사용 
    *            - nNumOfFile 이 1개 일 경우, &quot;null&quot; 을 입력하면, 원본 파일에 &quot;.fsc&quot; 를 붙인 DRM Secure Container 파일이 생성됨
    *            - nNumOfFile 이 2개 이상(복수) 일 경우, 이 값을 &quot;null&quot; 을 지정하면, 첫번째 원본파일명의 끝에 순서대로 &quot;_0.fsc&quot;, &quot;_1.fsc&quot; 와 같은 형태로 Secure Container 파일명이 생성됨
    *            - 이것은 Fasoo Packager 의 디폴트 규약이므로, 혼동을 방지하려면, 반드시 별도의 파일명을 지정해 줘야 함
    *            - 별도로 파일명을 지정할 경우, &quot;.fsc&quot; 확장자는 자동으로 부여됨.
    *            - 원본 파일이 write 가 불가능한 매체(CD-ROM 등)에 있는 경우에는 반드시, 이 파라미터를 이용해서 저장될 곳의 위치를 지정해야 함
    *            @param String sEssentialMetaData[i][4] =&gt; Content 공급자의 ID 를 지정 
    *            암호화될 문서의 공급자 ID 를 내부 메타 정보로 저장해 주는 Parameter
    *            @param String sEssentialMetaData[i][5] =&gt; Content 공급자의 이름을 지정  
    *            - 암호화될 문서의 공급자 이름을 내부 메타 정보로 저장해 주는 Parameter
    *            @param String sEssentialMetaData[i][6] =&gt; Content 공급자에 대한 부가 정보(Description) 를 지정 
    *            -  호화될 문서의 공급자에 대한 부가정보를 내부 메타 정보로로 저장해 주는 Parameter
    *            @param String sEssentialMetaData[i][7] =&gt; Content 의 제목을 지정함 
    *            - 암호화될 Content 의 제목을 내부 메타 정보로 저장해 주는 Parameter
    *            @param String[][] sAdditionalMetaData =&gt; DRM 파일 패키징시 추가할 정보를 설정함 (선택적 입력)
    *            - sEssentialMetaData 에서 설정한 파일들 중 &quot;추가정보&quot; 가 필요한 파일들에 대해서 설정하면 됨.
    *            - FASOO DRM 적용시, 추가 정보는 총 5개까지 설정 가능
    *            - 추가 정보가 필요치 않은 경우, &quot;null&quot; 입력
    *            ** MarkAny일 경우
    *            @param String[][] sEssentialMetaData =&gt; DRM 적용 대상 파일들의 정보 (필수입력)
    *            - sInfoGetArr 에 포함된 파일들 각각에 대해서 설정해 줘야 함
    *            @param String sEssentialMetaData[i][0] =&gt; 원본파일명(Full Path)
    *            @param String sEssentialMetaData[i][1] =&gt; 암호화된 파일명(Full Path)
    *            @param String sEssentialMetaData[i][2] =&gt; DRM 암호화 서버 IP
    *            @param String sEssentialMetaData[i][3] =&gt; DRM 암호화 서버 PORT(40001로 고정)
    *            @param String sEssentialMetaData[i][4] =&gt; 사용자 ID : 어떤 값이어도 상관없음
    *            @param String sEssentialMetaData[i][5] =&gt; 문서등록자 ID : 어떤 값이어도 상관없음
    *            @param String sEssentialMetaData[i][6] =&gt; Site명 (회사명), 삼성투신운용: SSFUND - 사이트별 발급
    *            @param String sEssentialMetaData[i][7] =&gt; 실시간 권한제어 여부 (1로 고정)
    *            @param String sEssentialMetaData[i][8] =&gt; DRM 로그 서버 IP
    *            - Ex) 127.0.0.1:40002
    *            @param String sEssentialMetaData[i][9] =&gt; 문서교환정책 (1로 고정)
    *            @param String sEssentialMetaData[i][10] =&gt; 암호화 여부(1이면 암호화안함, 0이면 암호화)
    *            @param String[][] sAdditionalMetaData =&gt; DRM 파일 패키징시 추가할 정보를 설정함 (선택적 입력-모듈마다 다름)
    *            - 해당 예제는 EDM 기준임
    *            @param String sAdditionalMetaData[i][0] =&gt; System ID (ex-ACUBE_DM, ACUBE_KM)
    *            @param String sAdditionalMetaData[i][1] =&gt; DocumentID
    *            @param String sAdditionalMetaData[i][2] =&gt; DB Alias
    *            @return int =&gt; 서비스 성공(0), 서비스 실패(음수 ,Minus)
    *            함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것
    *            이 API 에서는 DRM Secure Container 파일을 만들때, 
    *            중간에 생성된 &quot;저장서버로 부터 가져온 원본파일&quot; 및 최종적으로 생성된 DRM 파일을 삭제하지 않음. (API 채용 Application 영역으로 유보)
    * </pre>
    */
   public int[] JSTOR_FileGetExDRM(int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nGUIFlag, int nDRMEnabled,
         String sDRMType, String[][] sEssentialMetaData, String[][] sAdditionalMetaData)
   {
      return null;
   }

   public int[] JSTOR_FileGetExDRM(int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nGUIFlag, int nDRMEnabled,
         String sDRMType, String[][] sEssentialMetaData, String[][] sAdditionalMetaData, Vector vPrivInfo)
   {
      return null;
   }

   /**
    * <pre>
    *                 DRM 패키징된 파일 경로 반환 Api =&gt; 
    *                 @return String[] =&gt; 파일 경로 배열
    * </pre>
    */
   public String[] JSTOR_getOutDrmFilePath()
   {
      return null;
   }

   /**
    * <pre>
    *                STORServ 파일반출(가져오기) 서비스 Api (중계(Broker) 전송 방식)
    *                @param int nConnID =&gt; Connection ID
    *                @param int nNumOfFile =&gt; 파일등록 서비스 대상 파일의 갯수 
    *                @param Vector[] vInfoGetArr =&gt; 파일등록 정보
    *                @param String sInfoGetArr[i][0] =&gt; 저장서버로 부터 가져올 File ID 
    *                @param String sInfoGetArr[i][1] =&gt; 가져온 파일이 저장될 File Path (실제 파일이 저장될 컴퓨터에서의 File Full Path)
    *                @param String sInfoGetArr[i][2] =&gt; 가져올 때 적용할 Fileter ID (등록할 때 적용된 Filter ID 와 동일해야 함)
    *                (ex : 0 -&gt; NONE, 1 -&gt; 압축(GZIP 방식), 2 -&gt; 암호화, 3 -&gt; 압축 + 암호화, -1 -&gt; 자동 검출)
    *                @param Vector vInfoBrokerage =&gt; Brokerage 정보
    *                vInfoBrokerage.elementAt(0) =&gt; [String] 가져온 파일이 저장될 컴퓨터의 IP 또는 Host Name (SFTPD 가 설치되어 있어야 함)
    *                vInfoBrokerage.elementAt(1) =&gt; [Integer] 위 컴퓨터의 SFTPD Port
    *                vInfoBrokerage.elementAt(2) =&gt; [Integer] 파일전송 중개 옵션 
    *                (ex : 0 -&gt; 컴퓨터간의 직접 전송, 1 -&gt; Broker 역할을 하는 컴퓨터를 통한 간접 전송)
    *                (Broker 역할을 하는 컴퓨터는 &quot;JSTOR JNI Java Class API&quot; 를 호출하는 컴퓨터&quot; 를 말한다)
    *                vInfoBrokerage.elementAt(3) =&gt; [String] 파일전송 중개시 필요한 임시 디렉토리
    *                (ex : 중개 옵션 0 -&gt; 저장서버가 설치된 컴퓨터에 임시 디렉토리를 설정해야 함)
    *                (ex : 중개 옵션 1 -&gt; Broker 역할을 하는 컴퓨터에 임시 디렉토리를 설정)
    *                @param int nGUIFlag =&gt; GUI Interface 지원, 현재 Win32 및 UNIX 플랫폼 모두 지원하지 않고 있음 (Deprecated)
    *                (ex : 0 -&gt; NO, 1 -&gt; YES) 
    *                @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus), 성공시 JSTOR_getRegFileID() 를 호출할 것.
    *                함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_BrokerFileGet(int nConnID, int nNumOfFile, Vector[] vInfoGetArr, Vector vInfoBrokerage, int nGUIFlag)
   {
      return -1;
   }

   /**
    * <pre>
    *                STORServ Commit Api
    *                @param int nConnID =&gt; Connection ID
    *                @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *                함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_Commit(int nConnID)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ Rollback Api
    *              @param int nConnID =&gt; Connection ID
    *              @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_Rollback(int nConnID)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ Volume 정보검색 Api
    *              @param int nConnID =&gt; Connection ID
    *              @return int =&gt; 서비스 성공(반환 볼륨 정보 수), 서비스 실패(음수, Minus), 성공시 JSTOR_getVolInfo() 를 호출할 것
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_VolInfo(int nConnID)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 파일 삭제 서비스 Api
    *              @param int nConnID =&gt; Connection ID
    *              @param int nNumOfFile =&gt; 파일 삭제 서비스 대상 파일의 갯수 
    *              @param String sInfoDelArr[i] =&gt; 삭제할 File ID 
    *              @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileDel(int nConnID, int nNumOfFile, String[] sInfoDelArr)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 파일 교체 서비스 Api
    *              @param int nConnID =&gt; Connection ID
    *              @param int nNumOfFile =&gt; 파일 교체 서비스 대상 파일의 갯수 
    *              @param String sInfoRepArr[i][0] =&gt; 교체될 대상 File ID
    *              @param String sInfoRepArr[i][1] =&gt; 교체할 File Path
    *              @param String sInfoRepArr[i][2] =&gt; 교체하면서 적용될 Fileter ID (JSTOR_FileReg 참조)
    *              @param int nOption =&gt; 파일등록시 확장 옵션 지정
    *              (ex : 0 -&gt; NONE, 10 - FASOO DRM 암호화된 파일일 경우, 복호(Extract)후 저장)) 
    *              @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileRep(int nConnID, int nNumOfFile, String[][] sInfoRepArr, int nOption)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 파일 복사 서비스 Api
    *              @param int nConnID =&gt; Connection ID
    *              @param int nNumOfFile =&gt; 파일 복사 서비스 대상 파일의 갯수 
    *              @param String sInfoCpyArr[i][0] =&gt; 복사될 대상 File ID 
    *              @param String   sInfoCpyArr[i][1] =&gt; 복사될 대상 Volume ID
    *              @param String   sInfoCpyArr[i][2] =&gt; 복사되면서 적용될 Filter ID (JSTOR_FileReg 참조)
    *              @param String   sInfoCpyArr[i][3] =&gt; 복사될 대상 저장서버 구분 
    *              (ex : 1 -&gt; 로컬 저장서버, 2 -&gt; 원격 저장서버)
    *              @param String sInfoCpyArr[i][4] =&gt; 복사될 대상 저장서버 IP
    *              @param String sInfoCpyArr[i][5] =&gt; 복사될 대상 저장서버 Port 
    *              @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileCpy(int nConnID, int nNumOfFile, String[][] sInfoCpyArr)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 파일 이동 서비스 Api
    *              @param int nConnID =&gt; Connection ID
    *              @param int nNumOfFile =&gt; 파일 이동 서비스 대상 파일의 갯수 
    *              @param String sInfoCpyArr[i][0] =&gt; 이동될 대상 File ID
    *              @param String   sInfoCpyArr[i][1] =&gt; 이동될 대상 Volume ID
    *              @param String   sInfoCpyArr[i][2] =&gt; 이동되면서 적용될 Filter ID 
    *              @param String   sInfoCpyArr[i][3] =&gt; 이동될 대상 저장서버 구분 
    *              (ex : 1 -&gt; 로컬 저장서버, 2 -&gt; 원격 저장서버)
    *              @param String sInfoCpyArr[i][4] =&gt; 이동될 대상 저장서버 IP 
    *              @param String sInfoCpyArr[i][5] =&gt; 이동될 대상 저장서버 Port 
    *              @return int =&gt; 서비스 성공(0), 서비스 실패(음수 ,Minus)
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileMov(int nConnID, int nNumOfFile, String[][] sInfoMovArr)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 파일 정보검색 서비스 Api
    *              @param int nConnID =&gt; Connection ID
    *              @param int nNumOfFile =&gt; 파일 정보검색 서비스 대상 파일의 갯수 
    *              @param String sInfoInfoArr[i] =&gt; 파일 정보검색 대상 File ID
    *              @return int =&gt; 서비스 성공(0), 서비스 실패(음수, Minus)
    *              함수호출이 실패할 경우, JSTOR_GetErrMsg, JSTOR_GetErrCode 를 호출해서, 에러내용을 확인할 것.
    * </pre>
    */
   public int JSTOR_FileInfo(int nConnID, int nNumOfFile, String[] sInfoInfoArr)
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 파일 정보검색 서비스가 성공했을 경우, 각 파일의 정보를 Return 하는 Api,
    *              반드시 JSTOR_FileInfo() 함수가 성공한 다음에 사용하여야 함.
    *              @param void
    *              @return String[][] =&gt; 2차원 배열의 각 칼럼의 내용은 다음과 같다.
    *              String[i][0] : File Name
    *              String[i][1] : File Size
    *              String[i][2] : File Extension 
    *              String[i][3] : File Filter
    *              String[i][4] : File Creation Date
    *              String[i][5] : File Modification Date
    *              String[i][6] : Volume ID 
    * </pre>
    */
   public String[][] JSTOR_getFileInfo()
   {
      return null;
   }

   /**
    * <pre>
    *              STORServ 파일등록 서비스에서 복사 성공한 파일들의, 복사된 후 New File ID 를 Return 하는 Api, 
    *              반드시 JSTOR_FileCpy() 함수가 성공한 다음에 사용하여야 함.
    *              @param void
    *              @return String[] =&gt; File ID 의 배열
    * </pre>
    */
   public String[] JSTOR_getNewCpyFileID()
   {
      return null;
   }

   /**
    * <pre>
    *              STORServ Volume 정보검색 서비스가 성공했을 경우, 각 볼륨의 정보를 Return 하는 Api,
    *              반드시 JSTOR_VolInfo() 함수가 성공한 다음에 사용하여야 함. JSTOR_VolInfo() 의 리턴값이 볼륨의 갯수가 됨.
    *              @param  void
    *              @return String[][] =&gt; 2차원 배열의 각 칼럼의 내용은 다음과 같다.
    *              String[i][0] : Volume ID
    *              String[i][1] : Volume Name
    *              String[i][2] : Volume English Name, 저장서버 DB 에 해당 정보가 없을 경우, &quot;none&quot; 을 리턴
    *              String[i][3] : Volume Type
    *              String[i][4] : Volume Access Right
    *              String[i][5] : Volume Creation Date
    *              String[i][6] : Volume Description, 저장서버에 DB 에 해당 정보가 없을 경우, &quot;none&quot; 을 리턴
    * </pre>
    */
   public String[][] JSTOR_getVolInfo()
   {
      return null;
   }

   /**
    * <pre>
    *              STORServ 파일등록 서비스에서 등록이 성공한 파일들의 File ID 를 Return 하는 Api, 
    *              반드시 JSTOR_FileReg() 함수가 성공한 다음에 사용하여야 함.
    *              @param void
    *              @return String[] =&gt; File ID 의 배열
    * </pre>
    */
   public String[] JSTOR_getRegFileID()
   {
      return null;
   }

   /**
    * <pre>
    *              STORServ 에러 코드 반환 Api
    *              @param void
    *              @return int =&gt; 관련 에러 코드
    * </pre>
    */
   public int JSTOR_getErrCode()
   {
      return -1;
   }

   /**
    * <pre>
    *              STORServ 에러 메세지 반환 Api
    *              @param void
    *              @return String =&gt; 관련 에러 메세지
    * </pre>
    */
   public String JSTOR_getErrMsg()
   {
      return null;
   }

   /**
    * <pre>
    *              DRM 환경 설정
    *              @param String drmType : DRM 타입
    *              @param Hashtable drmConfig : DRM Configuration
    *              @return void
    * </pre>
    */
   public void JSTOR_SetDrmConfig(String drmType, Hashtable drmConfig)
   {
      m_drmType = drmType;
      m_drmConfig = drmConfig;
   }

   public int[] JSTOR_EncodeDRMFile(int nConnID, int nNumOfFile, String[] sFilePath, String sDRMType, String[][] sEssentialMetaData, String[][] sAdditionalMetaData)
	{
	   return null;
	}

   public int[] JSTOR_EncodeDRMFile(int nConnID, int nNumOfFile, String[] sFilePath, String sDRMType, String[][] sEssentialMetaData, String[][] sAdditionalMetaData, Vector vPrivInfo)
	{
	   return null;
	}

}
