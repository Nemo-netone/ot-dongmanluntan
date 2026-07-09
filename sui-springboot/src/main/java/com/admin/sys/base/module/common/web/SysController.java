package com.admin.sys.base.module.common.web;

import com.admin.sys.base.cache.service.SysCacheService;
import com.admin.sys.base.constant.AppletConst;
import com.admin.sys.base.module.extend.web.BaseController;
import com.admin.sys.base.entity.ResultInfo;
import com.admin.sys.base.properteis.FileUploadProperteis;
import com.admin.sys.base.module.common.entity.Unique;
import com.admin.sys.base.module.common.service.CommonService;
import com.admin.sys.base.module.menu.entity.SysMenu;
import com.admin.sys.base.module.menu.service.SysMenuService;
import com.admin.sys.base.module.role.entity.SysRole;
import com.admin.sys.base.module.role.service.SysRoleService;
import com.admin.sys.utils.admin.ShiroUtils;
import com.admin.sys.utils.admin.StringUtils;
import com.admin.entity.SysUser;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.admin.sys.base.module.config.service.SysConfigService.getSysConfig;

/**
 * 系统控制器
 */
@RestController
@RequestMapping("/api/sys")//该地址需要代理需要api
public class SysController extends BaseController {
    private static final String DEFAULT_PROJECT_DOMAIN = "https://ot-dongmanluntan-api-273280-7-1369167244.sh.run.tcloudbase.com";

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysCacheService sysCacheService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private FileUploadProperteis fileUploadProperteis;

    //唯一校验
    @RequestMapping("/unique")
    public ResultInfo unique(HttpServletRequest request,Unique unique) {
        boolean uniqueFlag = commonService.unique(unique);//false：代表不唯一
        if(uniqueFlag){
            return ResultInfo.ok("是唯一标识");
        }else{
            return ResultInfo.error("唯一项不能重复");
        }
    }
    //获取admin系统配置
    @RequestMapping("/getAdminConfig")
    public ResultInfo getAdminConfig(){
        Map map = new HashMap();
        String projectDomain = System.getenv().getOrDefault("PROJECT_DOMAIN", getSysConfig("projectDomain"));
        if (StringUtils.isEmpty(projectDomain) || projectDomain.contains("127.0.0.1") || projectDomain.contains("localhost")) {
            projectDomain = DEFAULT_PROJECT_DOMAIN;
        }
        SysRole sysRole = new SysRole();
        sysRole.setDelFlag(0);
        List<SysRole> allRole = sysRoleService.getList(sysRole);
        map.put("allRole",allRole);
        map.put("fileBasePath",projectDomain+fileUploadProperteis.getFileBasePath());
        map.put("projectDomain",projectDomain);
        map.put("projectName",getSysConfig("projectName"));
        map.put("isHorizontal","1".equals(getSysConfig("isHorizontal")));
        map.put("showRegister","1".equals(getSysConfig("showRegister")));
        map.put("showRoles","1".equals(getSysConfig("showRoles")));
        map.put("showValidCode","1".equals(getSysConfig("showValidCode")));
        return ResultInfo.ok("获取配置成功！",map);
    }

    //获取微信系统配置
    @RequestMapping("/getAppletConfig")
    public ResultInfo getAppletConfig(){
        Map map = new HashMap();
        String[] checkRoutes = getSysConfig(AppletConst.CHECK_ROUTES).split("\\|");
        map.put("fileBasePath",fileUploadProperteis.getFileBasePath());
        map.put("isCheckLogin","1".equals(getSysConfig("isCheckLogin")));
        map.put("checkRoutes",checkRoutes);
        map.put("projectName",getSysConfig("projectName"));
        map.put("projectDomain",getSysConfig("projectDomain"));
        return ResultInfo.ok("获取配置成功！",map);
    }

    //获取微信系统配置
    @RequestMapping("/getOfficialConfig")
    public ResultInfo getOfficialConfig(){
        Map map = new HashMap();
        String[] checkRoutes = getSysConfig(AppletConst.CHECK_ROUTES).split("\\|");
        map.put("fileBasePath",fileUploadProperteis.getFileBasePath());
        map.put("projectDomain",getSysConfig("projectDomain"));
        map.put("isCheckLogin","1".equals(getSysConfig("isCheckLogin")));
        map.put("checkRoutes",checkRoutes);
        return ResultInfo.ok("获取配置成功！",map);
    }

    //获取数据字典
    @RequestMapping("/getDictList")
    public ResultInfo getEhCacheList() {
        Map<String,Object> dictMap = sysCacheService.initDictCache();
        return ResultInfo.ok("获取数据字典成功",dictMap);
    }

    //获取菜单树
    @RequestMapping("/getMenuUserTree")
    public ResultInfo getMenuUserTree() {
        SysUser currentUser = ShiroUtils.getUserInfo();
        if(StringUtils.isNotNull(currentUser)){
            List<SysMenu> menuTree = sysMenuService.getMenuUserTree("1", currentUser.getId()); //查询出菜单栏下的菜单
            for (SysMenu menuTreeF : menuTree) {
                List<SysMenu> treeChild = sysMenuService.getMenuUserTree(menuTreeF.getId(), currentUser.getId()); //查询对应菜单栏下的资源
                menuTreeF.setChildren(treeChild);
            }
            return ResultInfo.ok("查询成功",menuTree);
        }else{
            return ResultInfo.error("系统过期,请重新登录!");
        }
    }

    //查询路由
    @RequestMapping("/getUserRoutes")
    public ResultInfo getUserRoutes() {
        SysUser currentUser = ShiroUtils.getUserInfo();
        if(StringUtils.isNotNull(currentUser)){
            List<SysMenu> menuTree = sysMenuService.getMenuUserTree("1", currentUser.getId()); //查询出菜单栏下的菜单
            for (SysMenu menuTreeF : menuTree) {
                List<SysMenu> treeChild = sysMenuService.getMenuUserTree(menuTreeF.getId(), currentUser.getId()); //查询对应菜单栏下的资源
                menuTreeF.setChildren(treeChild);
            }
            List<JSONObject> userRoutes = sysMenuService.menuFormatRoute(new ArrayList<>(), menuTree);
            if(userRoutes.size()==0){//当前角色没有配置菜单权限
                throw new AuthorizationException("当前用户没有配置权限,请联系管理员");
            }
            return ResultInfo.ok("查询成功",userRoutes);
        }else{
            return ResultInfo.error("系统过期,请重新登录!");
        }
    }

    //shiro重定向需要设置跨域，前端才能正确响应。
    @CrossOrigin
    @RequestMapping("/author")
    public String login(HttpServletRequest request) {
        throw new SessionException("会话过期，请重新登录！");
    }

    @RequestMapping("/error")//权限异常BaseController可以自己捕获
    public String error(HttpServletRequest request) {
        throw new UnauthorizedException("权限不足，请联系管理员！");
    }

}



