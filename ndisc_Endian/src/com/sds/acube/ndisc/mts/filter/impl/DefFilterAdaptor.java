package com.sds.acube.ndisc.mts.filter.impl;

import com.sds.acube.ndisc.mts.filter.iface.FilterIF;

public abstract class DefFilterAdaptor implements FilterIF {
   public abstract void filterFileForward(String filePath) throws Exception;
   public abstract void filterFileReverse(String filePath) throws Exception;
}
