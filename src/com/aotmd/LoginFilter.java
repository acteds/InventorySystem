package com.aotmd;

import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器,全局拦截未登陆
 * @author aotmd
 */
@Order(1)
public class LoginFilter implements HandlerInterceptor {
	private Map<String, String> excludeExtensions;
	private Map<String, String> excludeURLs;
	private void init(){
		/*排除的扩展名*/
		String[] excludeExtensions = { ".css", ".js", ".png", ".jpg", ".ttf", ".woff", ".woff2" };
		this.excludeExtensions =new HashMap<>();
		for (String s : excludeExtensions) {
			this.excludeExtensions.put(s, s);
		}
		/*排除拦截的网址*/
		String[] excludeURLs = { "/login", "/Login", "/reg", "/Reg"};
		this.excludeURLs =new HashMap<>();
		for (String s : excludeURLs) {
			this.excludeURLs.put(s, s);
		}

	}
	public LoginFilter() {
		init();
	}
	/**
	 *preHandle方法在控制器的处理请求方法前执行
	 * @param request 请求对象
	 * @param response 应答对象
	 * @param handler 处理器
	 * @return 返回true表示继续向下执行,返回false表示中断后续操作
	 * @throws IOException IO流异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		/*URL网址*/
		String requestUri = request.getRequestURI();
		/*项目名称*/
		String ctxPath = request.getContextPath();

		/*设置网址取不到点号时的默认值*/
		int uriLast = requestUri.lastIndexOf(".");
		if (uriLast!=-1) {
			String extensionNameUri = requestUri.substring(uriLast).toLowerCase();
			if (excludeExtensions.get(extensionNameUri)!=null) {
				System.out.print("扩展名排除 ");
				return true;
			}
		}
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		/*实际控制器Url*/
		String controllerUri = requestUri.substring(ctxPath.length());
		if (excludeURLs.get(controllerUri)!=null) {
			System.out.print("网址排除 ");
			return true;
		}

		if (request.getSession().getAttribute("user") != null) {
			System.out.print("用户排除 ");
			return true;
		} else {
			response.getWriter().print("<script>alert('你没有登录!');window.location='"+ctxPath+"/'</script>");
			return false;
		}
	}
}