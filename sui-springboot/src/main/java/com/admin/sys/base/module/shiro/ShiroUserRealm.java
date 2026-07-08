package com.admin.sys.base.module.shiro;

import com.admin.sys.base.cache.service.SysCacheService;
import com.admin.sys.base.constant.AdminConst;
import com.admin.sys.base.module.menu.dao.SysMenuDao;
import com.admin.sys.base.module.menu.entity.SysMenu;
import com.admin.sys.base.module.office.dao.SysOfficeDao;
import com.admin.sys.base.module.office.entity.SysOffice;
import com.admin.sys.base.module.role.dao.SysRoleDao;
import com.admin.sys.base.module.role.entity.SysRole;
import com.admin.entity.SysUser;
import com.admin.service.SysUserService;
import com.admin.sys.utils.admin.ObjectUtils;
import com.admin.sys.utils.admin.ShiroUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证
 *
 * @author suifeng
 * @date 2016年11月10日 上午11:55:49
 */
public class ShiroUserRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysOfficeDao sysOfficeDao;
    @Autowired
    private SysCacheService sysCacheService;
//    @Autowired
      //@Lazy
//    private SysUserRoleService sysUserRoleService;
    //至此，我们以将shiro和ehcache集成完毕，使用的是shiro框架提供的缓存管理器，有个需要特别注意的是，UserRealm里注入的SysUserService等service，需要延迟注入，所以都要添加@Lazy注解(如果不加需要自己延迟注入)，否则会导致该service里的@Cacheable缓存注解、@Transactional事务注解等失效

    public final static String REALM_NAME = "shiroCasRealm";
    public ShiroUserRealm(){
        setName(REALM_NAME);
    }

    /**
     * 登陆认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String loginName = token.getUsername();
        ByteSource salt = ByteSource.Util.bytes(AdminConst.SALT);
        SysUser params = new SysUser();
        params.setLoginName(loginName);
        SysUser sysUser = sysUserService.get(params);
        if (ObjectUtils.isNull(sysUser)) {
            throw new UnknownAccountException();
        }
        if (sysUser.getDisabled() == 1) {
            throw new DisabledAccountException();
        }
        //设置角色
        List<SysRole> roleList = sysRoleDao.findRolesListByUserId(sysUser.getId());
        sysUser.setRoleList(roleList);
        //设置机构
        SysOffice office = sysOfficeDao.selectById(sysUser.getOfficeId());
        sysUser.setOffice(office);
        //设置权限
        Set<SysMenu> menuSet = sysMenuDao.findPermissionsByUserId(sysUser.getId());
        Set<String> permsSet = new HashSet<>(menuSet.stream().map(SysMenu::getPermission).collect(Collectors.toList()));
        //设置缓存
        ShiroUtils.setSession(AdminConst.USER_INFO, sysUser);
        ShiroUtils.setSession(AdminConst.USER_PERMISSION, permsSet);
        ShiroUtils.setSession(AdminConst.USER_MENU, menuSet);//给日志匹配用
        sysCacheService.init();
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(sysUser, sysUser.getPassword(), salt, getName());
        return simpleAuthenticationInfo;
    }

    /**
     * 角色认证
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysUser sysUser = (SysUser)principals.getPrimaryPrincipal();
        Set<String> permsSet = (Set<String>) ShiroUtils.getSession(AdminConst.USER_PERMISSION);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        List<String> collect = sysUser.getRoleList().stream().map(SysRole::getRoleCode).collect(Collectors.toList());
        Set<String> roleList = new HashSet<>(collect);
        info.setRoles(roleList);
        return info;
    }

    /**
     * 更新用户授权信息缓存.
     */
    public void clearCachedAuthorizationInfo(String principal) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清除所有用户授权信息缓存.
     */
    public void clearAllCachedAuthorizationInfo() {
        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null) {
            for (Object key : cache.keys()) {
                cache.remove(key);
            }
        }
    }


}
