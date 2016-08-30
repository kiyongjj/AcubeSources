/*
 * @(#) CacheUtil.java Feb 9, 2006
 * Copyright (c) 2006 Samsung SDS Co., Ltd. All Rights Reserved.
 */
package com.sds.acube.cache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author KangHun Song
 * @version $Revision: 1.1 $ $Date: 2009/12/22 05:32:16 $
 */
public class CacheUtil {

	/**
     * nio를 통한 파일복사 (JDK 1.4+ 필요)
     * @param args
     * @throws Exception 
     */
    public static void copyFile(String srcFile, String destFile) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);

            // get FileChannel
            FileChannel fcin = fis.getChannel();
            FileChannel fcout = fos.getChannel();

         // 복사될 파일의 size
            long FileSize = fcin.size();

            // transferTo 메소드를 이용하여 복사할 파일의 채널을 지정
            // 복사할 position은 0부터 파일 크기 만큼
            fcin.transferTo(0, FileSize, fcout);

            fcout.close();
            fcin.close();
            fos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
