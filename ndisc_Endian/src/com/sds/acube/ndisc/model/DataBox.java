package com.sds.acube.ndisc.model;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author machinelee
 * @version $Revision: 1.1 $ Last Revised $Date: 2009/12/22 05:32:16 $
 */
public class DataBox extends HashMap {

    private ArrayList metaData = new ArrayList();
    protected String name = null;
    private static final long serialVersionUID = 1L;

    public DataBox() {
    }
    
    public DataBox(String name) {
        super();
        this.name = name;
    }

    
    /**
     * 전달받은 key값과 value를 설정한다.
     * 
     * @param key String
     * @param value String
     */
    public void put(String key, String value) {
        super.put(key, value);
    }
    
    /**
     * 전달받은 key값과 value를 설정한다.
     * 
     * @param key String
     * @param value int
     */
    public void put(String key, int value) {
        metaData.add(key);
        super.put(key, new Integer(value));
    }
    
    /**
     * 전달받은 key값과 value를 설정한다.
     * 
     * @param key String
     * @param value int
     */
    public void put(String key, boolean value) {
        metaData.add(key);
        super.put(key, new Boolean(value));
    }
    
    /**
     * 전달받은 key값과 value를 설정한다.
     * 
     * @param key String
     * @param value Object
     */
    public void put(String key, Object value) {
        metaData.add(key);
        super.put(key, value);
    }

    /**
     * 전달받은 key값과 value를 설정한다.
     * 
     * @param key String
     * @param value Object
     */
    public Object put(Object key, Object value) {
        metaData.add(key);
        return super.put(key, value);
    }

    /**
     * 전달받은 Vector의 key값과 Vector를 설정한다.
     * 
     * @param key java.lang.String
     * @param vector java.util.Vector
     */
    public void putVector(String key, Vector vector) {
        String value[] = new String[vector.size()];
        for (int idx = 0; idx < vector.size(); idx++)
            value[idx] = (vector.get(idx) == null) ? "" : vector.get(idx).toString();
        put(key, value);
    }

    /**
     * 전달받은 ArrayList의 key값과 ArrayList를 설정한다.
     * 
     * @param key java.lang.String
     * @param vector java.util.ArrayList
     */
    public void putArrayList(String key, ArrayList arrayList) {
        String value[] = new String[arrayList.size()];
        for (int idx = 0; idx < arrayList.size(); idx++)
            value[idx] = (arrayList.get(idx) == null) ? "" : arrayList.get(idx).toString();
        put(key, value);
    }
    
    /**
     * SSBox 객체의 복사한 새로운 SSBox객체를 생성하여 return한다.
     * 
     * @return SSBox Instance
     */
    public Object clone() {
        DataBox newbox = new DataBox(name);
        HashMap src = (HashMap) this;
        HashMap target = (HashMap) newbox;

        for (Iterator e = src.keySet().iterator(); e.hasNext();) {
            String key = (String) e.next();
            Object value = src.get(key);
            target.put(key, value);
        }
        return newbox;
    }

    /**
     * SSBox객체로 EntityClass 를 만들고자 할때 유용하다. SSBox객체의 Attribute name과 entity 클래스의
     * 필드명이 같으면 필드 type에 맞는 Data를 얻어 entity 클래스의 필드를 채운다.
     * 
     * @param entity Entity Object
     */
    public void copyToEntity(Object entity) {

        if (entity == null) {
            throw new NullPointerException("entity null");
        }

        Class c = entity.getClass();
        Field[] field = c.getFields();
        for (int i = 0; i < field.length; i++) {
            try {
                String fieldtype = field[i].getType().getName();
                String fieldname = field[i].getName();

                if (containsKey(fieldname)) {
                    if (fieldtype.equals("java.lang.String")) {
                        field[i].set(entity, getString(fieldname));
                    } else if (fieldtype.equals("int")) {
                        field[i].setInt(entity, getInt(fieldname));
                    } else if (fieldtype.equals("double")) {
                        field[i].setDouble(entity, getDouble(fieldname));
                    } else if (fieldtype.equals("long")) {
                        field[i].setLong(entity, getLong(fieldname));
                    } else if (fieldtype.equals("float")) {
                        field[i].set(entity, new Float(getDouble(fieldname)));
                    } else if (fieldtype.equals("boolean")) {
                        field[i].setBoolean(entity, getBoolean(fieldname));
                    } // end if ( fieldtype.equals("java.lang.String"))
                } // end if ( containsKey( fieldname ) )

            } catch (Exception e) {
                // Debug.warn.println(this, e.getMessage());
            }
        }
    }

    /**
     * 전달받은 key값에 해당되는 Value를 문자열로 return한다. 내부에 getString()을 이용하여 구현되어 있다.
     * 
     * @param key String
     * @return java.lang.String
     */
    public String get(String key) {
        return getString(key);
    }

    /**
     * 전달받은 key값에 해당되는 Value를 boolean으로return한다.
     * 
     * @param key String
     * @return boolean
     */
    public boolean getBoolean(String key) {
        String value = getString(key);
        boolean isTrue = false;
        try {
            isTrue = (new Boolean(value)).booleanValue();
        } catch (Exception e) {
        }

        return isTrue;
    }

    /**
     * 전달받은 key값에 해당되는 Value를 double로 return한다.
     * 
     * @param key String
     * @return double
     */
    public double getDouble(String key) {
        String value = removeComma(getString(key));
        if (value.equals("")) {
            return 0;
        }

        double num = 0;
        try {
            num = Double.valueOf(value).doubleValue();
        } catch (Exception e) {
            num = 0;
        }

        return num;
    }

    /**
     * 전달받은 key값에 해당되는 Value를 float로 return한다.
     * 
     * @param key String
     * @return float
     */
    public float getFloat(String key) {
        return (float) getDouble(key);
    }

    /**
     * 전달받은 key값에 해당되는 Value를 Int로 return한다.
     * 
     * @param key String
     * @return int
     */
    public int getInt(String key) {
        double value = getDouble(key);
        return (int) value;
    }

    /**
     * 전달받은 key값에 해당되는 Value를 long으로 return한다.
     * 
     * @param key String
     * @return long
     */
    public long getLong(String key) {
        String value = removeComma(getString(key));
        if (value.equals("")) {
            return 0L;
        }

        long lvalue = 0L;
        try {
            lvalue = Long.valueOf(value).longValue();
        } catch (Exception e) {
            lvalue = 0L;
        }

        return lvalue;
    }

    /**
     * Method Desciption : 전달받은 key값에 해당되는 Value를 Object으로 return 한다.
     * 
     * @param key
     * @return
     */
    public Object getObject(String key) {
        Object o = null;
        try {
            if(containsKey(key)) {
                o = (Object) super.get(key);
            }
        } catch (Exception e) {
        }
        return o;
    }

    /**
     * 전달받은 key값에 해당되는 Value를 String으로 return한다.
     * 
     * @param key String
     * @return java.lang.String
     */
    public String getString(String key) {
        String value = null;

        try {
            Object o = (Object) super.get(key);
            Class c = o.getClass();

            if (o == null) {
                value = "";
            } else if (c.isArray()) {
                int length = Array.getLength(o);
                if (length == 0) {
                    value = "";
                } else {
                    Object item = Array.get(o, 0);
                    if (item == null) {
                        value = "";
                    } else {
                        value = item.toString();
                    } // end if ( item == null )
                } // end if ( length == 0 )
            } else {
                value = o.toString();
            } // end if ( o == null )

        } catch (Exception e) {
            value = "";
        }

        return value;
    }

    /**
     * 전달받은 key값에 해당되는 Value를 Timestamp으로 return한다.
     * 
     * @param key String
     * @return Timestamp
     */
    public java.sql.Timestamp getTimestamp(String key) {
        return getTimestamp(key, "yyyy-MM-dd");
    }

    /**
     * 전달받은 key값에 해당되는 Value를 Timestamp으로 return한다.
     * 
     * @param key String
     * @return Timestamp
     */
    public java.sql.Timestamp getTimestamp(String key, String strPattern) {

        String strDate = getString(key);
        String pattern = "yyyy-MM-dd HH:mm:ss";

        if (strDate.equals(""))
            return null;
        if (strPattern != null)
            pattern = strPattern;

        try {
            java.text.SimpleDateFormat sd = new java.text.SimpleDateFormat(pattern);
            return new java.sql.Timestamp(sd.parse(strDate).getTime());
        } catch (java.text.ParseException e) {
            return null;
        }

    }

    /**
     * 전달받은 key값에 해당되는 Value를 Vector type으로 return한다.
     * 
     * @param key String
     * @return java.util.Vector
     */
    public Vector getVector(String key) {
        Vector vector = new Vector();
        try {
            Object o = (Object) super.get(key);
            Class c = o.getClass();
            if (o != null) {
                if (c.isArray()) {
                    int length = Array.getLength(o);
                    if (length != 0) {
                        for (int i = 0; i < length; i++) {
                            Object item = Array.get(o, i);
                            if (item == null) {
                                vector.addElement("");
                            } else {
                                vector.addElement(item.toString());
                            } // end if (item == null )
                        } // end for(int i=0; i<length;i++)
                    } // end if ( length != 0 )

                } else {
                    vector.addElement(o.toString());
                } // end if( c.isArray() )
            } // end if ( o != null )
        } catch (Exception e) {
        }

        return vector;
    }

    /**
     * 전달받은 key값에 해당되는 Value를 ArrayList type으로 return한다.
     * 
     * @param key String
     * @return java.util.ArrayList
     */
    public ArrayList getArrayList(String key) {
        ArrayList arrayList = new ArrayList();
        try {
            Object o = (Object) super.get(key);
            Class c = o.getClass();
            if (o != null) {
                if (c.isArray()) {
                    int length = Array.getLength(o);
                    if (length != 0) {
                        for (int i = 0; i < length; i++) {
                            Object item = Array.get(o, i);
                            if (item == null) {
                                arrayList.add("");
                            } else {
                                arrayList.add(item.toString());
                            } // end if (item == null )
                        } // end for(int i=0; i<length;i++)
                    } // end if ( length != 0 )
                } else {
                    arrayList.add(o.toString());
                } // end if( c.isArray() )
            } // end if ( o != null )
        } catch (Exception e) {
        }
        return arrayList;
    }



    /**
     * 전달받은 String값에서 ','값을 제거한다음 변경된 문자열을 반환한다. 금액형태의 DATA처리시 유용하다.
     * 
     * @param s String
     * @return java.lang.String
     */
    protected static String removeComma(String s) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(",") != -1) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c != ',') {
                    buf.append(c);
                } // end if ( c != ',')
            }// end for(int i=0;i<s.length();i++)

            return buf.toString();
        } // end if ( s.indexOf(",") != -1 )

        return s;
    }

    /**
     * 해당 key값에 Vector를 설정한다.
     * 
     * @param key java.lang.String
     * @param vector java.util.Vector
     */
    public void setVector(String key, Vector vector) {
        String value[] = new String[vector.size()];
        for (int idx = 0; idx < vector.size(); idx++)
            value[idx] = (vector.get(idx) == null) ? "" : vector.get(idx).toString();
        put(key, value);
    }

    /**
     * HttpServletRequest의 request로 만든 HashMap을 읽어서 name과 value로 문자열을 만들어
     * return한다.
     * 
     * @return java.lang.String
     */
    public synchronized String toString() {
        int max = size() - 1;
        StringBuffer buf = new StringBuffer();

        // key set
        Iterator Keys = keySet().iterator();

        // value set
        Iterator values = entrySet().iterator();

        buf.append("{");

        for (int i = 0; i <= max; i++) {

            String key = (String) Keys.next();
            String value = null;
            Object o = values.next();

            if (o == null) {
                value = "";

            } else {
                Class c = o.getClass();
                if (c.isArray()) {
                    int length = Array.getLength(o);
                    if (length == 0) {
                        value = "";
                    } else if (length == 1) {
                        Object item = Array.get(o, 0);
                        if (item == null) {
                            value = "";
                        } else {
                            value = item.toString();
                        } // end if ( item == null )
                    } else {
                        StringBuffer valueBuf = new StringBuffer();
                        valueBuf.append("[");
                        for (int j = 0; j < length; j++) {
                            Object item = Array.get(o, j);
                            if (item != null) {
                                valueBuf.append(item.toString());
                            }
                            if (j < length - 1) {
                                valueBuf.append(",");
                            }
                        } // end for ( int j=0;j<length;j++)

                        valueBuf.append("]");
                        value = valueBuf.toString();
                    } // end if ( length == 0 )

                } else {
                    value = o.toString();
                } // end if( c.isArray() )
            } // end if ( o == null )

            buf.append(key + "=" + value);
            if (i < max) {
                buf.append(", ");
            }
        } // end for (int i = 0; i <= max; i++)
        buf.append("}");

        return "SSBox[" + name + "]=" + buf.toString();

    }

    
    /**
     * @return Returns the metaData.
     */
    public ArrayList getMetaData() {
        return this.metaData;
    }

}
