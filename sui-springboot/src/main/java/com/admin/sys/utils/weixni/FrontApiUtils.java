package com.admin.sys.utils.weixni;

import com.admin.sys.base.entity.UserToken;
import com.admin.sys.utils.admin.IdGen;
import com.admin.sys.utils.admin.StringUtils;
import com.admin.sys.utils.jwt.JwtUtils;
import net.sf.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 网站前台工具类
 */
public class FrontApiUtils {

    //token设置值
    public static String getJscode2session(String code, String from) {
        Map<String, String> map = new HashMap<>();
        String openid = "";
        if (StringUtils.isEmpty(code)) {
            openid = IdGen.primaryKey();
        } else { //token设置值
            byte[] decoded = Base64.getDecoder().decode(code);
            openid = new String(decoded);//Base64 解密
        }
        map.put("openid", openid);
        map.put("unionid", "suifeng");
        map.put("sessionKey", code);
        map.put("from", from);
        String token = JwtUtils.generateToken(map);
        return token;
    }

}
