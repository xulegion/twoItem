package com.yc.utils;



import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanUtils {

    public static <T> T parseMapToObject(Map<String,String> map,Class<T> cls) throws Exception {
        T t = cls.newInstance();
       //1.到map中找有几个键
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        Method[] ms = cls.getDeclaredMethods();
        while (iterator.hasNext()){
            //2.循环这些键，到cls中找对应的setXxx方法
            String key = iterator.next();
            String value=map.get(key);
            Method setMedthod=findSetMethod(ms,key);
            //3.激活setXxx方法,设置值
                //没有这个方法
            if (setMedthod==null){
                continue;
            }
            //查看一下 setMethod的参数类型(注意，参数可能有多个，我们只考虑一个情况)
            Class<?>[] parameterTypes = setMedthod.getParameterTypes();
            if (parameterTypes==null || parameterTypes.length<=0){
                continue;
            }
            Class parameterType = parameterTypes[0];
            String parameterTypeName = parameterType.getName();  //set方法第一个参数的类型名
            if ("int".equals(parameterTypeName) || "java.lang.Integer".equals(parameterTypeName)){
                setMedthod.invoke(t,Integer.parseInt(value));
            }else if ("double".equals(parameterTypeName) || "java.lang.Double".equals(parameterTypeName)){
                setMedthod.invoke(t,Double.parseDouble(value));
            }else if ("float".equals(parameterTypeName) || "java.lang.Float".equals(parameterTypeName)){
                setMedthod.invoke(t,Float.parseFloat(value));
            }else if ("long".equals(parameterTypeName) || "java.lang.Long".equals(parameterTypeName)){
                setMedthod.invoke(t,Long.parseLong(value));
            }else if ("short".equals(parameterTypeName) || "java.lang.Short".equals(parameterTypeName)){
                setMedthod.invoke(t,Short.parseShort(value));
            }else if ("char".equals(parameterTypeName) || "java.lang.Character".equals(parameterTypeName)){
                setMedthod.invoke(t,value.substring(0,1));
            }else {
                setMedthod.invoke(t,value);
            }
        }
        return t;
    }

    private static Method findSetMethod(Method[] ms, String key) {
        if (ms==null || ms.length<=0){
            return null;
        }
        for (Method m:ms){
            String methodName="set"+key.substring(0,1).toUpperCase()+key.substring(1);
            if (methodName.equals(m.getName())){
                return m;
            }
        }
        return null;
    }

}
