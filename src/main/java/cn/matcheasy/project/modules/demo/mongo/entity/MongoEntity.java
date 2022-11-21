package cn.matcheasy.project.modules.demo.mongo.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @class: MongoEntity
 * @author: wangjing
 * @date: 2021/9/10/0010
 * @desc: TODO
 */
@Data
public class MongoEntity<T> implements Serializable
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

}
