package cn.matcheasy.framework.commonbean;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @class SheetBean
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Data
public class SheetBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * sheet页名称
     */
    private String sheetName;

    /**
     * 字段和别名,如果使用这个, properties 和 titles 可以不用处理
     */
    private Map<String, String> fieldAndAlias;

    /**
     * 字段
     */
    private String[] properties;

    /**
     * 标题/别名
     */
    private String[] titles;

    /**
     * 列宽
     */
    private Integer[] columnWidth;

    /**
     * 数据集
     */
    private Collection<?> collection;

    public Map<String, String> getFieldAndAlias() {
        if (fieldAndAlias == null) {
            this.fieldAndAlias = new LinkedHashMap<String, String>();
            for (int i = 0; i < properties.length; i++) {
                fieldAndAlias.put(properties[i], titles[i]);
            }
        }
        return fieldAndAlias;
    }

    public void setFieldAndAlias(Map<String, String> fieldAndAlias) {
        this.fieldAndAlias = fieldAndAlias;
    }

}
