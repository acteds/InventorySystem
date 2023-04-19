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
		map.put("uid","UID");
		map.put("name","名称");
		map.put("password","密码");
		map.put("phone","联系电话");
		map.put("rank","权限");

		map.put("pmid","参数序号");
		map.put("explanation","说明");
		map.put("ParametersSubCount","参数数量");

		map.put("psid","键值对序号");
		map.put("value","值");

		map.put("gid","货品序号");

		map.put("iid","清单号");
		map.put("quantity","数量");
		map.put("location","位置");
		map.put("createTime","创建时间");
		map.put("review","审核状态");

		map.put("lastChangeTime","最后修改时间");
		map.put("mainLocation","主要位置");
		return map;
	}

}
