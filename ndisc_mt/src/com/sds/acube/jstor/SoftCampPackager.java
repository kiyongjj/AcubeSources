package com.sds.acube.jstor;

import java.io.File;
import java.util.Vector;

import SCSL.*;

public class SoftCampPackager
{
	public String m_strPathForProperty = null;   // 환경설정 파일(softcamp.properties)의 경로(풀패스) : only for SOFTCAMP_V3.1

   // 파일 암호화(팩키징)
   public synchronized int[] makePackage(int nNumOfFile, String[][] sEssentialMetaData, String[][] sAdditionalMetaData, 
         String[] sRetErrMsg, String[] sOutDrmFilePath, Vector vPrivInfo)
   {
		int arrRet[] = new int[nNumOfFile];
		int nRet = -1;

	    for (int i = 0; i < nNumOfFile; i++)
		{
			sRetErrMsg[i] = "UNKNOWN";
		}
		
		// step 1) 클래스 생성
		SCSL.SLDsFile slFile = new SCSL.SLDsFile();

//System.out.println("[JSTOR SOFTCAMP DEBUG] : SLDsFile()");

		// step 2) 초기화
		
		if (null != m_strPathForProperty)
		{
			slFile.SettingPathForProperty(m_strPathForProperty);

//System.out.println("[JSTOR SOFTCAMP DEBUG] : SettingPathForProperty()");

		}
		
		slFile.SLDsInitDAC();   // DAC 방식

//System.out.println("[JSTOR SOFTCAMP DEBUG] : SLDsInitDAC()");

		// step 3) 권한 추가
		for (int j = 0; j < vPrivInfo.size(); j++)
		{
			String[] arrPriv = (String[])vPrivInfo.get(j);

			String sPrivType    = arrPriv[0];    // 권한 타입 - U(사용자), D(부서)
			String sPrivMembers = arrPriv[1];    // 권한을 부여받을 멤버리스트 - 각 멤버간은 ";" 로 구분
			String sPrivAuth    = arrPriv[2];    // 권한 문자열 - 별도 참조
			int    nExpireDate  = Integer.parseInt(arrPriv[3]);  // 유효 기간 설정	0: 기한없음,  1: 당일만,  2: 명일까지,  n: n일까지
			int    nReadLimit   = Integer.parseInt(arrPriv[4]);  // 읽기 횟수	0: 읽기횟수 제한 없음,  n: 읽기 n회 가능
			int    nPrintLimit  = Integer.parseInt(arrPriv[5]);  // 프린트 횟수	0: 출력횟수 제한 없음,  n: 출력 n회 가능
		
			if("U".equalsIgnoreCase(sPrivType))
			{
				slFile.SLDsAddUserDAC(sPrivMembers, sPrivAuth, nExpireDate, nReadLimit, nPrintLimit);
//System.out.println("[JSTOR SOFTCAMP DEBUG] : SLDsAddUserDAC()");
			}
			else if("D".equalsIgnoreCase(sPrivType))
			{
				slFile.SLDsAddGroupDAC(sPrivMembers, sPrivAuth, nExpireDate, nReadLimit, nPrintLimit);
//System.out.println("[JSTOR SOFTCAMP DEBUG] : SLDsAddGroupDAC()");
			}
		}

		for (int i = 0; i < nNumOfFile; i++)
		{
			String sKeyFileDir = sEssentialMetaData[i][0];
			String sKeyFile    = sEssentialMetaData[i][1];

			String sSrcFilePath = sEssentialMetaData[i][2];
			String sDstFilePath = sSrcFilePath + "___SCSL_ENCODED___";
			String sSystemName = sAdditionalMetaData[i][0];

			// step 4) 암호화 지원 가능한 파일인지 확인
			nRet = slFile.DSIsSupportFile(sSrcFilePath);
//System.out.println("[JSTOR SOFTCAMP DEBUG] : DSIsSupportFile() - " + sSrcFilePath + "(" + nRet + ")");
			// 지원가능한 확장자, 
			// 서비스 링커에서 지원되는 확장자 목록 
			// ".doc", ".xls", ".ppt", ".csv", ".hwp", ".gul", ".txt", ".pdf", ".bmp", ".jpg", ".jpeg", ".gif", ".tif", ".tiff", ".rtf", ".jtd"
			if(1 == nRet) //암호화 지원 가능 파일인 경우   
			{
				// step 5) 암호화
				/* SLDsEncFileDACV2 메소드 인자 설명 
				 * keyFileName : 키파일 이름(경로 포함)
				 * systemName : 연동 시스템 이름 (생성자 정보에 연동시스템 이름이 들어감)
				 * srcPath : 원본 파일 이름 (경로 포함)
				 * destPath : 암호화한 파일 이름 (경로 포함)
				 * nOption : 원본파일이 암호화 파일일 경우 재 암호화 옵션 (0:암호화하지 않음 1:복호화 후 재 암호화)
				 */
				nRet = slFile.SLDsEncFileDACV2(sKeyFileDir + File.separator + sKeyFile, sSystemName, sSrcFilePath, sDstFilePath, 1);
//System.out.println("[JSTOR SOFTCAMP DEBUG] : SLDsEncFileDACV2()");
				// nRet = slFile.SLDsEncFileDAC(sKeyFileDir + File.separator + sKeyFile, sSrcFilePath, sDstFilePath, 1);
				nRet = (nRet > 0 ? nRet * -1 : nRet);
				if (0 == nRet || -36 == nRet) //암호화 성공 , 반환 데이터가 36인 경우에도 암호화가 성공한 것으로 간주(국민은행 요구사항 2009.03.03)
				{
					// step 6) 최종 Output 파일로 Rename
					String sOutFilePath = null;
					if (null == sEssentialMetaData[i][3])
					{
						// 지정 안되어 있을 경우, 원래 파일명(패스) 로 변경
						sOutFilePath = sSrcFilePath;
					}
					else
					{
						// 지정되어 있을 경우에는, 지정된 경로로 변경
						sOutFilePath = sEssentialMetaData[i][3];
					}

					// 먼저 지우고
					new File(sOutFilePath).delete();
					// 변경
					new File(sDstFilePath).renameTo(new File(sOutFilePath));

					sOutDrmFilePath[i] = sOutFilePath;		
				}else if(nRet == -81){//SLDsEncFileDACV2 메소드의 반환 값이 -81인 경우 아무 처리 해 주지 않고 넘어간다. (국민은행 요구사항 2009.03.03)
					// 암호화 시도 시 생성된 '파일명___SCSL_ENCODED___' 파일을 삭제 한다. 
					File f = new File(sDstFilePath);
					if(f.exists()){
						f.delete();
					}				
					
				}else{ //암호화 실패 
					System.out.println("ERROR(SoftCampPackager) : Cannot apply DRM to the File - " + sSrcFilePath);
					nRet = -5;
				}
			}
			else //암호화 지원 파일이 아닌 경우 
			{
				System.out.println("ERROR(SoftCampPackager) : Cannot support Enc File - " + sSrcFilePath);
				nRet = -9;
			}

			arrRet[i] = nRet;
			if (nRet < 0)
			{
				sRetErrMsg[i] = nRet + "";
			}
						
		}

      return arrRet;
   }
	  

   // 파일 복호화	 
   public boolean doSoftCampFileExtract(String filePath, String keyFilePath /* 키파일 이름(경로포함) */, String privID /* 권한을 구분 적용하는 개인/그룹(부서) ID */)
   {

//System.out.println("[JSTOR SOFTCAMP DEBUG] : doSoftCampFileExtarct() start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");	   
      boolean bRet = false;
	  int nRet = -1;
      String drmOutFile = filePath + "___SCSL_DECODED___";
	  SCSL.SLDsFile slFile = new SCSL.SLDsFile();
//System.out.println("[JSTOR SOFTCAMP DEBUG] : SLDsFile()");
	  if (null != m_strPathForProperty)
	  {
	  	  slFile.SettingPathForProperty(m_strPathForProperty);
//System.out.println("[JSTOR SOFTCAMP DEBUG] : SettingPathForProperty()");
	  }

      nRet = slFile.CreateDecryptFileDAC(keyFilePath, privID, filePath, drmOutFile);
//System.out.println("[JSTOR SOFTCAMP DEBUG] : CreateDecryptFileDAC() - " + filePath + "(" + nRet + ")");
	  if (1 == nRet)
	  {
	     System.out.println("FATAL ERROR(SoftCampPackager) : SOFTCAMP KEY Information Error");      
		 bRet = false;
	  }
	  else
	  { 
		  nRet = (nRet > 0 ? nRet * -1 : nRet);
		  if (-36 == nRet)   // no encrypted file
		  {
//System.out.println("[JSTOR SOFTCAMP DEBUG] : No Encrypted File - " + filePath);
			  bRet = true; // do nothing			
		  }
		  else if (nRet < 0)
		  {
			 System.out.println("ERROR(SoftCampPackager) : Cannot CreateDecryptFileDAC - " + filePath + ", ERRCODE - " + nRet);      		 
			 bRet = false;
		  }
		  else if (nRet == 0)
		  {
//System.out.println("[JSTOR SOFTCAMP DEBUG] : CreateDecryptFileDAC() SUCCESS - " + filePath);
			  bRet = true;
		  }

		  if (bRet)
		  {
			 bRet = new File(drmOutFile).renameTo(new File(filePath));
//System.out.println("[JSTOR SOFTCAMP DEBUG] : Rename Out Drm File(" + bRet + ")");
		  }
	  }

//System.out.println("[JSTOR SOFTCAMP DEBUG] : doSoftCampFileExtarct() end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
      return bRet;
   }
	  
}

