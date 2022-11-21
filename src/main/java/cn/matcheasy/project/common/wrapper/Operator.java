package cn.matcheasy.project.common.wrapper;

/**
 * @class: Operator
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: 操作
 */
public interface Operator
{

    /**
     * 操作符：等于
     */
    int EQUAL = 0;
    /**
     * 操作符：不等于
     */
    int NOT_EQUAL = 1;
    /**
     * 操作符：区间
     */
    int BETWEEN = 2;
    /**
     * 操作符：大于
     */
    int GREATER = 3;
    /**
     * 操作符：小于
     */
    int LESS = 4;
    /**
     * 操作符：in
     */
    int IN = 5;
    /**
     * 操作符：大于等于
     */
    int GREATER_EQUAL = 6;
    /**
     * 操作符：小于等于
     */
    int LESS_EQUAL = 7;
    /**
     * 操作符：not in
     */
    int NOT_IN = 8;
    /**
     * 操作符：like
     */
    int LIKE = 9;
    /**
     * 操作符：not like
     */
    int NOT_LIKE = 10;

}
