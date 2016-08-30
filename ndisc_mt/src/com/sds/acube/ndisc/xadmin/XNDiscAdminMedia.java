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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.dao.config.DaoConfig;
import com.sds.acube.ndisc.dao.iface.MediaDAO;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.mts.logger.iface.LoggerIF;

/**
 * 미디어 정보 보여주기
 * 
 * @author Takkies
 *
 */
public class XNDiscAdminMedia extends XNDiscAdminBase {

	private MediaDAO dao = null;

	public XNDiscAdminMedia(boolean printlog, PrintStream out, Logger log) {
		super(printlog, out, log);
		dao = (MediaDAO) DaoConfig.getDaomanager().getDao(MediaDAO.class);
	}

	/**
	 * 미디어 생성하고 생성한 미디어 정보 Console에 보여주기
	 * 
	 * @param host HOST 정보
	 * @param port PORT 정보
	 * @param name 미디어 명
	 * @param type 미디어 타입
	 * @param path 미디어 경로
	 * @param desc 미디어 설명
	 * @param maxSize 미디어 최대 사이즈
	 * @param volumeId 미디어 볼륨 아이디
	 */
	public void makeMedia(String host, int port, String name, int type, String path, String desc, int maxSize, int volumeId) {
		try {
			Media media = new Media();
			media.setName(name);
			media.setType(type);
			media.setPath(path);
			media.setDesc(desc);
			media.setMaxSize(maxSize);
			media.setVolumeId(volumeId);
			media.setCreatedDate(getCreateDate());

			xnapi.XNDisc_Connect(host, port);
			boolean rtn = xnapi.XNDISC_MakeMedia(media);
			if (rtn) {
				StringBuilder medias = new StringBuilder(LINE_SEPERATOR);
				medias.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
				medias.append("│").append(StringUtils.center("makeMedia Successfully", 100, " ")).append("│").append(LINE_SEPERATOR);
				medias.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
				if (printlog) {
					log.info(medias.toString());
				} else {
					out.print(medias.toString());
				}
			} else {
				logger.log(LoggerIF.LOG_ERROR, "mkmedia() failed !!!!");
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		} finally {
			try {
				xnapi.XNDisc_Disconnect();
			} catch (NetworkException ne) {
				logger.log(LoggerIF.LOG_ERROR, ne.getMessage());
			} catch (IOException e) {
				logger.log(LoggerIF.LOG_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * 미디어 아이디를 이용한 미디어 정보 Console에 보여주기
	 * 
	 * @param mediaId 미디어 아이디
	 */
	public void selectMediaById(int mediaId) {
		ArrayList<Media> mediaList = null;
		try {
			mediaList = new ArrayList<Media>();
			Media media = storage.selectMediaInfo(mediaId);
			if (media != null) {
				mediaList.add(media);
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
		showMediaInfo(mediaList);
	}

	/**
	 * 모든 미디어 정보 가져와 Console에 보여주기
	 */
	@SuppressWarnings("unchecked")
	public void selectMediaList() {
		ArrayList<Media> mediaList = null;
		try {
			mediaList = (ArrayList<Media>) storage.selectMediaInfoList();
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
		showMediaInfo(mediaList);
	}

	/**
	 * 미디어 정보 Console에 보여주기
	 * 
	 * @param mediaList 미디어 정보
	 */
	private void showMediaInfo(ArrayList<Media> mediaList) {
		int size = (mediaList == null) ? 0 : mediaList.size();
		Media media = null;
		StringBuilder medias = new StringBuilder(LINE_SEPERATOR);
		medias.append("┌").append(StringUtils.center("", 5, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 48, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 5, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 49, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 14, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 12, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 12, "-"));
		medias.append("┬");
		medias.append(StringUtils.center("", 5, "-"));
		medias.append("┐").append(LINE_SEPERATOR);

		medias.append("│").append(StringUtils.center("ID", 5, " "));
		medias.append("│");
		medias.append(StringUtils.center("Media Name", 48, " "));
		medias.append("│");
		medias.append(StringUtils.center("Type", 5, " "));
		medias.append("│");
		medias.append(StringUtils.center("Path", 49, " "));
		medias.append("│");
		medias.append(StringUtils.center("Create Date", 14, " "));
		medias.append("│");
		medias.append(StringUtils.center("Max Size", 12, " "));
		medias.append("│");
		medias.append(StringUtils.center("Size", 12, " "));
		medias.append("│");
		medias.append(StringUtils.center("Vol", 5, " "));
		medias.append("│").append(LINE_SEPERATOR);

		if (size == 0) {
			int colidx = 0;
			medias.append("├").append(StringUtils.center("", 5, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 48, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 49, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 14, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("┴");
			colidx++;
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┤").append(LINE_SEPERATOR);
			medias.append("│").append(StringUtils.center(" No Data Found.", PRINT_COLUMN_SIZE + colidx, " ")).append("│").append(LINE_SEPERATOR);
		} else {
			medias.append("├").append(StringUtils.center("", 5, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 48, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 49, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 14, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("┼");
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┤").append(LINE_SEPERATOR);
		}

		for (int i = 0; i < size; i++) {
			media = (Media) mediaList.get(i);
			medias.append("│").append(StringUtils.center(Integer.toString(media.getId()), 5, " "));
			medias.append("│");
			medias.append(StringUtils.rightPad(getName(media.getName(), 48), 48, " "));
			medias.append("│");
			medias.append(StringUtils.center(Integer.toString(media.getType()), 5, " "));
			medias.append("│");
			medias.append(StringUtils.rightPad(getName(media.getPath(), 49), 49, " "));
			medias.append("│");
			medias.append(StringUtils.center((StringUtils.isEmpty(media.getCreatedDate()) ? "N/A" : media.getCreatedDate()), 14, " "));
			medias.append("│");
			medias.append(StringUtils.center(Long.toString(media.getMaxSize()), 12, " "));
			medias.append("│");
			medias.append(StringUtils.center(Long.toString(media.getSize()), 12, " "));
			medias.append("│");
			medias.append(StringUtils.center(Integer.toString(media.getVolumeId()), 5, " "));
			medias.append("│").append(LINE_SEPERATOR);
			if ((i < size - 1) && (i <= MAX_LIST_SIZE)) {
				medias.append("├").append(StringUtils.center("", 5, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 48, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 5, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 49, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 14, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 12, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 12, "-"));
				medias.append("┼");
				medias.append(StringUtils.center("", 5, "-"));
				medias.append("┤").append(LINE_SEPERATOR);
			}
			if (i > MAX_LIST_SIZE) {
				break;
			}
		}
		if (size == 0) {
			medias.append("└").append(StringUtils.center("", 5, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 48, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 49, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 14, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("-");
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┘").append(LINE_SEPERATOR);
		} else {
			medias.append("└").append(StringUtils.center("", 5, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 48, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 49, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 14, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 12, "-"));
			medias.append("┴");
			medias.append(StringUtils.center("", 5, "-"));
			medias.append("┘").append(LINE_SEPERATOR);
		}
		medias.append(size + " row selected.").append(LINE_SEPERATOR).append(LINE_SEPERATOR);
		if (printlog) {
			log.info(medias.toString());
		} else {
			out.print(medias.toString());
		}
	}

	/**
	 * 미디어 정보 변경하고 변경 결과 Console에 보여주기
	 * 
	 * @param id 미디어 아이디
	 * @param name 미디어 명
	 * @param type 미디어 타입
	 * @param path 미디어 경로
	 * @param desc 미디어 설명
	 * @param maxSize 미디어 최대 사이즈
	 * @param volumeId 미디어 볼륨 아이디
	 */
	public void changeMedia(int id, String name, int type, String path, String desc, int maxSize, int volumeId) {
		try {
			Media media = new Media();
			media.setId(id);
			media.setName(name);
			media.setType(type);
			media.setPath(path);
			media.setDesc(desc);
			media.setMaxSize(maxSize);
			media.setVolumeId(volumeId);
			dao.updateMedia(media);
			StringBuilder medias = new StringBuilder(LINE_SEPERATOR);
			medias.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
			medias.append("│").append(StringUtils.center("changeMedia Successfully", 100, " ")).append("│").append(LINE_SEPERATOR);
			medias.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
			if (printlog) {
				log.info(medias.toString());
			} else {
				out.print(medias.toString());
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
	}

	/**
	 * 미디어 삭제하고 삭제 결과 보여주기
	 * 
	 * @param id 미디어 아이디
	 */
	public void removeMedia(int id) {
		try {
			dao.deleteMedia(id);
			StringBuilder medias = new StringBuilder(LINE_SEPERATOR);
			medias.append("┌").append(StringUtils.rightPad("", 100, "-")).append("┐").append(LINE_SEPERATOR);
			medias.append("│").append(StringUtils.center("removeMedia Successfully", 100, " ")).append("│").append(LINE_SEPERATOR);
			medias.append("└").append(StringUtils.rightPad("", 100, "-")).append("┘").append(LINE_SEPERATOR);
			if (printlog) {
				log.info(medias.toString());
			} else {
				out.print(medias.toString());
			}
		} catch (Exception ex) {
			logger.log(LoggerIF.LOG_ERROR, ex.getMessage());
		}
	}
}
