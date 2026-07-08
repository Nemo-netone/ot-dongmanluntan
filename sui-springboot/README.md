1.项目运行方式:
 
 点击idea右上方绿色三角形
 
 2.系统管理员账号密码:
 
 账号: admin 密码: 123456
 
 3.项目登录地址:
 
 http://localhost:8088/login
 
 //角色数据过滤
 if (ShiroUtils.getUserRole().contains("userRole")) {
     sysUser.setId(ShiroUtils.getUserInfo().getId());
 }