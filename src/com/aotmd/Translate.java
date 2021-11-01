package com.aotmd;

import java.util.HashMap;
import java.util.Map;

/**
 * 翻译器
 * @author aotmd
 */
public class Translate {
	public static Map<String, String> getTranslate() {
		Map<String, String> map= new HashMap<>(16);
		map.put("password","密码");
		map.put("rank","用户类型");
		map.put("username","用户名");
		map.put("description","个人介绍");

		return map;
	}

}
