package com.sds.acube.jstor;

import java.io.File;
import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.napi.NApi;


public class NDiscNApi extends JSTORApi {

    static {   	
    	
        String strVersion = "NDisc NApi for JSTOR (2009-04-23)";
        System.setProperty("jstor_api_version", strVersion);
 
        logger = LogFactory.getLogger("jstorapi");
        logger.info("\n-------------------------------------------------- \n"
        		+ "▶ Company :  SAMSUNG SDS\n"
        		+ "▶ Product Name : JSTOR API for NDISC\n"
        		+ "▶ Version : " + strVersion        
        );
    }

    public NDiscNApi(boolean useCache) {
        System.setProperty("ndisc_napi_use_cache", useCache + "");
        m_useCache = useCache;
    } 

    private NApi m_napi = null;

    private boolean m_useCache = false;
 
    private int m_nErrCode;

    private String m_sErrMsg;

    private String[] m_sRegFileIDArr;

    private String[] m_sNewCpyFileIDArr;

    private static final int FILE_ERROR = 7000;

    private static final int NETWORK_ERROR = 7100;

    private static final int NDISC_ERROR = 7200;

    private void setError(int nErrCode, String sErrMsg) {
        m_nErrCode = nErrCode;

        if (null != sErrMsg) {
            m_sErrMsg = sErrMsg;
        } else {
            m_sErrMsg = null;
        }
    }

    public int JSTOR_getErrCode() {
        return m_nErrCode;
    }

    public String JSTOR_getErrMsg() {
        return m_sErrMsg;
    }

    public String[] JSTOR_getRegFileID() {
        for (int i = 0; i < m_sRegFileIDArr.length; i++) {
            if (null != m_sRegFileIDArr[i])
                m_sRegFileIDArr[i] = m_sRegFileIDArr[i].trim();
        }

        return m_sRegFileIDArr;
    }

    public String[] JSTOR_getNewCpyFileID() {
        for (int i = 0; i < m_sNewCpyFileIDArr.length; i++) {
            if (null != m_sNewCpyFileIDArr[i])
                m_sNewCpyFileIDArr[i] = m_sNewCpyFileIDArr[i].trim();
        }

        return m_sNewCpyFileIDArr;
    }

    public int JSTOR_Connect(String sIPAddr, int nPortNo) {
        int nConnID = -1;
        m_napi = new NApi(m_useCache);

        try {
            nConnID = m_napi.NDisc_Connect(sIPAddr, nPortNo);
            logger.info("JSTOR Connect / ConnID : " + nConnID);
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_Connect fail : " + e.getMessage());
        }

        return nConnID;
    }

    public void JSTOR_Disconnect(int nConnID) {
        try {
            m_napi.NDisc_Disconnect();
            logger.info("JSTOR Disconnect / ConnID : " + nConnID);
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_Disconnect fail : " + e.getMessage());
        }
    }

    /*
     * 파일 등록 
     * @see com.sds.acube.jstor.JSTORApi#JSTOR_FileReg(int, int, java.lang.String[][], int)
     */
    public int JSTOR_FileReg(int nConnID, int nNumOfFile, String[][] sInfoRegArr, int nOption) {
        int nRet = -1;
        NFile[] nFile = null;

        try {
            nFile = new NFile[nNumOfFile];

            for (int i = 0; i < nNumOfFile; i++) {
                nFile[i] = new NFile();
                nFile[i].setName(sInfoRegArr[i][0]); //파일 위치 정보
                nFile[i].setVolumeId(Integer.parseInt(sInfoRegArr[i][1])); //저장서버 볼륨 ID
                nFile[i].setStatType(sInfoRegArr[i][2]); //"0"
            }
            
            if(nOption == 10){ // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다. 
            	if(JSTOR_DecodeDRMFile(nFile) != true){// 복호화 실패 시
            		logger.error("JSTOR_DecodeDRMFile() fail");
            		return nRet;
            	}
            }            

            m_sRegFileIDArr = m_napi.NDISC_FileReg(nFile);
            logger.info("JSTOR_FileReg() success");
            nRet = 0;
        } catch (FileException e) {
            e.printStackTrace();
            setError(FILE_ERROR, e.getMessage());
            logger.error("JSTOR_FileReg fail : " + e.getMessage());
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_FileReg fail : " + e.getMessage());
        } catch (NDiscException e) {
            e.printStackTrace();
            setError(NDISC_ERROR, e.getMessage());
            logger.error("JSTOR_FileReg fail : " + e.getMessage());
        } finally {
            nFile = null;
        }

        return nRet;
    }
    
    
    /* 
     * DRM으로 암호화된 파일을 복호화 한다.
     * 현재는 Fasoo DRM 과 SoftCamp DRM 만 적용 되어 있다.
    */
    private boolean JSTOR_DecodeDRMFile(NFile[] nFile){
    	boolean ret = false;
    	try{
    		//Fasoo DRM 사용
    		if(m_drmType.equalsIgnoreCase("FASOO_V3.1")){
    			m_fasooPackager = new FasooPackager();
    			String fsdHomeDir = (String) m_drmConfig.get("FSD_HOME_DIR");
				String fsdServerID = (String) m_drmConfig.get("FSD_SERVER_ID");
				
				for(int i=0;i<nFile.length;i++){
					ret = m_fasooPackager.isFasooPackageFile(nFile[i].getName());
					if(ret){
						ret = m_fasooPackager.doFasooPacakgeFileExtract(nFile[i].getName(),fsdHomeDir,fsdServerID);
					}				
				}   		
    		//SoftCamp DRM 사용
    		}else if(m_drmType.startsWith("SOFTCAMP")){
    			m_softCampPackager = new SoftCampPackager();  			
    			String keyDir = (String) m_drmConfig.get("SCSL_KEY_DIR");
				String keyFile = (String) m_drmConfig.get("SCSL_KEY_FILE");
				String privID = (String) m_drmConfig.get("SCSL_PRIV_ID");
				
				if(m_drmType.equalsIgnoreCase("SOFTCAMP_V3.1")){
					m_softCampPackager.m_strPathForProperty = (String) m_drmConfig.get("SCSL_PROP_PATH");									
				}
				
				for(int i=0;i<nFile.length;i++){
					ret = m_softCampPackager.doSoftCampFileExtract(nFile[i].getName(), keyDir + File.separator + keyFile, privID);			
				}    			
    		}    		
    		
    	}catch(Exception e){    		
    		logger.error("JSTOR_DecodeDRMFile fail : " + e.getMessage());    		
    	}
    	
    	return ret;
    }
    

    public int JSTOR_FileGet(int nConnID, int nNumOfFile, String[][] sInfoGetArr, int nOption) {
        int nRet = -1;
        NFile[] nFile = null;

        try {
            nFile = new NFile[nNumOfFile];

            for (int i = 0; i < nNumOfFile; i++) {
                nFile[i] = new NFile();
                nFile[i].setId(sInfoGetArr[i][0]);
                nFile[i].setName(sInfoGetArr[i][1]);
                nFile[i].setStatType(sInfoGetArr[i][2]);
            }

            m_napi.NDISC_FileGet(nFile);
            logger.info("JSTOR_FileGet() sucess");
            nRet = 0;
        } catch (FileException e) {
            e.printStackTrace();
            setError(FILE_ERROR, e.getMessage());
            logger.error("JSTOR_FileGet fail : " + e.getMessage());
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_FileGet fail : " + e.getMessage());
        } catch (NDiscException e) {
            e.printStackTrace();
            setError(NDISC_ERROR, e.getMessage());
            logger.error("JSTOR_FileGet fail : " + e.getMessage());
        } finally {
            nFile = null;
        }

        return nRet;
    }

    public int JSTOR_FileRep(int nConnID, int nNumOfFile, String[][] sInfoRepArr, int nOption) {
        int nRet = -1;
        NFile[] nFile = null;

        try {
            nFile = new NFile[nNumOfFile];

            for (int i = 0; i < nNumOfFile; i++) {
                nFile[i] = new NFile();
                nFile[i].setId(sInfoRepArr[i][0]); // 저장서버 위치 정보 
                nFile[i].setName(sInfoRepArr[i][1]); // 파일 위치 정보
                nFile[i].setStatType(sInfoRepArr[i][2]); // 0 
            }
            
            if(nOption == 10){ // 옵션값이 "10" 일 경우 암호화된 파일을 복호화 한다. 
            	if(JSTOR_DecodeDRMFile(nFile) != true){// 복호화 실패 시
            		logger.error("JSTOR_DecodeDRMFile() fail");
            		return nRet;
            	}
            }      

            m_napi.NDISC_FileRep(nFile);
            logger.info("JSTOR_FileRep() success");
            nRet = 0;
        } catch (FileException e) {
            e.printStackTrace();
            setError(FILE_ERROR, e.getMessage());
            logger.error("JSTOR_FileRep fail : " + e.getMessage());
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_FileRep fail : " + e.getMessage());
        } catch (NDiscException e) {
            e.printStackTrace();
            setError(NDISC_ERROR, e.getMessage());
            logger.error("JSTOR_FileRep fail : " + e.getMessage());
        } finally {
            nFile = null;
        }

        return nRet;
    }

    public int JSTOR_FileDel(int nConnID, int nNumOfFile, String[] sInfoDelArr) {
        int nRet = -1;
        NFile[] nFile = null;

        try {
            nFile = new NFile[nNumOfFile];

            for (int i = 0; i < nNumOfFile; i++) {
                nFile[i] = new NFile();
                nFile[i].setId(sInfoDelArr[i]);
            }

            m_napi.NDISC_FileDel(nFile);
            logger.info("JSTOR_FileDel() success");
            nRet = 0;
        } catch (FileException e) {
            e.printStackTrace();
            setError(FILE_ERROR, e.getMessage());
            logger.error("JSTOR_FileDel fail : " + e.getMessage());
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_FileDel fail : " + e.getMessage());
        } catch (NDiscException e) {
            e.printStackTrace();
            setError(NDISC_ERROR, e.getMessage());
            logger.error("JSTOR_FileDel fail : " + e.getMessage());
        } finally {
            nFile = null;
        }

        return nRet;
    }

    public int JSTOR_FileCpy(int nConnID, int nNumOfFile, String[][] sInfoCpyArr) {
        int nRet = -1;
        NFile[] nFile = null;

        try {
            nFile = new NFile[nNumOfFile];

            for (int i = 0; i < nNumOfFile; i++) {
                nFile[i] = new NFile();
                nFile[i].setId(sInfoCpyArr[i][0]);
                nFile[i].setVolumeId(Integer.parseInt(sInfoCpyArr[i][1]));
                nFile[i].setStatType(sInfoCpyArr[i][2]);
            }

            m_sNewCpyFileIDArr = m_napi.NDISC_FileCpy(nFile);
            logger.info("JSTOR_FileCpy() success");
            nRet = 0;
        } catch (FileException e) {
            e.printStackTrace();
            setError(FILE_ERROR, e.getMessage());
            logger.error("JSTOR_FileCpy fail : " + e.getMessage());
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_FileCpy fail : " + e.getMessage());
        } catch (NDiscException e) {
            e.printStackTrace();
            setError(NDISC_ERROR, e.getMessage());
            logger.error("JSTOR_FileCpy fail : " + e.getMessage());
        } finally {
            nFile = null;
        }

        return nRet;
    }

    public int JSTOR_FileMov(int nConnID, int nNumOfFile, String[][] sInfoMovArr) {
        int nRet = -1;
        NFile[] nFile = null;

        try {
            nFile = new NFile[nNumOfFile];

            for (int i = 0; i < nNumOfFile; i++) {
                nFile[i] = new NFile();
                nFile[i].setId(sInfoMovArr[i][0]);
                nFile[i].setVolumeId(Integer.parseInt(sInfoMovArr[i][1]));
                nFile[i].setStatType(sInfoMovArr[i][2]);
            }

            m_napi.NDISC_FileMov(nFile);
            logger.info("JSTOR_FileMov() success");
            nRet = 0;
        } catch (FileException e) {
            e.printStackTrace();
            setError(FILE_ERROR, e.getMessage());
            logger.error("JSTOR_FileMov fail : " + e.getMessage());
        } catch (NetworkException e) {
            e.printStackTrace();
            setError(NETWORK_ERROR, e.getMessage());
            logger.error("JSTOR_FileMov fail : " + e.getMessage());
        } catch (NDiscException e) {
            e.printStackTrace();
            setError(NDISC_ERROR, e.getMessage());
            logger.error("JSTOR_FileMov fail : " + e.getMessage());
        } finally {
            nFile = null;
        }

        return nRet;
    }

    public int JSTOR_Commit(int nConnID) {
        // no means
        return 0;
    }

    public int JSTOR_Rollback(int nConnID) {
        // no means
        return 0;
    }
}
