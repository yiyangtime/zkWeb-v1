package com.yasenagat.zkweb.web;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.yasenagat.zkweb.util.ZkCache;
import com.yasenagat.zkweb.util.ZkCfgFactory;
import com.yasenagat.zkweb.util.ZkCfgManager;
import com.yasenagat.zkweb.util.ZkManagerImpl;

/**
 * zookeeper配置管理controller
 * 
 * @author LS
 * @date 2018年11月27日下午5:22:51
 */
@RestController
@RequestMapping("/zkcfg")
public class ZkCfgController {
	private static final Logger Logger = LoggerFactory.getLogger(ZkCfgController.class);
	static ZkCfgManager zkCfgManager = ZkCfgFactory.createZkCfgManager();

	/**
	 * 查询zookeeper配置
	 * 
	 * @param page
	 * @param rows
	 * @param whereSql
	 * @return
	 */
	@RequestMapping(value = "/queryZkCfg")
	public Map<String, Object> queryZkCfg(@RequestParam(required = false) int page,
			@RequestParam(required = false) int rows, @RequestParam(required = false) String whereSql) {
		Map<String, Object> map = null;
		try {
			Logger.info(new Date() + "");
			map = new HashMap<String, Object>();
			map.put("rows", zkCfgManager.query(page, rows, URLDecoder.decode(whereSql, "utf-8")));
			map.put("total", zkCfgManager.count());
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
		}
		return map;
	}

	/**
	 * 添加zookeeper配置
	 * 
	 * @param desc
	 * @param connectstr
	 * @param sessiontimeout
	 * @return
	 */
	@RequestMapping(value = "/addZkCfg", produces = "text/html;charset=UTF-8")
	public String addZkCfg(@RequestParam(required = false) String desc,
			@RequestParam(required = false) String connectstr, @RequestParam(required = false) String sessiontimeout) {

		try {
			// String id = UUID.randomUUID().toString().replaceAll("-", "");
			String id = UUID.randomUUID().toString();
			if (ZkCfgFactory.createZkCfgManager().add(id, desc, connectstr, sessiontimeout)) {
				ZkCache.put(id, ZkManagerImpl.createZk().connect(connectstr, Integer.parseInt(sessiontimeout)));
			}
			;

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
			return "添加失败";
		}
		return "添加成功";
	}

	/**
	 * 根据ID查询zookeeper配置
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryZkCfgById")
	public Map<String, Object> queryZkCfg(@RequestParam(required = false) String id) {

		try {
			return ZkCfgFactory.createZkCfgManager().findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 更新zookeeper配置
	 * 
	 * @param id
	 * @param desc
	 * @param connectstr
	 * @param sessiontimeout
	 * @return
	 */
	@RequestMapping(value = "/updateZkCfg", produces = "text/html;charset=UTF-8")
	public String updateZkCfg(@RequestParam(required = true) String id, @RequestParam(required = false) String desc,
			@RequestParam(required = false) String connectstr, @RequestParam(required = false) String sessiontimeout) {

		try {
			if (ZkCfgFactory.createZkCfgManager().update(id, desc, connectstr, sessiontimeout)) {
				ZkCache.put(id, ZkManagerImpl.createZk().connect(connectstr, Integer.parseInt(sessiontimeout)));
			}
			;

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
			return "保存失败";
		}
		return "保存成功";
	}

	/**
	 * 删除zookeeper配置
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delZkCfg", produces = "text/html;charset=UTF-8")
	public String delZkCfg(@RequestParam(required = true) String id) {
		try {
			ZkCfgFactory.createZkCfgManager().delete(id);
			ZkCache.remove(id);

		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage(), e);
			return "删除失败";
		}
		return "删除成功";
	}
}
