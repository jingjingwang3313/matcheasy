package cn.matcheasy.framework.config.mybatisplus;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.utils.ComU;
import cn.matcheasy.framework.utils.JwtUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @class CommonFieldHandler
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: MyBatisPlus 提供字段自动填充功能 @TableField 注解,需要加在需要自动填充的字段上
 * 1. @TableField(fill = FieldFill.INSERT) // 插入的时候自动填充
 * 2. @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新的时候自动填充
 * 注: 逻辑删除配置,@TableLogic 注解用于设置逻辑删除属性
 */
@Slf4j
@Component
public class CommonFieldHandler implements MetaObjectHandler
{

    /**
     * 插入时的自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject)
    {
        log.info("insert开始属性填充");
        /**
         * metaObject：元对象，就是方法的入参
         * fieldName：为哪个属性进行自动填充
         * fieldType：属性的类型
         * fieldVal：需要填充的属性值
         */
        boolean hasCreateBy = metaObject.hasSetter("createBy");
        if (hasCreateBy)
        {
            Object createBy = this.getFieldValByName("createBy", metaObject);
            if (ObjectUtil.isEmpty(createBy))
            {
                this.strictInsertFill(metaObject, "createBy", String.class, getUserId());
            }
        }
        boolean hasCreateTime = metaObject.hasSetter("createTime");
        if (hasCreateTime)
        {
            Object createTime = this.getFieldValByName("createTime", metaObject);
            if (ObjectUtil.isEmpty(createTime))
            {
                this.strictInsertFill(metaObject, "createTime", Date.class, DateUtil.date());
            }
        }
        boolean hasUpdateBy = metaObject.hasSetter("updateBy");
        if (hasUpdateBy)
        {
            Object updateBy = this.getFieldValByName("updateBy", metaObject);
            if (ObjectUtil.isEmpty(updateBy))
            {
                this.strictInsertFill(metaObject, "updateBy", String.class, getUserId());
            }
        }
        boolean hasUpdateTime = metaObject.hasSetter("updateTime");
        if (hasUpdateTime)
        {
            Object updateTime = this.getFieldValByName("updateTime", metaObject);
            if (ObjectUtil.isEmpty(updateTime))
            {
                this.strictInsertFill(metaObject, "updateTime", Date.class, DateUtil.date());
            }
        }
    }

    /**
     * 更新时的自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject)
    {
        log.info("update开始属性填充");
        /**
         * metaObject：元对象，就是方法的入参
         * fieldName：为哪个属性进行自动填充
         * fieldType：属性的类型
         * fieldVal：需要填充的属性值
         */
        boolean hasUpdateBy = metaObject.hasSetter("updateBy");
        if (hasUpdateBy)
        {
            Object updateBy = this.getFieldValByName("updateBy", metaObject);
            if (ObjectUtil.isEmpty(updateBy))
            {
                this.strictInsertFill(metaObject, "updateBy", String.class, getUserId());
            }
        }
        boolean hasUpdateTime = metaObject.hasSetter("updateTime");
        if (hasUpdateTime)
        {
            Object updateTime = this.getFieldValByName("updateTime", metaObject);
            if (ObjectUtil.isEmpty(updateTime))
            {
                this.strictInsertFill(metaObject, "updateTime", Date.class, DateUtil.date());
            }
        }
    }

    /**
     * 获取userId
     */
    public String getUserId()
    {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Map requestMap = ComU.getRequestMap(request);
        String access_token = MapUtil.getStr(requestMap, ProjectConst.ACCESS_TOKEN);
        if (StringUtils.isNotBlank(access_token))
        {
            long userId = JwtUtil.getUserId(access_token);
            return String.valueOf(userId);
        }
        else
        {
            return "";
        }
    }

}

