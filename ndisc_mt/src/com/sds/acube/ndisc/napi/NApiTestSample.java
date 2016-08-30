package com.sds.acube.ndisc.napi;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.mts.common.NDConstant;
import com.sds.acube.ndisc.mts.util.timer.TimeChecker;

/**
 * @author ky.cho TEST FOR NDISC CLIENT
 */
public class NApiTestSample {
	public static void main(String[] args) {
		NApiTestSample scc = new NApiTestSample();

		if ("filereg".equalsIgnoreCase(args[0])) {
			scc.regFile(args);
		} else if ("fileregex".equalsIgnoreCase(args[0])) {
			scc.regFileEx(args);
		} else if ("fileget".equalsIgnoreCase(args[0])) {
			scc.getFile(args);
		} else if ("filegetex".equalsIgnoreCase(args[0])) {
			scc.getFileEx(args);
		} else if ("filerep".equalsIgnoreCase(args[0])) {
			scc.repFile(args); 
		} else if ("filerepex".equalsIgnoreCase(args[0])) {
			scc.repFileEx(args);
		} else if ("filedel".equalsIgnoreCase(args[0])) {
			scc.delFile(args);
		} else if ("filedelex".equalsIgnoreCase(args[0])) {
			scc.delFileEx(args);
		} else if ("filecpy".equalsIgnoreCase(args[0])) {
			scc.cpyFile(args);
		} else if ("filecpyex".equalsIgnoreCase(args[0])) {
			scc.cpyFileEx(args);
		} else if ("filemov".equalsIgnoreCase(args[0])) {
			scc.movFile(args);
		} else if ("fileinfo".equalsIgnoreCase(args[0])) {
			scc.fileInfo(args);
		} else {
			;
		}
	}

	private void regFile(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		String[] fileID = null;
		int connID = -1;

		nFile[0] = new NFile();
		// nFile[0].setName("./data/test.ppt");
		nFile[0].setName(args[3]);
		nFile[0].setVolumeId(Integer.parseInt(args[4]));
		// nFile[0].setStatType(args[5]);
		nFile[0].setStatType(NDConstant.STAT_NONE);
		// nFile[0].setStatType("9");

		NApi napi = new NApi(false);

		try {
			TimeChecker.setStartPoint();
			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			try {
				fileID = napi.NDISC_FileReg(nFile);
			} catch (Exception ee) {
				System.out.println(ee.getMessage());
			}

			if (null == fileID) {
				System.out.println("FileReg : Fail");
			} else {
				for (int j = 0; j < fileID.length; j++) {
					System.out.println("Return ID [" + j + "] = " + fileID[j]);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				napi.NDisc_Disconnect();
				System.out.println(TimeChecker.getCurrentInterval());
			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void regFileEx(String[] args) {
		String HOST = "localhost";
		int PORT = 7404;
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		String[] arrRet = null;

		nFile[0] = new NFile();
		nFile[0].setName("/data2/dmsjava/david/NDISC_RUN/data2/test1.txt");
		nFile[0].setVolumeId(101);
		nFile[0].setStatType(NDConstant.STAT_ENC);

		NApi napi = new NApi(HOST, PORT);

		try {
			arrRet = napi.NDISC_FileRegEx(nFile);
		} catch (FileException e) {
			System.out.println(e.getMessage());
			arrRet = null;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			arrRet = null;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			arrRet = null;
		}

		if (null == arrRet) {
			System.out.println("Result : Fail");
		} else {
			for (int i = 0; i < arrRet.length; i++) {
				System.out.println("Return ID [" + i + "] = " + arrRet[i]);
			}
		}
	}

	private void getFile(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;
		int connID = -1;

		nFile[0] = new NFile();
		// nFile[0].setId("f47e4e77f77f078a377dec441bf076bb");
		nFile[0].setId(args[3]);
		nFile[0].setStatType(NDConstant.STAT_NONE);
		nFile[0].setName(args[4]);

		NApi napi = new NApi(false);
		try {

			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			TimeChecker.setStartPoint();
			bRet = napi.NDISC_FileGet(nFile);
			System.out.println(TimeChecker.getCurrentInterval());
			// TimeChecker.setStartPoint();
			// bRet = napi.NDISC_FileGet(nFile);
			// System.out.println(TimeChecker.getCurrentInterval());

		} catch (FileException e) {
			System.out.println(e.getMessage());
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				napi.NDisc_Disconnect();

			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.println("Result : " + bRet);
	}

	private void getFileEx(String[] args) {
		String HOST = "localhost";
		int PORT = 7404;
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;

		nFile[0] = new NFile();
		// nFile[0].setId("f47e4e77f77f078a377dec441bf076bb");
		nFile[0].setId("6607ba05c1f8e824f8b457ffd706d24a");
		nFile[0].setStatType(NDConstant.STAT_AUTO);
		nFile[0].setName("./data2/test_get.txt");

		NApi napi = new NApi(HOST, PORT);

		TimeChecker.setStartPoint();

		try {
			bRet = napi.NDISC_FileGetEx(nFile);
		} catch (FileException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			bRet = false;
		}

		System.out.println("ELAPSE : " + TimeChecker.getCurrentInterval());
		System.out.println("Result : " + bRet);
	}

	private void repFile(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;
		int connID = -1;

		nFile[0] = new NFile();
		nFile[0].setName(args[4]);
		nFile[0].setStatType(NDConstant.STAT_COMP);
		nFile[0].setId(args[3]);

		NApi napi = new NApi();

		try {
			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			for (int i = 0; i < 1; i++) {
				if (1 == i) {
					//nFile[0].setId("d89c574fd1792db45aa643267e019e2c");
				}
				try {
					bRet = napi.NDISC_FileRep(nFile);

					System.out.println("Result : " + bRet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				napi.NDisc_Disconnect();
			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void repFileEx(String[] args) {
		String HOST = "localhost";
		int PORT = 7404;
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;

		nFile[0] = new NFile();
		nFile[0].setName("./data2/test2.txt");
		nFile[0].setStatType(NDConstant.STAT_NONE);
		nFile[0].setId("a01cdd17e25c0989bfec448a8d652614");

		NApi napi = new NApi(HOST, PORT);

		try {
			bRet = napi.NDISC_FileRepEx(nFile);
		} catch (FileException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			bRet = false;
		}

		System.out.println("Result : " + bRet);
	}

	private void delFile(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;
		int connID = -1;

		nFile[0] = new NFile();
		nFile[0].setId(args[3]);

		NApi napi = new NApi();

		try {
			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			bRet = napi.NDISC_FileDel(nFile);
		} catch (FileException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} finally {
			try {
				napi.NDisc_Disconnect();
			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.println("Result = " + bRet);
	}

	private void delFileEx(String[] args) {
		String HOST = "localhost";
		int PORT = 7404;
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;

		nFile[0] = new NFile();
		nFile[0].setId("22965c3db265afd7260c62a88db7cee8");

		NApi napi = new NApi(HOST, PORT);

		try {
			bRet = napi.NDISC_FileDelEx(nFile);
		} catch (FileException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			bRet = false;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			bRet = false;
		}

		System.out.println("Result = " + bRet);
	}

	private void cpyFile(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		String[] arrRet = null;
		int connID = -1;

		nFile[0] = new NFile();
		nFile[0].setId(args[3]);
		nFile[0].setVolumeId(Integer.parseInt(args[4]));
		nFile[0].setStatType(NDConstant.STAT_NONE);

		NApi napi = new NApi();

		try {
			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			arrRet = napi.NDISC_FileCpy(nFile);
			if (null == arrRet) {
				System.out.println("FileRep : Fail");
			} else {
				for (int i = 0; i < arrRet.length; i++) {
					System.out.println("Return ID [" + i + "] = " + arrRet[i]);
				}
			}
		} catch (FileException e) {
			System.out.println(e.getMessage());
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				napi.NDisc_Disconnect();
			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void cpyFileEx(String[] args) {
		String HOST = "localhost";
		int PORT = 7404;
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		String[] arrRet = null;

		nFile[0] = new NFile();
		nFile[0].setId("00ab789950add9a788d62444d598899c");
		nFile[0].setVolumeId(101);
		nFile[0].setStatType(NDConstant.STAT_ENC);

		NApi napi = new NApi(HOST, PORT);

		try {
			arrRet = napi.NDISC_FileCpyEx(nFile);
		} catch (FileException e) {
			System.out.println(e.getMessage());
			arrRet = null;
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
			arrRet = null;
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
			arrRet = null;
		}

		if (null == arrRet) {
			System.out.println("Result : Fail");
		} else {
			for (int i = 0; i < arrRet.length; i++) {
				System.out.println("Return ID [" + i + "] = " + arrRet[i]);
			}
		}
	}

	private void movFile(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		boolean bRet = false;
		int connID = -1;

		nFile[0] = new NFile();
		nFile[0].setId(args[3]);
		nFile[0].setVolumeId(Integer.parseInt(args[4]));
		nFile[0].setStatType(NDConstant.STAT_NONE);

		NApi napi = new NApi();

		try {
			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			bRet = napi.NDISC_FileMov(nFile);

		} catch (FileException e) {
			System.out.println(e.getMessage());
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				napi.NDisc_Disconnect();
			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.println("Result = " + bRet);
	}

	private void fileInfo(String[] args) {
		String HOST = args[1];
		int PORT = Integer.parseInt(args[2]);
		int nNumOfFiles = 1;
		NFile[] nFile = new NFile[nNumOfFiles];
		int connID = -1;

		nFile[0] = new NFile();
		nFile[0].setId(args[3]);
		//nFile[1] = new NFile();
		//nFile[1].setId("942d1058258113fe7f9f9f9c6b631f86");

		NApi napi = new NApi();

		try {
			connID = napi.NDisc_Connect(HOST, PORT);
			System.out.println("Connected - ID = " + connID);

			nFile = napi.NDISC_FileInfo(nFile);

			for (int i = 0; i < nFile.length; i++) {
				System.out.println((i + 1)
						+ " th file info : -------------------------");
				System.out.println(nFile[i].getId());
				System.out.println(nFile[i].getName());
				System.out.println(nFile[i].getSize());
				System.out.println(nFile[i].getCreatedDate());
				System.out.println(nFile[i].getModifiedDate());
				System.out.println(nFile[i].getMediaId());
				System.out.println(nFile[i].getStatType());
				System.out
						.println("------------------------------------------");
			}

		} catch (FileException e) {
			System.out.println(e.getMessage());
		} catch (NetworkException e) {
			System.out.println(e.getMessage());
		} catch (NDiscException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				napi.NDisc_Disconnect();
			} catch (NetworkException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
