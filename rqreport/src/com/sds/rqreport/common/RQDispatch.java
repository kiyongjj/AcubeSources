package com.sds.rqreport.common;
import java.util.*;

public interface RQDispatch {
	int callByDispatch(int functionID, List argarray, List ret);
}
