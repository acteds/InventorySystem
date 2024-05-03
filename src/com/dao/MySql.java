package com.dao;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

interface MySqlDao{
	@SelectProvider(type = UserMapper.class, method = "returnSql")
	List<LinkedHashMap<String,Object>> select(String sql);
	@UpdateProvider(type = UserMapper.class, method = "returnSql")
	int update(String sql);
	@InsertProvider(type = UserMapper.class, method = "returnSql")
	int insert(String sql);
	@DeleteProvider(type = UserMapper.class, method = "returnSql")
	int delete(String sql);
}
/**
 * 	自定义Sql运行类
 * <p>
 * 	常见用法:List&lt;LinkedHashMap&lt;String, Object&gt;&gt; list=ms.setSql("select * from linkman where
 * name=?").set("张三").runlist();
 * <p>
 * @date 2023-3-23
 * @author aotmd
 */
@Repository
public class MySql {
	static ThreadLocal<ThreadCache> TL = new ThreadLocal<ThreadCache>(){
		@Override
		protected ThreadCache initialValue() {
			return new ThreadCache();
		}
	};

	public static class ThreadCache {
		public String sql = "";
		public PreparedStatement ps = null;
		/**
		 * 总条目数
		 */
		public int sum = 0;
		/**
		 * 索引
		 */
		public int index = 0;
		/**
		 * 数据库表字段(Map键值)
		 */
		public String[] top = null;

		public ThreadCache() {
		}
	}
	MySqlDao mySqlDao;
	private Connection conn = null;

	public MySql(MySqlDao mySqlDao) {
		this.mySqlDao = mySqlDao;
		connectToTheDatabase();
	}

	/**
	 * 	反复使用时的更新Sql语句方法
	 * @param sql mysql语句
	 * @return this
	 */
	public MySql setSql(String sql) {
		TL.get().sum = 0;
		TL.get().index = 1;
		try {
		    //检查连接存活状态
            if (conn.isClosed()){
                connectToTheDatabase();
            }
			TL.get().ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}
	/**
	 *	 调试用方法
	 * @return 返回sql语句
	 */
	public String getSql() {
		return TL.get().sql;
	}
	/**
	 *	 连接数据库
	 *
	 */
	private void connectToTheDatabase() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/inventory_system?useUnicode=true&characterEncoding=utf-8";
			conn = DriverManager.getConnection(url, "root", "123456");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从前往后一个一个修改
	 * @param object 修改数据库?号所对值
	 * @return this
	 */
	public MySql set(Object object) {
		try {
			TL.get().ps.setObject(TL.get().index++,object);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 	更新的形式提交
	 * @return (1) SQL 数据操作语言 (DML) 语句的行数 (2) 对于无返回内容的 SQL 语句，返回 0
	 */
	public int run() {
		int i = 0;
		try {
			String temp = TL.get().ps.toString();
			TL.get().ps.close();
			String sql1 = temp.substring(temp.indexOf(": ") + 2);
			TL.get().sql = sql1;//记录用
        	if (sql1.contains("?")){
				System.out.println("sql语句错误!有多余的?号");
            }
			String type=sql1.trim().split("\\s+")[0];
        	if (type.equalsIgnoreCase("update")){
        		i=mySqlDao.update(sql1);
			}else if (type.equalsIgnoreCase("insert")){
				i=mySqlDao.insert(sql1);
			}else if (type.equalsIgnoreCase("delete")){
				i=mySqlDao.delete(sql1);
			}else {
				System.out.println("不是增删改的数据库语句");
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * 	运行executeQuery(),即查询内容
	 * 	<P>调用方法:for(LinkedHashMap&lt;String, Object&gt; map:list){map.get(key)}
	 * <p>
	 * 	示例:topname为getName()取得的String[]
	 * <ul>
	 * <li>for(LinkedHashMap&lt;String, Object&gt; map:list) {</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;for(int j = 0; j < topname.length; j++){</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.print(map.get(topname[j])+"\t");</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;}</li>
	 * <li>System.out.println();</li> }
	 * </ul>
	 *
	 * @return 返回List对象
	 */
	public List<LinkedHashMap<String, Object>> runList() {
		// 声明返回的对象
		List<LinkedHashMap<String, Object>> list = new ArrayList<>();
		try {
			String temp = TL.get().ps.toString();
			String sql1;
			sql1 =temp.substring(temp.indexOf(": ")+2);
			TL.get().sql = sql1;//记录用
			if (sql1.contains("?")){
				System.out.println("sql语句错误!有多余的?号");
				System.out.println(sql1);
				TL.get().ps.close();
				return list;
			}
			String type= sql1.trim().split("\\s+")[0];
			if (type.equalsIgnoreCase("select")){
				list=mySqlDao.select(sql1);
			}else {
				System.out.println("不是查询的数据库语句");
				System.out.println(sql1);
				TL.get().ps.close();
				return list;
			}
			TL.get().sum = list.size();
			//当MyBatis取得到值时获取数据库列字段,取不到时使用JDBC取数据库列字段
			if (!list.isEmpty()){
				TL.get().top = list.get(0).keySet().toArray(new String[0]);
			} else {
				System.out.println("调用JDBC获取字段");
				ResultSet resu = TL.get().ps.executeQuery();
				// 分析结果集
				ResultSetMetaData rsmd = resu.getMetaData();
				// 获取列数
				int cols = rsmd.getColumnCount();
				// 初始化
				TL.get().top = new String[cols];
				for (int i = 0; i < cols; i++) {
					// 获取列名存入String[]
					TL.get().top[i] = rsmd.getColumnName(i + 1);
				}
			}
			TL.get().ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 	调用该方法需正确设置sql语句,<p>"select * from linkman where uid=? order by id asc limit ?,?"<p>
	 * 	<b>order后的两个?号不需要set()设置</b>
	 * @param request request
	 * @param url 当前Servlet网址
	 * @param pageMax 以多少记录为一页
	 */
	public void runPagination(HttpServletRequest request, String url, int pageMax) {
		//当前页
		int currentPage = 1;
		// 判断传递页码是否有效
		if (request.getParameter("currentPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		List<LinkedHashMap<String, Object>> list = this.set((currentPage - 1) * pageMax).set(pageMax).runList();
		request.getSession().setAttribute("list", list);
		//重新查询未分页时的记录数(更新sum值)
		this.setSql(TL.get().sql.substring(0, TL.get().sql.indexOf("order")-1)).runList();
		// 总页数
		int pages;
		if (this.getSum() % pageMax == 0) {
			pages = this.getSum() / pageMax;
		} else {
			pages = this.getSum() / pageMax + 1;
		}
		StringBuilder sb = new StringBuilder();
		//格式化url,传值有/则截去
		if(url.indexOf("/")==0) {url=url.substring(1);}
		// 构建分页条
		for (int i = 1; i <= pages; i++) {
			if (i == currentPage) {
				sb.append(String.format("<b>%d</b>", i));
			} else {
				sb.append(String.format("<a href='%s?currentPage=%d'>%d</a>", url, i, i));
			}
			if (i != pages) {
				sb.append("　");
			}
		}
		request.getSession().setAttribute("bar", sb.toString());
	}
	/**
	 *  	 获取数据库表字段(Map键值)
	 *
	 * @return 返回String[](Map键值)
	 */
	public String[] getTop() {
		return TL.get().top;
	}

	/**
	 *   	返回executeQuery()获得记录的行数,
	 *   <p>若使用分页方法则返回的是未分页时的记录条数
	 * @return 返回executeQuery()获得记录的行数
	 */
	public int getSum() {
		return TL.get().sum;
	}

	//	public static void main(String[] args) {
	/*	MySql ms = new MySql("select * from linkman where uid=?");
		List<Map<String, Object>> list = ms.set(1).runlist();
		//-------------------------获取表头(Map的Key)----------------------------
		String[] topname = ms.getTop();
		for (int i = 0; i < topname.length; i++) {
			System.out.print(topname[i] + "\t");
		}
		System.out.println();
		//----------------------------------------------------------------------
		for (Map<String, Object> map : list) {
			for (int j = 0; j < topname.length; j++) {
				System.out.print(map.get(topname[j]) + "\t");
			}
			System.out.println();
		}
		System.out.println("共" + ms.getSum() + "条数据");
		System.out.println(ms.getSql());*/
		/*
		 * 	//下为增强for实际代码
		 * for(int i=0;i<list.size();i++) {
		 * Map<String, Object> map=list.get(i);
		 * for (int j = 0; j < topname.length; j++) {//指定key
		 * System.out.print(map.get(topname[j])+"\t"); }
		 * System.out.println(); }
		 * 	//增强for
		 * for (String key : map.keySet())
		 * {//map为无序输出,因此不一定会按加入顺序输出,要按顺序输出请指定key或者改为list
		 * System.out.print(key + " ：" +map.get(key));
		 * }
		 * System.out.println();
		 * 	//下为增强for实际代码
		 *  for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
		 *   String key=iterator.next();
		 *   System.out.print(key + " ：" + map.get(key));
		 *   }
		 */
//	}
}
