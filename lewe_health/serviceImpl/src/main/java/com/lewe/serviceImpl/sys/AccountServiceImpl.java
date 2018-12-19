package com.lewe.serviceImpl.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSON;

import com.alibaba.fastjson.JSONObject;
import com.lewe.bean.check.CheckItem;
import com.lewe.bean.hospital.Hospital;
import com.lewe.bean.hospital.HospitalGroup;
import com.lewe.bean.hospital.HospitalLinkman;
import com.lewe.bean.sys.Account;
import com.lewe.bean.sys.Channel;
import com.lewe.bean.sys.Role;
import com.lewe.dao.check.CheckItemMapper;
import com.lewe.dao.hospital.HospitalGroupMapper;
import com.lewe.dao.hospital.HospitalLinkmanMapper;
import com.lewe.dao.hospital.HospitalMapper;
import com.lewe.dao.sys.AccountMapper;
import com.lewe.dao.sys.ChannelMapper;
import com.lewe.dao.sys.MenuMapper;
import com.lewe.dao.sys.RoleMapper;
import com.lewe.dao.sys.vo.MenuTree;
import com.lewe.service.sys.IAccountService;
import com.lewe.service.sys.IRoleService;
import com.lewe.service.sys.ISysLogService;
import com.lewe.util.common.ApiResult;
import com.lewe.util.common.BizCode;
import com.lewe.util.common.JedisUtil;
import com.lewe.util.common.MD5;
import com.lewe.util.common.Page;
import com.lewe.util.common.PropertiesUtil;
import com.lewe.util.common.StringUtils;
import com.lewe.util.common.constants.AccountType;

@Service("accountService")
public class AccountServiceImpl implements IAccountService {

	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private HospitalMapper hospitalMapper;
	@Autowired
	private HospitalGroupMapper hospitalGroupMapper;
	@Autowired
	private CheckItemMapper checkItemMapper;
	@Autowired
	private HospitalLinkmanMapper hospitalLinkmanMapper;

	public static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	public Account accountLogin(String account, String password, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		Account sysAccount = accountMapper.selectByAccount(account);
		if (sysAccount == null) {
			result.setCode(BizCode.LOGIN_FAIL);
			result.setMessage("登录失败,账号不存在！");
			return null;
		}
		if (!sysAccount.getPassword().equals(MD5.md5WithKey(password, PropertiesUtil.getApiPropertyByKey("password.md5.key")))) {
			result.setCode(BizCode.LOGIN_FAIL);
			result.setMessage("登录失败,密码错误！");
			return null;
		}
		if (sysAccount.getStatus() == 2) {
			result.setCode(BizCode.LOGIN_FAIL);
			result.setMessage("您的账号已被冻结,暂无法使用！");
			return null;
		}
		result.setCode(BizCode.LOGIN_SUCCESS);
		sysAccount.setPassword(null);
		result.setData(sysAccount);
		result.setMessage(BizCode.getCodeMsg(BizCode.LOGIN_SUCCESS));
		return sysAccount;
	}

	public JSONObject getAccountAndHospitalInfo(Account account, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (account == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("登录账号不能为空！");
			return null;
		}
		Hospital hospital = hospitalMapper.selectByPrimaryKey(account.getHospitalId());
		JSONObject json = new JSONObject();
		if (hospital == null) {
			result.setCode(BizCode.DATA_NOT_FOUND);
			result.setMessage("未查询到门店机构信息！");
			return json;
		}
		// 用户名
		json.put("accountName", account.getName());
		json.put("hospitalId", hospital.getId());
		// 门店机构名称
		json.put("hospitalName", hospital.getHospitalName());
		// 门店机构编号
		json.put("hospitalCode", hospital.getHospitalCode());
		// 门店机构地址
		json.put("hospitalAddress", hospital.getAreaCodeName() + hospital.getDetailAddress());
		// 门店LOGO
		json.put("hospitalLogo", hospital.getLogoUrl());
		// 渠道名称
		Channel channel = channelMapper.selectByPrimaryKey(hospital.getChannelId());
		json.put("channelId", channel == null ? "" : channel.getId());
		json.put("channelName", channel == null ? "" : channel.getName());
		// 可检测项目
		String checkItemName = "";
		String checkItemIds = hospital.getCheckItemIds();
		if (checkItemIds != null) {
			int i = 0;
			String[] arr = checkItemIds.split("\\,");
			for (String itemId : arr) {
				CheckItem checkItem = checkItemMapper.selectByPrimaryKey(Integer.valueOf(itemId));
				if (checkItem != null) {
					if (i == 0) {
						checkItemName = checkItemName + checkItem.getName();
					} else {
						checkItemName = checkItemName + " " + checkItem.getName();
					}
					i++;
				}
			}
		}
		json.put("checkItemName", checkItemName);
		// 门店联系人
		List<HospitalLinkman> linkManList = hospitalLinkmanMapper.selectByHospitalId(hospital.getId());
		json.put("linkManList", linkManList);
		return json;
	}

	@Autowired
	private ISysLogService sysLogService;

	@Transactional
	public int saveAccount(String accountStr, Account loginAccount, Object apiResult) {
		if (accountStr != null) {
			ApiResult result = (ApiResult) apiResult;
			Account account = JSONObject.parseObject(accountStr, Account.class);
			int checkParam = checkParam(account, result);
			if (checkParam == 0)
				return 0;
			// 将密码进行md5加密
			String password = account.getPassword();
			String md5Password = MD5.md5WithKey(password, PropertiesUtil.getApiPropertyByKey("password.md5.key"));
			account.setPassword(md5Password);
			Hospital hospital = hospitalMapper.selectByPrimaryKey(account.getHospitalId());
			if (hospital != null) {
				account.setChannelId(hospital.getChannelId());
			}
			String content = "";
			if (account.getId() == null) {
				// 新增
				Account accountDB = accountMapper.selectByAccount(account.getAccount());
				if (accountDB != null) {
					result.setCode(BizCode.DATA_EXIST);
					result.setMessage("该账号已存在！请重新填写");
					return 0;
				}
				account.setCreatorId(loginAccount.getId());
				account.setCreateTime(new Date());
				accountMapper.insertSelective(account);
				content = "新增了一个账号信息,姓名:" + account.getName() + ",账号:" + account.getAccount();
			} else {
				// 修改
				Account accountDB = accountMapper.selectByAccount(account.getAccount());
				if (accountDB != null && accountDB.getId() != account.getId()) {
					result.setCode(BizCode.DATA_EXIST);
					result.setMessage("该账号已存在！请重新填写");
					return 0;
				}
				account.setUpdateTime(new Date());
				String key = "lewe_loginAccountMenuTree:" + account.getId();
				JedisUtil redis = JedisUtil.getInstance();
				// 账号编辑之后有可能更改了角色,因此清除一下缓存数据.
				redis.del(key);
				accountMapper.updateByPrimaryKeySelective(account);
				content = "修改了一个账号信息,姓名:" + account.getName() + ",账号:" + account.getAccount();
			}
			// 将账号密码存储到缓存中(后台账号列表显示原始密码时用到)
			JedisUtil redis = JedisUtil.getInstance();
			String key = "lewe_account:" + account.getId();
			redis.hset(key, "password", password);
			redis.hset(key, "md5Password", md5Password);
			sysLogService.addSysLog(loginAccount, content, new Date());
		}
		return 1;
	}

	/**
	 * 校验新增或修改 账号必填字段
	 * 
	 * @param account
	 * @param result
	 * @return
	 */
	private int checkParam(Account account, ApiResult result) {
		if (StringUtils.isBlank(account.getHospitalId())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择机构");
			return 0;
		}
		if (StringUtils.isBlank(account.getName())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入用户姓名");
			return 0;
		}
		if (StringUtils.isBlank(account.getAccount())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入账号");
			return 0;
		}
		if (StringUtils.isBlank(account.getPassword())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请输入密码");
			return 0;
		}
		if (StringUtils.isBlank(account.getRoleId())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择角色");
			return 0;
		}
		if (StringUtils.isBlank(account.getHospitalGroupId())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择门店组");
			return 0;
		}
		if (StringUtils.isBlank(account.getShowFieldIds())) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("请选择要显示的字段");
			return 0;
		}
		return 1;
	}

	@Transactional
	public int delAccount(String accountId, Account loginAccount, Object apiResult) {
		if (StringUtils.isNotBlank(accountId)) {
			Account record = new Account();
			record.setId(Long.valueOf(accountId));
			record.setIsDel((byte) 1);
			record.setUpdateTime(new Date());
			accountMapper.updateByPrimaryKeySelective(record);
			Account account = accountMapper.selectByPrimaryKey(Long.valueOf(accountId));
			String content = "删除了" + account.getName() + "的账号信息";
			sysLogService.addSysLog(loginAccount, content, new Date());
		}
		return 1;
	}

	@Transactional
	public int updateAccountStatus(String accountId, Integer status, Account loginAccount, Object apiResult) {
		if (StringUtils.isNotBlank(accountId) && status != null) {
			Account record = new Account();
			record.setId(Long.valueOf(accountId));
			record.setStatus(status);
			record.setUpdateTime(new Date());
			accountMapper.updateByPrimaryKeySelective(record);
			Account account = accountMapper.selectByPrimaryKey(Long.valueOf(accountId));
			String content = "";
			if (status == 1) {
				content = "解冻了" + account.getName() + "的账号信息";
			} else if (status == 2) {
				content = "冻结了" + account.getName() + "的账号信息";
			}
			sysLogService.addSysLog(loginAccount, content, new Date());
		}
		return 1;
	}

	public JSONObject getAccountList(String keyword, Integer hospitalId, Integer pageNo, Integer pageSize,
			Account loginAccount, Object apiResult) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword.replaceAll("\\s*", "");
			map.put("keyword", "%" + keyword + "%");
		}
		if (hospitalId != null) {
			map.put("hospitalId", hospitalId);
		}
		map.put("isDel", 0);
		Integer totalCount = accountMapper.selectCountByMap(map);
		if (totalCount == null || totalCount == 0) {
			json.put("page", null);
			json.put("accountList", null);
			return json;
		}
		Page page = new Page(pageNo, pageSize, totalCount);
		map.put("startIndex", page.getStartIndex());
		map.put("pageSize", page.getPageSize());
		List<Account> list = accountMapper.selectListByMap(map);
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (Account account : list) {
			JSONObject accountJson = new JSONObject();
			accountJson.put("id", account.getId());
			// 用户姓名
			accountJson.put("userName", account.getName());
			// 账号
			accountJson.put("account", account.getAccount());
			// 密码(需要将原始密码给管理员看)
			JedisUtil redis = JedisUtil.getInstance();
			String key = "lewe_account:" + account.getId();
			// 取出原始密码
			String password = redis.hget(key, "password");
			accountJson.put("password", password == null ? account.getPassword() : password);
			// 账号状态 1:正常 2:冻结
			accountJson.put("status", account.getStatus());

			Role role = roleMapper.selectByPrimaryKey(account.getRoleId());
			Hospital hospital = hospitalMapper.selectByPrimaryKey(account.getHospitalId());
			HospitalGroup hospitalGroup = hospitalGroupMapper.selectByPrimaryKey(account.getHospitalGroupId());
			// 角色名称
			accountJson.put("roleName", role == null ? "" : role.getName());
			// 门店机构名称
			accountJson.put("hospitalName", hospital == null ? "" : hospital.getHospitalName());
			// 门店组名称
			accountJson.put("groupName", hospitalGroup == null ? "" : hospitalGroup.getName());
			jsonList.add(accountJson);
		}
		json.put("page", page);
		json.put("accountList", jsonList);
		return json;
	}

	@Autowired
	private IRoleService roleService;

	public JSONObject getLoginAccountMenu(Account account, Object apiResult) {
		JSONObject json = new JSONObject();
		List<MenuTree> menuList = new ArrayList<MenuTree>();

		JedisUtil redis = JedisUtil.getInstance();
		String key = "lewe_loginAccountMenuTree:" + account.getId();

		if (account != null && account.getAccountType() == AccountType.SUPERADMIN.getValue()) {
			// 超级管理员的菜单直接取系统全部菜单
			JSONObject sysMenu = roleService.getSysMenuTree();
			logger.error("JSONObject 356 getLoginAccountMenu(Account account, Object apiResult)" + sysMenu.toJSONString());
			json.put("menuList", sysMenu.getJSONArray("menuTree"));

		} else {
			// 其他账号则通过角色拥有的菜单id 查询并构造出当前登录账号所能看到的菜单树
			// 先从缓存中拿数据
			if (redis.exists(key)) {
				Map<String, String> menuMap = redis.hgetAll(key);
				// 处理菜单展示顺序
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
				if (customerManager.getId() != null) {
					menuList.add(customerManager);
				}
				if (checkManager.getId() != null) {
					menuList.add(checkManager);
				}
				if (dataCount.getId() != null) {
					menuList.add(dataCount);
				}
				if (sysManager.getId() != null) {
					menuList.add(sysManager);
				}
				json.put("menuList", menuList);
				logger.error("JSONObject 395 getLoginAccountMenu(Account account, Object apiResult)" + menuList.toString());
				return json;
			}

			// 缓存中无,查询数据库
			Role role = roleMapper.selectByPrimaryKey(account.getRoleId());
			if (role != null) {
				String menuIds = role.getMenuIds();
				logger.error("JSONObject 405 getLoginAccountMenu(Account account, Object apiResult)" + menuIds);
				

				if (StringUtils.isNotBlank(menuIds)) {
					String[] arr = menuIds.split("\\,");
					// 1.先筛选出相同级别的菜单(用treeSet集合实现顺序排放)
					Set<MenuTree> set1 = new TreeSet<MenuTree>();
					Set<MenuTree> set2 = new TreeSet<MenuTree>();
					Set<MenuTree> set3 = new TreeSet<MenuTree>();
					for (String menuId : arr) {
						MenuTree menu = menuMapper.selectByMenuId(Integer.valueOf(menuId));
						if (menu != null && menu.getLevel() == 1) {
							set1.add(menu);
						}
						if (menu != null && menu.getLevel() == 2) {
							set2.add(menu);
						}
						if (menu != null && menu.getLevel() == 3) {
							set3.add(menu);
						}
					}
					// 2.从第三级菜单开始向上查询二级菜单
					Set<MenuTree> secondParents = new HashSet<MenuTree>();
					for (MenuTree menu : set3) {
						MenuTree secondParent = menuMapper.selectByMenuId(menu.getParentId());
						secondParents.add(secondParent);
					}
					secondParents.addAll(set2);

					// 3.从第二级菜单向上查询一级菜单
					Set<MenuTree> firstParents = new HashSet<MenuTree>();
					for (MenuTree menu : secondParents) {
						MenuTree firstParent = menuMapper.selectByMenuId(menu.getParentId());
						firstParents.add(firstParent);
					}
					firstParents.addAll(set1);

					// 4.分别查出当前角色的一级菜单的子菜单和二级菜单的子菜单
					Map<String, Object> param = new HashMap<String, Object>();
					List<MenuTree> secondMenuList = null;
					for (MenuTree menuTree : set1) {
						param.put("parentId", menuTree.getId());
						param.put("level", 2);
						secondMenuList = menuMapper.selectAllChildrenByParam(param);
						if (secondMenuList != null && secondMenuList.size() > 0) {
							secondParents.addAll(secondMenuList);
						}
					}
					List<MenuTree> thirdMenuList = null;
					for (MenuTree menuTree : set2) {
						param.put("parentId", menuTree.getId());
						param.put("level", 3);
						thirdMenuList = menuMapper.selectAllChildrenByParam(param);
						if (thirdMenuList != null && thirdMenuList.size() > 0) {
							set3.addAll(thirdMenuList);
						}
					}

					// 5.开始构造菜单树
					for (MenuTree parentMenu : firstParents) {
						// 第一级
						MenuTree firstMenuTree = new MenuTree();
						BeanUtils.copyProperties(parentMenu, firstMenuTree);
						// 第二级
						TreeSet<MenuTree> secondChildrens = new TreeSet<MenuTree>();
						for (MenuTree secondMenu : secondParents) {
							if (secondMenu.getParentId().longValue() == parentMenu.getId().longValue()) {
								MenuTree secondTree = new MenuTree();
								BeanUtils.copyProperties(secondMenu, secondTree);
								// 第三级
								TreeSet<MenuTree> thirdChildrens = new TreeSet<MenuTree>();
								for (MenuTree thirdMenu : set3) {
									if (thirdMenu.getParentId().longValue() == secondMenu.getId().longValue()) {
										MenuTree thirdTree = new MenuTree();
										BeanUtils.copyProperties(thirdMenu, thirdTree);
										if (thirdTree.getId() != null) {
											thirdChildrens.add(thirdTree);
										}
									}
								}
								secondTree.setChildren(thirdChildrens);
								if (secondTree.getId() != null) {
									secondChildrens.add(secondTree);
								}
							}
						}
						firstMenuTree.setChildren(secondChildrens);
						if (firstMenuTree.getId() != null) {
							menuList.add(firstMenuTree);
						}
					}
					if (menuList != null && menuList.size() > 0) {
						for (MenuTree menuTree : menuList) {
							String id = menuTree.getId().toString();
							// 存储数据到缓存中
							redis.hset(key, id, JSONObject.toJSONString(menuTree));
						}
						redis.setExpire(key, 10 * 24 * 3600);// 每个账号的菜单缓存10天
					}

					// 处理菜单展示顺序
					MenuTree sysManager = null;// 系统管理
					MenuTree customerManager = null;// 客户管理
					MenuTree checkManager = null;// 检测管理
					MenuTree dataCount = null;// 数据统计
					for (MenuTree menuTree : menuList) {
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
					json.put("menuList", menuListSort);

					logger.error("JSONObject 537 getLoginAccountMenu(Account account, Object apiResult)" + json.toJSONString());

				}
			}
		}
		return json;
	}

	public JSONObject getAccountDetail(Long accountId, Account loginAccount, Object apiResult) {
		ApiResult result = (ApiResult) apiResult;
		if (accountId == null) {
			result.setCode(BizCode.PARAM_EMPTY);
			result.setMessage("参数accountId为空");
			return null;
		}
		JSONObject json = new JSONObject();
		Account account = accountMapper.selectByPrimaryKey(accountId);
		if (account != null) {
			json.put("id", accountId);
			json.put("name", account.getName());
			json.put("account", account.getAccount());
			json.put("hospitalId", account.getHospitalId());
			json.put("hospitalGroupId", account.getHospitalGroupId());
			json.put("roleId", account.getRoleId());
			json.put("showFieldIds", account.getShowFieldIds());
			// 密码(需要将原始密码给管理员看)
			JedisUtil redis = JedisUtil.getInstance();
			String key = "lewe_account:" + accountId;
			// 取出原始密码
			String password = redis.hget(key, "password");
			json.put("password", password == null ? account.getPassword() : password);
		}
		return json;
	}
}
