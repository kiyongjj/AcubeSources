/*
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * Author	          : Takkies
 * Date   	          : 2014. 04. 01.
 * Description 	  : 
 * </pre>
 */
package com.sds.acube.ndisc.xadmin;

import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.acube.ndisc.dao.config.DaoConfig;
import com.sds.acube.ndisc.dao.iface.VolumeDAO;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;

/**
 * XNDisc Admin 볼륨 정보 보여주기
 * 
 * @author Takkies
 *
 */
public class XNDiscAdminVolume extends XNDiscAdminBase {
	
	private VolumeDAO dao = null;

	public XNDiscAdminVolume(boolean printlog, PrintStream out, Logger log) {
		super(printlog, out, log);
		dao = (VolumeDAO) DaoConfig.getDaomanager().getDao(VolumeDAO.class);
	}

	/**
	 * 볼륨을 생성 하고 생성된 볼륨 정보 Console에 보여주기
	 * 
	 * @param name 볼륨명
	 * @param accessAuth 볼륨 권한
	 * @param desc 볼륨 설명
	 */
	public void makeVolume(String name, String accessAuth, String desc) {
		try {
			Volume volume = new Volume();
			volume.setName(name);
			volume.setAccessable(accessAuth);
			volume.setDesc(desc);
			volume.setCreatedDate(getCreateDate());
			storage.insertNewVolumeToDB(volume);
			StringBuilder volumes = new StringBuilder(LINE_SEPERATOR);
			volumes.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
			volumes.append("│").append(StringUtils.center("makeVolue Successfully", 100, " ")).append("│").append(LINE_SEPERATOR);
			volumes.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
			if (printlog) {
				log.info(volumes.toString());
			} else {
				out.print(volumes.toString());
			}					
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, "makeVolume() has been failed... " + ex.getMessage());
		}
	}

	/**
	 * 볼륨 아이디를 이용하여 볼륨 정보 Console에 출력하기
	 * 
	 * @param volumeId 볼륨 아이디
	 */
	public void selectVolumeById(int volumeId) {
		ArrayList<Volume> volumeList = null;
		try {
			volumeList = new ArrayList<Volume>();
			Volume volume = storage.selectVolumeInfo(volumeId);
			if (volume != null) {
				volumeList.add(volume);
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
		showVolumeInfo(volumeList);
	}

	/**
	 * 모든 볼륨정보 가져와 Console에 보여주기
	 */
	@SuppressWarnings("unchecked")
	public void selectVolumeList() {
		ArrayList<Volume> volumelist = null;
		try {
			volumelist = new ArrayList<Volume>();
			volumelist = (ArrayList<Volume>) storage.selectVolumeInfoList();
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
		showVolumeInfo(volumelist);
	}

	/**
	 * 볼륨 정보 Console에 출력하기
	 * 
	 * @param volumelist 볼륨 정보
	 */
	private void showVolumeInfo(ArrayList<Volume> volumelist) {
		int size = (volumelist == null) ? 0 : volumelist.size();
		Volume volume = null;
		StringBuilder volumes = new StringBuilder(LINE_SEPERATOR);
		volumes.append("┌").append(StringUtils.center("", 5, "-"));
		volumes.append("┬");
		volumes.append(StringUtils.center("", 121, "-"));
		volumes.append("┬");
		volumes.append(StringUtils.center("", 10, "-"));
		volumes.append("┬");
		volumes.append(StringUtils.center("", 14, "-"));
		volumes.append("┐").append(LINE_SEPERATOR);
		volumes.append("│").append(StringUtils.center("ID", 5, " "));
		volumes.append("│");
		volumes.append(StringUtils.center("Volume Name", 121, " "));
		volumes.append("│");
		volumes.append(StringUtils.center("Access", 10, " "));
		volumes.append("│");
		volumes.append(StringUtils.center("Create Date", 14, " "));
		volumes.append("│").append(LINE_SEPERATOR);
		
		if (size == 0) {
			int colidx = 0;
			volumes.append("├").append(StringUtils.center("", 5, "-"));
			volumes.append("┴");
			colidx++;
			volumes.append(StringUtils.center("", 121, "-"));
			volumes.append("┴");
			colidx++;
			volumes.append(StringUtils.center("", 10, "-"));
			volumes.append("┴");
			colidx++;
			volumes.append(StringUtils.center("", 14, "-"));
			volumes.append("┤").append(LINE_SEPERATOR);			
			volumes.append("│").append(StringUtils.center(" No Data Found.", PRINT_COLUMN_SIZE + colidx, " ")).append("│").append(LINE_SEPERATOR);
		} else {
			volumes.append("├").append(StringUtils.center("", 5, "-"));
			volumes.append("┼");
			volumes.append(StringUtils.center("", 121, "-"));
			volumes.append("┼");
			volumes.append(StringUtils.center("", 10, "-"));
			volumes.append("┼");
			volumes.append(StringUtils.center("", 14, "-"));
			volumes.append("┤").append(LINE_SEPERATOR);
		}
		
		for (int i = 0; i < size; i++) {
			volume = volumelist.get(i);
			volumes.append("│").append(StringUtils.center(Integer.toString(volume.getId()), 5, " "));
			volumes.append("│");
			volumes.append(StringUtils.rightPad(getName(volume.getName(), 121), 121, " "));
			volumes.append("│");
			volumes.append(StringUtils.center((StringUtils.isEmpty(volume.getAccessable()) ? "N/A" : volume.getAccessable()), 10, " "));
			volumes.append("│");
			volumes.append(StringUtils.center((StringUtils.isEmpty(volume.getCreatedDate()) ? "N/A" : volume.getCreatedDate()), 14, " "));
			volumes.append("│").append(LINE_SEPERATOR);
			if ((i < size - 1) && (i <= MAX_LIST_SIZE)) {
				volumes.append("├").append(StringUtils.center("", 5, "-"));
				volumes.append("┼");
				volumes.append(StringUtils.center("", 121, "-"));
				volumes.append("┼");
				volumes.append(StringUtils.center("", 10, "-"));
				volumes.append("┼");
				volumes.append(StringUtils.center("", 14, "-"));
				volumes.append("┤").append(LINE_SEPERATOR);
			}
			if (i > MAX_LIST_SIZE) {
				break;
			}
		}
		if (size == 0) {
			volumes.append("└").append(StringUtils.center("", 5, "-"));
			volumes.append("-");
			volumes.append(StringUtils.center("", 121, "-"));
			volumes.append("-");
			volumes.append(StringUtils.center("", 10, "-"));
			volumes.append("-");
			volumes.append(StringUtils.center("", 14, "-"));
			volumes.append("┘").append(LINE_SEPERATOR);
		} else {
		volumes.append("└").append(StringUtils.center("", 5, "-"));
		volumes.append("┴");
		volumes.append(StringUtils.center("", 121, "-"));
		volumes.append("┴");
		volumes.append(StringUtils.center("", 10, "-"));
		volumes.append("┴");
		volumes.append(StringUtils.center("", 14, "-"));
		volumes.append("┘").append(LINE_SEPERATOR);
		}
		volumes.append(size + " row selected.").append(LINE_SEPERATOR).append(LINE_SEPERATOR);
		if (printlog) {
			log.info(volumes.toString());
		} else {
			out.print(volumes.toString());
		}
	}

	/**
	 * 볼륨 아이디를 이용하여 볼륨을 삭제하고 삭제한 볼륨 정보를 Console에 보여주기
	 * 
	 * @param volumeId 볼륨 아이디
	 */
	public void removeVolume(int volumeId) {
		try {
			if (dao.deleteVolume(volumeId)) { // success
				StringBuilder volumes = new StringBuilder(LINE_SEPERATOR);
				volumes.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
				volumes.append("│").append(StringUtils.center("removeVolume Successfully : " + volumeId, 100, " ")).append("│").append(LINE_SEPERATOR);
				volumes.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(volumes.toString());
				} else {
					out.print(volumes.toString());
				}
			} else {
				logger.log(LoggerIF.LOG_ERROR, "removeVolume() failed");
			}
		} catch (Exception e) {
			logger.log(LoggerIF.LOG_ERROR, e.getMessage());
		}
	}

	/**
	 * 볼륨 정보 변경하고 변경된 볼륨 정보 Console 에 보여주기
	 * 
	 * @param id 볼륨 아이디
	 * @param name 볼륨 명
	 * @param accessAuth 볼륨 권한
	 * @param desc 볼륨 설명
	 */
	public void changeVolume(int id, String name, String accessAuth, String desc) {
		try {
			Volume volume = new Volume();
			volume.setId(id);
			volume.setName(name);
			volume.setAccessable(accessAuth);
			volume.setDesc(desc);
			if (dao.updateVolume(volume)) {
				StringBuilder volumes = new StringBuilder(LINE_SEPERATOR);
				volumes.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
				volumes.append("│").append(StringUtils.center("changeVolume Successfully : " + id, 100, " ")).append("│").append(LINE_SEPERATOR);
				volumes.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(volumes.toString());
				} else {
					out.print(volumes.toString());
				}				
			} else {
				logger.log(LoggerIF.LOG_ERROR, "changeVolume() failed..");
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
	}
}
