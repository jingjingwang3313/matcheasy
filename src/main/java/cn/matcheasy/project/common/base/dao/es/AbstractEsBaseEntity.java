package cn.matcheasy.project.common.base.dao.es;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @class: AbstractEsBaseEntity
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: TODO
 */
public abstract class AbstractEsBaseEntity
{
    public AbstractEsBaseEntity()
    {
    }

    @JSONField(serialize = false)
    public abstract String getEsEntityId();

    @JSONField(serialize = false)
    public abstract String getRouting();

    @JSONField(serialize = false)
    public abstract Long getVersion();

    public abstract String toJson();
}
