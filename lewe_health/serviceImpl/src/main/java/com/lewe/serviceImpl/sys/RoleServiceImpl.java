package com.lewe.serviceImpl.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Menu;
import com.lewe.bean.sys.Role;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.MenuMapper;
import com.lewe.dao.sys.RoleMapper;
import com.lewe.dao.sys.vo.MenuTree;
import com.lewe.service.sys.IRoleService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.Page;
import com.lewe.util.common.StringUtils;

@Service("roleService")
public class RoleServiceImpl implements IRoleService {

	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private AccountMapper accountMapper;

	

	@Transactional
	public int saveRole(String roleId, String roleName, String menuIds, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(roleName)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入角色名");
			return 0;
		}
		if (StringUtils.isBlank(menuIds)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择该角色的权限");
			return 0;
		}
		Role role = new Role();
		role.setName(roleName);
		role.setMenuIds(menuIds);
		if (StringUtils.isBlank(roleId)) {// 新增
			role.setCreateTime(new Date());
			role.setCreatorId(loginAccount.getId());
			roleMapper.insertSelective(role);
		} else {// 修改
			role.setId(Integer.valueOf(roleId));
			role.setUpdateTime(new Date());
			roleMapper.updateByPrimaryKeySelective(role);
		}
		return 1;
	}

	public JSONObject getSysMenuTree() {
		JSONObject json = new JSONObject();
		JedisUtil redis = JedisUtil.getInstance();
		List<MenuTree> resultList = new ArrayList<MenuTree>();
		String key = "lewe_sysMenuTree";

		// 1.先从缓存中获取数据
		if (redis.exists(key)) {
			Map<String, String> menuMap = redis.hgetAll(key);
			MenuTree sysManager = new MenuTree();// 系统管理
			MenuTree customerManager = new MenuTree();// 客户管理
			MenuTree checkManager = new MenuTree();// 检测管理
			MenuTree dataCount = new MenuTree();// 数据统计
			for (String jsonStr : menuMap.values()) {
				MenuTree menuTree = JSONObject.parseObject(jsonStr, MenuTree.class);
				if (menuTree.getId() == 1) {// 系统管理
					sysManager = menuTree;
				} else if (menuTree.getId() == 15) {// 客户管理
					customerManager = menuTree;
				} else if (menuTree.getId() == 18) {// 检测管理
					checkManager = menuTree;
				} else if (menuTree.getId() == 23) {// 数据统计
					dataCount = menuTree;
				}
			}

			// 按照产品原型页面上的顺序展示菜单
			resultList.add(customerManager);
			resultList.add(checkManager);
			resultList.add(dataCount);
			resultList.add(sysManager);
		}

		if (resultList != null && resultList.size() > 0) {
			json.put("menuTree", resultList);
			return json;
		}
		// 缓存中没有,则查询数据库
		Map<String, Object> param = new HashMap<String, Object>();
		// 查询所有一级菜单
		param.put("level", 1);
		List<MenuTree> parentList = menuMapper.selectAllChildrenByParam(param);

		// 查询所有二级级菜单
		param.put("level", 2);
		List<MenuTree> secondChildrenList = menuMapper.selectAllChildrenByParam(param);

		// 查询所有三级级菜单
		param.put("level", 3);
		List<MenuTree> thirdChildrenList = menuMapper.selectAllChildrenByParam(param);

		// 构造菜单树
		for (MenuTree parentMenu : parentList) {
			// 第一级
			MenuTree firstMenuTree = new MenuTree();
			BeanUtils.copyProperties(parentMenu, firstMenuTree);
			// 第二级
			TreeSet<MenuTree> secondChildrens = new TreeSet<MenuTree>();
			for (MenuTree secondMenu : secondChildrenList) {
				if (secondMenu.getParentId().longValue() == parentMenu.getId().longValue()) {
					MenuTree secondTree = new MenuTree();
					BeanUtils.copyProperties(secondMenu, secondTree);
					// 第三级
					TreeSet<MenuTree> thirdChildrens = new TreeSet<MenuTree>();
					for (MenuTree thirdMenu : thirdChildrenList) {
						if (thirdMenu.getParentId().longValue() == secondMenu.getId().longValue()) {
							MenuTree thirdTree = new MenuTree();
							BeanUtils.copyProperties(thirdMenu, thirdTree);
							thirdChildrens.add(thirdTree);
						}
					}
					secondTree.setChildren(thirdChildrens);
					secondChildrens.add(secondTree);
				}
			}
			firstMenuTree.setChildren(secondChildrens);
			resultList.add(firstMenuTree);
		}

		if (resultList != null && resultList.size() > 0) {
			for (MenuTree menuTree : resultList) {
				String id = menuTree.getId().toString();
				// 存储数据到缓存中
				redis.hset(key, id, JSONObject.toJSONString(menuTree));
			}
		}
		// 处理菜单展示顺序
		MenuTree sysManager = null;// 系统管理
		MenuTree customerManager = null;// 客户管理
		MenuTree checkManager = null;// 检测管理
		MenuTree dataCount = null;// 数据统计
		for (MenuTree menuTree : resultList) {
			if (menuTree.getId() == 1) {// 系统管理
				sysManager = menuTree;
			} else if (menuTree.getId() == 15) {// 客户管理
				customerManager = menuTree;
			} else if (menuTree.getId() == 18) {// 检测管理
				checkManager = menuTree;
			} else if (menuTree.getId() == 23) {// 数据统计
				dataCount = menuTree;
			}
		}
		// 按照产品原型页面上的顺序展示菜单
		List<MenuTree> menuListSort = new ArrayList<MenuTree>();
		if (customerManager != null) {
			menuListSort.add(customerManager);
		}
		if (checkManager != null) {
			menuListSort.add(checkManager);
		}
		if (dataCount != null) {
			menuListSort.add(dataCount);
		}
		if (sysManager != null) {
			menuListSort.add(sysManager);
		}
		json.put("menuTree", menuListSort);
		return json;
	}

	@Transactional
	public int delRole(String roleId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (StringUtils.isBlank(roleId)) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要删除的角色");
			return 0;
		}
		if (Integer.valueOf(roleId) == 1) {
			result.setCode(BizCode.NOT_ALLOW_DEL);
			result.setMessage("超级管理员不允许删除");
			return 0;
		}
		// 通过角色id查询当前角色是否有用户存在,若存在则不允许删除.
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		map.put("roleId", roleId);
		Integer count = accountMapper.selectCountByMap(map);
		if (count != null && count > 0) {
			result.setCode(BizCode.NOT_ALLOW_DEL);
			result.setMessage("该角色不可删除,有账号在使用该角色");
			return 0;
		}
		Role record = new Role();
		record.setId(Integer.valueOf(roleId));
		record.setUpdateTime(new Date());
		record.setIsDel((byte) 1);
		roleMapper.updateByPrimaryKeySelective(record);
		return 1;
	}

	public JSONObject getRoleList(Integer pageNo, Integer pageSize, Account account) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isDel", 0);
		Integer totalCount = roleMapper.selectCountByMap(map);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("roleList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<Role> list = roleMapper.selectListByMap(map);
		for (Role role : list) {
			String menuNames = "";
			String menuIds = role.getMenuIds();
			if (StringUtils.isNotBlank(menuIds)) {
				int i = 1;
				String[] arr = menuIds.split("\\,");
				for (String menuId : arr) {
					Menu menu = menuMapper.selectByPrimaryKey(Integer.valueOf(menuId));
					if (menu != null) {
						if (i == 1) {
							menuNames = menuNames + menu.getName();
						} else {
							menuNames = menuNames + "," + menu.getName();
						}
					}
					i++;
				}
			} else {
				menuNames = "--";
			}
			role.setMenuNames(menuNames);
		}
		json.put("page", page);
		json.put("roleList", list);
		return json;
	}

	public JSONObject getRoleDetail(Integer roleId, Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		if (roleId != null) {
			Role role = roleMapper.selectByPrimaryKey(roleId);
			json.put("id", roleId);
			json.put("name", role == null ? null : role.getName());
			json.put("menuIds", role == null ? null : role.getMenuIds());
		}
		;
		return json;
	}
}
