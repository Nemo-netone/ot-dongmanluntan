package com.admin.controller;

import com.admin.entity.SysUser;
import com.admin.service.SysUserService;
import com.admin.sys.base.annotation.LogField;
import com.admin.sys.base.constant.AdminConst;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.sys.base.module.role.entity.SysRole;
import com.admin.sys.base.module.role.service.SysRoleService;
import com.admin.sys.utils.admin.ShiroUtils;
import com.admin.sys.utils.admin.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/login")
public class AdminLoginController extends BaseController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    /**
     * pc用户登录
     * @param entity
     * @return
     */
    @RequestMapping("/userLogin")
    @LogField(moduleName="用户登录")
    public ResultInfo userLogin(SysUser entity, HttpServletRequest request) {
        if (StringUtils.isEmpty(entity.getLoginName())) {
            return ResultInfo.error("登录账号不能为空！");
        }
        if (StringUtils.isEmpty(entity.getPassword())) {
            return ResultInfo.error("登录密码不能为空！");
        }
        try {
            Subject currentUser = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(entity.getLoginName(), entity.getPassword());
            currentUser.login(token);
            SysUser sysUser = ShiroUtils.getUserInfo();
            if (StringUtils.isNotEmpty(entity.getRoleCode())) {
                List<String> roleList = sysUser.getRoleList().stream().map(SysRole::getRoleCode).collect(Collectors.toList());
                boolean contains = roleList.contains(entity.getRoleCode());
                if (!contains) {
                    return ResultInfo.error("没有找到该角色用户");
                }
            }
            return ResultInfo.ok("登陆成功！", sysUser);
        } catch (UnknownAccountException uae) { //用户名未知
            return ResultInfo.error("该用户不存在");
        } catch (IncorrectCredentialsException ice) {//凭据不正确，例如密码不正确
            return ResultInfo.error("账号或者密码不正确！");
        } catch (DisabledAccountException ae) {
            return ResultInfo.error("此帐号已经被禁止登录，请联系管理员！");
        }
    }

    /**
     * 用户注册
     * @return
     */
    @RequestMapping("/register")
    public ResultInfo register(SysUser entity) {
        //根据登录账号判断是否有用户
        if (StringUtils.isEmpty(entity.getLoginName())) {
            return ResultInfo.error("登录账号不能为空！");
        }
        if (StringUtils.isEmpty(entity.getPassword())) {
            return ResultInfo.error("登录密码不能为空！");
        }
        SysUser nameRegister = sysUserService.findByLoginName(entity.getLoginName());
        if (nameRegister != null) {//已经被注册过
            return ResultInfo.error("注册失败,该登录账号已经被注册过");
        }
        ByteSource salt = ByteSource.Util.bytes(AdminConst.SALT);
        String passwordMd5 = new SimpleHash("MD5", entity.getPassword(), ByteSource.Util.bytes(salt), 1024).toString();
        entity.setPassword(passwordMd5);
        sysUserService.insert(entity);
        //更新当前用户所有角色
        SysRole sysRole = new SysRole();
        sysRole.setRoleCode("userRole");
        List<SysRole> list = sysRoleService.getList(sysRole);
        entity.setRoleList(list);
        sysUserService.updateUserRoles(entity);
        return ResultInfo.ok("注册成功！");
    }

    /**
     * 获取注册信息
     * @return
     */
    @RequestMapping("/getUserInfo")
    public ResultInfo getUserInfo() {
        String currentUserId = ShiroUtils.getUserInfo().getId();
        if (!StringUtils.isEmpty(currentUserId)) {
            SysUser userInfo = sysUserService.get(currentUserId);
            List<SysRole> roleList = sysRoleService.findRolesListByUserId(currentUserId);
            userInfo.setRoleList(roleList);
            return ResultInfo.ok("获取成功！", userInfo);
        }
        return ResultInfo.ok("获取失败！");
    }

    /**
     * 获取用户登录信息
     * @return
     */
    @RequestMapping("/getLoginInfo")
    public ResultInfo getLoginInfo() {
        //判断是否有基础信息
        Map<String, Object> datas = new HashMap<>();
        SysUser userInfo = ShiroUtils.getUserInfo();
        List<String> roles = ShiroUtils.getUserRole();
        Set<String> permissions = ShiroUtils.getPermissions();
        datas.put("userInfo", userInfo);
        datas.put("roles", roles);
        datas.put("permissions", permissions);
        return ResultInfo.ok("获取登录信息成功！", datas);
    }

    /**
     * 退出登录
     * @return
     */
    @LogField(moduleName="退出登录")
    @RequestMapping("/loginOut")
    public ResultInfo loginOut() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            currentUser.logout();//会自动清空session
        }
        return ResultInfo.ok("退出成功！");
    }
}