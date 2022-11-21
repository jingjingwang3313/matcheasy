package cn.matcheasy.project.modules.demo.es.entity;

import cn.matcheasy.framework.utils.DateUtil;
import cn.matcheasy.framework.utils.JsonUtil;
import cn.matcheasy.project.common.base.dao.es.AbstractEsBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @class: EsEntity
 * @author: wangjing
 * @date: 2021/9/10/0010
 * @desc: TODO
 */
@Data
public class EsEntity<T> extends AbstractEsBaseEntity implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String id;

    private Integer itg;

    private Long lg;

    private float flt;

    private Double dub;

    private String str;

    private Boolean boo;

    private Date date;

    private List<T> list;

    @Override
    public String getEsEntityId()
    {
        return id;
    }

    @Override
    public String getRouting()
    {
        return null;
    }

    @Override
    public Long getVersion()
    {
        return null;
    }

    @Override
    public String toJson()
    {
        return JsonUtil.toJson(this, DateUtil.FORMAT_yyyyMMddHHmmssSSS);
    }
}
