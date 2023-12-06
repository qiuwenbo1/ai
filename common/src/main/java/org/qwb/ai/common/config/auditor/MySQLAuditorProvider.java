package org.qwb.ai.common.config.auditor;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import org.qwb.ai.common.constant.AuthConstant;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class MySQLAuditorProvider implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        //从Header中获取用户信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String userStr = request.getHeader("user");
            JSONObject userJsonObject = new JSONObject(userStr);

            if(userJsonObject.get(AuthConstant.USER_ID) == null) {
                return Optional.ofNullable(null);
            }
            return Optional.of(Convert.toLong(userJsonObject.get(AuthConstant.USER_ID)));
        } else {
            return Optional.empty();
        }
    }
    
}