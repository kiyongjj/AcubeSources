/*===================================================================================
 *
 * 작성자 : 조 국 영
 *
 * 클래스 이름 : FasooPackager.class
 *
 * 설명 : Fasoo DRM 적용을 위한 Packager 클래스
 *
 * 개발 시작일 : 2002/02/03
 *
 * 개발 종료일 : 0000/00/00
 *
 * 수정 내역 : 아래와 같음
 *
 * 버젼 : ver 1.0
 *
 * Copyright notice : Copyright (C) 2000 by SAMSUNG SDS co.,Ltd. All right reserved.
 *
 ===================================================================================*/

package jstornative;

import fsdjava.*;

public class FasooPackager {

	static boolean __JSTOR_FSD_DEBUG__ = false;

	static   
	{
		if ("true".equals(System.getProperty("drm.debug")))
			__JSTOR_FSD_DEBUG__ = true;
	}  
 
     
	public static void main (String args[])
	{
		System.out.println ("\n");
		System.out.println ("▶ Company :  SAMSUNG SDS");
		System.out.println ("▶ Product Name : Fasoo Packager API");
		System.out.println ("▶ Version : 1.3");
		System.out.println ("\n");
	}

	public synchronized int makePackage(int nNumOfFile, String[][] sEssentialMetaData, String[][] sAdditionalMetaData, String[] sRetErrMsg, String[] sOutDrmFilePath)
	{
		int nRet;
		String sRet;

		FasooPackagerJNI fspjni = new FasooPackagerJNI();

		for (int i = 0; i < nNumOfFile; i++)
		{
			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed ADK Home Dir [" + i + "] -> [" + sEssentialMetaData[i][0] + "]");

			// SET EssentialMetaData
			nRet = fspjni.SetHomeDir(sEssentialMetaData[i][0]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetHomeDir() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Server ID [" + i + "] -> [" + sEssentialMetaData[i][1] + "]");

			nRet = fspjni.SetServerID(sEssentialMetaData[i][1]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetServerID() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Source File Name[" + i + "] -> [" + sEssentialMetaData[i][2] + "]");

			nRet = fspjni.SetSourceFileName(sEssentialMetaData[i][2]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetSourceFileName() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Dest(Content) File Name[" + i + "] -> [" + sEssentialMetaData[i][3] + "]");

			if (null != sEssentialMetaData[i][3] && 0 != sEssentialMetaData[i][3].length())
			{
				nRet = fspjni.SetContainerFileName(sEssentialMetaData[i][3]);
				if (0 != nRet)
				{
					sRetErrMsg[0] = "SetContainerFileName() : " + fspjni.GetLastErrorStr();
					return nRet;
				}
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Provider ID [" + i + "] -> [" + sEssentialMetaData[i][4] + "]");

			nRet = fspjni.SetContentProviderID(sEssentialMetaData[i][4]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetContentProviderID() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Provider Name [" + i + "] -> [" + sEssentialMetaData[i][5] + "]");

			nRet = fspjni.SetContentProviderName(sEssentialMetaData[i][5]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetContentProviderName() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Provider Description [" + i + "] -> [" + sEssentialMetaData[i][6] + "]");

			nRet = fspjni.SetContentProviderDescription(sEssentialMetaData[i][6]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetContentProviderDescription() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Content Title [" + i + "] -> [" + sEssentialMetaData[i][7] + "]");

			nRet = fspjni.SetContentTitle(sEssentialMetaData[i][7]);
			if (0 != nRet)
			{
				sRetErrMsg[0] = "SetContentTitle() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			int nLen = sAdditionalMetaData[0].length;

			if (__JSTOR_FSD_DEBUG__)
				System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info Length [" + i + "] -> " + nLen);

			// Set AdditionalMetaData
			if (null != sAdditionalMetaData)
			{
				if (nLen > 0 && null != sAdditionalMetaData[i][0] && 0 != sAdditionalMetaData[i][0].length())
				{

					if (__JSTOR_FSD_DEBUG__)
						System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 1 [" + i + "] -> [" + sAdditionalMetaData[i][0] + "]");

					nRet = fspjni.SetAdditionalInfo1(sAdditionalMetaData[i][0]);
					if (0 != nRet)
					{
						sRetErrMsg[0] = "SetAdditionalInfo1() : " + fspjni.GetLastErrorStr();
						return nRet;
					}
				}

				if (nLen > 1 && null != sAdditionalMetaData[i][1] && 0 != sAdditionalMetaData[i][1].length())
				{
					if (__JSTOR_FSD_DEBUG__)
						System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 2 [" + i + "] -> [" + sAdditionalMetaData[i][1] + "]");

					nRet = fspjni.SetAdditionalInfo2(sAdditionalMetaData[i][1]);
					if (0 != nRet)
					{
						sRetErrMsg[0] = "SetAdditionalInfo2() : " + fspjni.GetLastErrorStr();
						return nRet;
					}
				}

				if (nLen > 2 && null != sAdditionalMetaData[i][2] && 0 != sAdditionalMetaData[i][2].length())
				{
					if (__JSTOR_FSD_DEBUG__)
						System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 3 [" + i + "] -> [" + sAdditionalMetaData[i][2] + "]");

					nRet = fspjni.SetAdditionalInfo3(sAdditionalMetaData[i][2]);
					if (0 != nRet)
					{
						sRetErrMsg[0] = "SetAdditionalInfo3() : " + fspjni.GetLastErrorStr();
						return nRet;
					}
				}

				if (nLen > 3 && null != sAdditionalMetaData[i][3] && 0 != sAdditionalMetaData[i][3].length())
				{
					if (__JSTOR_FSD_DEBUG__)
						System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 4 [" + i + "] -> [" + sAdditionalMetaData[i][3] + "]");

					nRet = fspjni.SetAdditionalInfo4(sAdditionalMetaData[i][3]);
					if (0 != nRet)
					{
						sRetErrMsg[0] = "SetAdditionalInfo4() : " + fspjni.GetLastErrorStr();
						return nRet;
					}
				}

				if (nLen > 4 && null != sAdditionalMetaData[i][4] && 0 != sAdditionalMetaData[i][4].length())
				{
					if (__JSTOR_FSD_DEBUG__)
						System.out.println ("[JSTOR_FSD_DEBUG] makePackage(), Inputed Additional Info 5 [" + i + "] -> [" + sAdditionalMetaData[i][4] + "]");

					nRet = fspjni.SetAdditionalInfo5(sAdditionalMetaData[i][4]);
					if (0 != nRet)
					{
						sRetErrMsg[0] = "SetAdditionalInfo5() : " + fspjni.GetLastErrorStr();
						return nRet;
					}
				}
			}

			nRet = fspjni.Initialize();
			if (0 != nRet)
			{
				sRetErrMsg[0] = "Initialize() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			nRet = fspjni.CreateSecureContainer();
			if (0 != nRet)
			{
				sRetErrMsg[0] = "CreateSecureContainer() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			sRet = fspjni.GetSecureContentID();
			if (null == sRet)
			{
				sRetErrMsg[0] = "GetSecureContentID() : " + fspjni.GetLastErrorStr();
				return nRet;
			}

			sOutDrmFilePath[i] = fspjni.GetSecureContainerFileName();

		}

		return 0;
	}



	public synchronized int[] makePackageEx(int nNumOfFile, String[][] sEssentialMetaData, String[][] sAdditionalMetaData, String[] sRetErrMsg, String[] sOutDrmFilePath)
	{

		if (__JSTOR_FSD_DEBUG__)
			System.out.println ("[JSTOR_FSD_DEBUG] makePackage() Entered, Requested nNumOfFile = " + nNumOfFile);

		int nRet[] = new int[nNumOfFile];
		String sRet;

		int nEssenCol = sEssentialMetaData[0].length;
		int nAddCol = sAdditionalMetaData[0].length;

		if (__JSTOR_FSD_DEBUG__)
			System.out.println ("[JSTOR_FSD_DEBUG] before FasooPackagerJNI Object create");

		FasooPackagerJNI fspjni = new FasooPackagerJNI();

		if (__JSTOR_FSD_DEBUG__)
			System.out.println ("[JSTOR_FSD_DEBUG] FasooPackagerJNI Object is created");

		for (int i = 0; i < nNumOfFile; i++)
		{
			String[] sErr = new String[1];
			String[] sOut = new String[1];
			String[][] sE = new String[1][nEssenCol];
			String[][] sA = new String[1][nAddCol];

			for (int j = 0; j < nEssenCol; j++)
			{
				sE[0][j] = sEssentialMetaData[i][j];
			}

			for (int j = 0; j < nAddCol; j++)
			{
				sA[0][j] = sAdditionalMetaData[i][j];
			}

			int iRet = makePackage (1, sE, sA, sErr, sOut);
			nRet[i] = (iRet > 0 ? iRet * (-1) : iRet);
			sRetErrMsg[i] = sErr[0];
			sOutDrmFilePath[i] = sOut[0];	
			
			sErr = null;
			sOut = null;
			sE = null;
			sA = null;
		}

		if (__JSTOR_FSD_DEBUG__)
			System.out.println ("[JSTOR_FSD_DEBUG] makePackage() will be returned");

		return nRet;
	}
}


