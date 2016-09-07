package com.sds.rqreport.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import com.sds.rqreport.util.Base64Decoder;


public class RepositoryClientRequester extends AbstractClientRequester {

	int parameter;
	String types = "";
	public RepositoryClientRequester() {
		super();
	}

	public RepositoryClientRequester(InputStream in, OutputStream out) {
		super(in, out);
	}

	public RepositoryClientRequester(SocketConnector sc) {
		super(sc);
	}

	public RepositoryClientRequester(String server, int port) {
		super(server, port);
	}

	public String makeSendString(int id, List args) {
		StringBuffer sb = new StringBuffer("\nENC=\t\nUTF-8\n");
		sb.append("\nType=\t\n" +id +  "\t\n");
		int argsize = args.size();
		for(int i = 0; i < argsize; ++i)
		{
			Object arg = args.get(i);
			if(arg instanceof String)
			{
				addParam(sb,(String)arg);
			}
			else if(arg instanceof Integer)
			{
				addParam(sb,((Integer)arg).intValue());
			}
			else if(arg instanceof Calendar)
			{
				addParam(sb,(Calendar)arg);
			}
			else if(arg instanceof Float)
			{
				addParam(sb,((Float)arg).floatValue());
			}

		}


		return sb.toString();
	}

	private StringBuffer addParam(StringBuffer sb, String str)
	{
		if(sb != null)
		{
			sb.append("\nParam"+ parameter++ +"=\t\n");
			str.replaceAll("\\","\\\\");
			str.replaceAll("\n","\\n");
			str.replaceAll("\t","\\t");
			sb.append(str);
			sb.append("\n");
			types += "S";

		}
		return sb;
	}
	private StringBuffer addParam(StringBuffer sb, int val)
	{
		if(sb != null)
		{

			sb.append("\nParam"+ parameter++ +"=\t\n");
			sb.append(val);
			sb.append("\n");
			types += "I";
		}
		return sb;
	}
	private StringBuffer addParam(StringBuffer sb, float val)
	{
		if(sb != null)
		{

			sb.append("\nParam"+ parameter++ +"=\t\n");
			sb.append(val);
			sb.append("\n");
			types += "F";
		}
		return sb;
	}
	private StringBuffer addParam(StringBuffer sb, Calendar cal)
	{
		if(sb != null)
		{

		}
		return sb;
	}
	public short parseReceiveString(String str, List rets) {

		int ret;
		ret = getResultCode(str);
		if(ret < 0) // if failed
		{
			rets.add(getError(str));
		}
		else // if succeeded
		{
			String types = getType(str);
			int len = types.length();
			for(int i=0; i < len;++i)
			{
				char type = str.charAt(i);
				switch(type)
				{
				case 'I':
					rets.add(new Integer(getParamInt(str, i + 1)));
					break;
				case 'S':
					rets.add(getParamString(str, i + 1));
					break;
				case 'D':
					break;
				case 'A':
					rets.add(getArray(str,i + 1));
				case 'B':
					rets.add(getByteData(str,i + 1));
				default:
					rets.add(null);
				}
			}
		}
		return 0;
	}

	private String getParamString(String str, int idx)
	{
		String find = "\nParam" + idx + "=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			return str.substring(pos + n, pos2);
		}
		return "";
	}

	private int getParamInt(String str, int idx)
	{
		String find = "\nParam" + idx + "=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			return Integer.parseInt(str.substring(pos + n, pos2));
		}
		return 0;
	}

	private String getError(String str)
	{
		String find = "\nERR=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			return str.substring(pos + n, pos2);
		}
		return "";
	}

	private String getType(String str)
	{
		String find = "\nTYPE=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			return str.substring(pos + n, pos2);
		}
		return "";
	}

	private byte[] getByteData(String str, int idx)
	{
		String find = "\nParam" + idx + "=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			return Base64Decoder.decodeToBytes(str.substring(pos + n, pos2));
		}

		return null;
	}

	private int getResultCode(String str)
	{
		String find = "\nERR=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			return Integer.parseInt(str.substring(pos + n, pos2));
		}
		return -1;
	}
	private List getArray(String str,int idx)
	{
		String find = "\nParam" + idx + "=\t\n";
		int pos = str.indexOf(find);
		int n = find.length();
		int pos2 = find.indexOf("\n", pos + n);
		if(pos > -1 && pos2 - pos - n > -1)
		{
			ArrayList arr = null;
			String param = str.substring(pos + n, pos2);
			String[] valArray = param.split("\t");
			int stringarrayidx = 0;
			if(valArray != null && valArray.length > 0)
			{
				String[] sizeArray = valArray[0].split(",");
				int nDim = sizeArray.length;
				if(nDim == 2)
				{
					int rownum = Integer.parseInt(sizeArray[1]);
					arr = new ArrayList(rownum);
					for(int i = 0; i< rownum; ++i)
					{
						List obj = new ArrayList();
						switch(valArray[stringarrayidx].charAt(0))//
						{
						case 'S':
							stringarrayidx++;
							str = valArray[stringarrayidx];
							obj.add(convertString(str));
							break;
						case 'I':
							stringarrayidx++;
							str = valArray[stringarrayidx];
							obj.add(new Integer(Integer.parseInt(str)));

							break;
						case 'F':
							stringarrayidx++;
							str = valArray[stringarrayidx];
							obj.add(new Float(Float.parseFloat(str)));
							break;
						case 'D':
							stringarrayidx++;
							str = valArray[stringarrayidx];
							obj.add(null);
							break;
						case 'A':
							//To do: Array
							stringarrayidx++;
							obj.add(null);
							break;
						}

						arr.add(obj);
					}

				}
				else if(nDim == 1)
				{

				}

			}
			return arr;
		}
		else
			return null;
	}

	String convertString(String str)
	{
		return str;
	}
}
