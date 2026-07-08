package com.admin.sys.base.module.shiro.listener;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * Created by suifeng
 */
public class MySessionListener implements SessionListener {

//    private Logger logger = Logger.getLogger(MySessionListener.class);
//
//    @Autowired
//    private SystemNoticeService systemNoticeService;

    @Override
    public void onStart(Session session) {
        //logger.info("会话创建：" + session.getId());
    }

    @Override
    public void onStop(Session session) {
        System.out.println("退出会话：" + session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        //Object userObject = session.getAttribute(WebAppConst.SESSION_USER_KEY);
        //if(userObject != null){
        //    SysUser user = (SysUser)userObject;
        //    Message message = new Message();
        //    message.setAuthToken(user.getId());
        //    message.setToUserId(user.getId());
        //    message.setToUserName(user.getUserName());
        //    message.setMsgType(MessageType.SESSION_TIMEOUT.getValue());
        //    message.setMsgTypeName(MessageType.SESSION_TIMEOUT.getName());
        //    message.setToAll(false);
        //    message.setContent("会话过期，请重新登录！");
        //    systemNoticeService.sendSysNotice(message);
        //}
    }
}
