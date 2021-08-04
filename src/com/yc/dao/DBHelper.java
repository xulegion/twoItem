package com.yc.dao;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper {
	static {
		try {
			MyProperties mp= MyProperties.getInstance();
			//方法一：反射
			Class.forName(mp.getProperty("driverClassName"));
//			System.out.println(1111111);
			//二：驱动注册
			//java.sql.DriverManager.registerDriver(new OracleDriver());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public Connection getConnection() {

		Connection conn=null;

		try {
			MyProperties mp= MyProperties.getInstance();
			conn=DriverManager.getConnection(mp.getProperty("url"),mp.getProperty("username"),mp.getProperty("password"));
			//conn=DriverManager.getConnection(mp.getProperty("url"),mp);  //properties中的用户名和密码必须为user,password(此处配置文件中key值还没改);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public int Select(String sql,Object...param) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
//			ps.setString(1,param);
			setParams(ps,param);
			System.out.println(ps);

			rs = ps.executeQuery();
			if (rs.next()) {
				return 1;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}



	//如果取出表中的列名，列的数据类型
	public List<Map<String,String>> doSelect(String sql,Object...params){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Connection conn=getConnection();
		//2.创建语句对象
		PreparedStatement ps=null;
		//4.执行语句对象，获取结果集
		ResultSet rs=null;
		try {
			//语句对象
			ps=conn.prepareStatement(sql);
			setParams(ps,params); //修改成调用此方法设置占位符
			rs=ps.executeQuery();                       //查询得到结果集
			ResultSetMetaData rsmd=rs.getMetaData();   //通过结果集获取列的元信息
			int columnCount=rsmd.getColumnCount();    //列的数量
			String[] columName=new String[columnCount];
			for(int i=0;i<columnCount;i++) {
				columName[i]=rsmd.getColumnLabel(i+1);
			}
			//5.循环结果集
			while(rs.next()) {
				//取出rs这一行中的各个列的值，创建一个map，存好这些值和列名，再将这个map存到list中
				Map<String,String> map=new HashMap<String,String>();
				//根据columName数组中保存的列名，取出rs当前这一行的各列的值
				for(String cn:columName) {
					String value=rs.getString(cn);
					map.put(cn, value);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			closeAll(conn,ps,rs);
		}
		return list;
	}

	public int doUpdate(String sql,Object...params) {
		Connection conn=getConnection();
		//预编译的语句对象
		PreparedStatement ps=null;
		int result=-1;
		try {
			ps=conn.prepareStatement(sql);
			setParams(ps, params);
			result=ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			closeAll(conn,ps);
		}
		return result;
	}



	public void setParams(PreparedStatement ps, Object... params) throws SQLException {
		if(params!=null&&params.length>0 ) {
			for(int i=0;i<params.length;i++) {
				ps.setObject(i+1, params[i]);
			}
		}
	}

	public void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
		if(params!=null&&params.size()>0 ) {
			for(int i=0;i<params.size();i++) {
				ps.setObject(i+1, params.get(i));
			}
		}
	}
	public int doUpdate(String sql,List<Object> params) {
		if(params==null || params.size()<=0) {
			return doUpdate(sql,new Object[] {});
		}
		return doUpdate(sql,params.toArray());

	}

	/**
	 * 带事务带?的修改，
	 * @param sqls
	 *            　　　要执行的sql语句集
	 * @return　成功返回true
	 */
	public boolean doUpdate(List<String> sqls, List<List<Object>> params) {
		Connection conn = this.getConnection();
		PreparedStatement ps=null;
		int result = 0;
		try {
			conn.setAutoCommit(false); // 关闭自动提交
			if (sqls != null && sqls.size() > 0) {
				for (int i = 0; i < sqls.size(); i++) {
					ps = conn.prepareStatement(sqls.get(i));
					if(  params!=null  && params.size()>0   &&   params.get(i)!=null  ){
						this.setParams(ps,params.get(i));
					}
					result = ps.executeUpdate();
				}
			}
			// 当所有语句执行完后没有出现错误，
			conn.commit(); // 提交修改
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} // 说明执行过程中出错，那么就回滚数据
			e.printStackTrace();

		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();

			}
			this.closeAll(conn,ps,null); // 只有查询才有结果集，更新没有结果集
		}
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}



	public void closeAll(Connection conn) {
		closeAll(conn,null,null);
	}

	public void closeAll(Connection conn,PreparedStatement ps) {
		closeAll(conn,ps,null);
	}

	public void closeAll(Connection conn,PreparedStatement ps,ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(ps!=null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(conn!=null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//查询表中的多条记录
	public <T> List<T> getForList(Class<T> clazz,String sql,Object...args){
		//1.获取连接
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			//2.预编译sql
			ps = conn.prepareStatement(sql);
//			for (int i = 0; i < args.length; i++) {
//				ps.setObject(i+1,args[i]);
//			}
			setParams(ps,args);
			rs = ps.executeQuery();
			//获取结果集的元数据，ResultSetMetaData
			ResultSetMetaData rsmd = rs.getMetaData();
			//获取结果集中的列数
			int columnCount = rsmd.getColumnCount();
			ArrayList<T> list = new ArrayList<>();
			while (rs.next()){
				T t = clazz.newInstance();
				for (int i=0;i<columnCount;i++){
					//获取列值
					Object columnValue = rs.getObject(i + 1);
					//获取列名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					//给cust对象指定的columnName属性，赋值为columnValue，通过反射
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					if ("ordertime".equals(columnLabel) ||"time".equals(columnLabel) ){
						Timestamp Value=rs.getTimestamp(i+1);
						field.set(t,Value);
					}else {
						field.set(t, columnValue);
					}
					field.set(t,columnValue);
				}
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeAll(conn,ps);
		}
		return null;
	}

}
