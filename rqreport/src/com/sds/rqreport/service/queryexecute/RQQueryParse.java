package com.sds.rqreport.service.queryexecute;

import java.io.IOException;
import java.io.StringReader;
import java.io.*;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class RQQueryParse {
	
	/**
	 * 문서에서 받은 String(xml structure)을 파싱한다. 
	 * @param strXml - 파싱할 String
	 * @return 파싱된 Document
	 * @throws JDOMException
	 * @throws IOException
	 */
	//string source
    public Document parseXML(String strXml) throws JDOMException, IOException {
        
        StringReader lm_stRSos = new StringReader(strXml);
        InputSource source = new InputSource(lm_stRSos);  

        SAXBuilder builder = new SAXBuilder();
        return builder.build(source);
    }
    
    /**
     * 파일(xml structure) 파싱 작업을 거쳐 Document 형태로 반환한다. 
     * @param file - 파싱할 파일
     * @return 파싱된 Document
     * @throws JDOMException
     * @throws IOException
     */
    //file source
    public Document parseXML(File file) throws JDOMException, IOException {

    	FileInputStream inputStream = new FileInputStream(file);
    	//InputStream inputStream = pageContext.getServletContext().getResourceAsStream("/WEB-INF/xml/sample.xml");
    	InputSource source = new InputSource(inputStream);   

        SAXBuilder builder = new SAXBuilder();
    	return builder.build(source);
    }

}
