package cn.matcheasy.framework.result;

import cn.hutool.core.convert.Convert;
import cn.matcheasy.framework.constant.ProjectConst;
import cn.matcheasy.framework.utils.ThreadValueUtil;
import cn.matcheasy.framework.utils.UUIDUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @class Result
 * @author: wangjing
 * @date: 2020/11/7/0007
 * @desc: TODO
 */
@ApiModel(value = "API接口返回对象")
public class Result implements Serializable
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志追踪码")
    private String traceId;

    @ApiModelProperty(value = "响应成功标识 (true-成功，false-失败)")
    private String success;

    @ApiModelProperty(value = "响应码 (-1-请求失败，200-请求成功，404-请求url错误，405-access_token过期，406-access_token参数缺失，500-服务器内部错误)")
    private String code;

    @ApiModelProperty(value = "响应信息")
    private String msg;

    @ApiModelProperty(value = "接口具体返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    public Result()
    {
    }

    public Result(ResultEnum response)
    {
        this.success = response.getSuccess();
        this.code = response.getCode();
        this.msg = response.getMsg();
    }

    public String getTraceId()
    {
        return traceId;
    }

    public void setTraceId(String traceId)
    {
        this.traceId = traceId;
    }

    public String getSuccess()
    {
        return success;
    }

    public void setSuccess(String success)
    {
        this.success = success;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public Map getData()
    {
        return data;
    }

    public void setData(Map data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return JSONObject.toJSONString(this);
    }

    /**
     * 成功
     */
    public static Result success()
    {
        Result result = new Result();
        result.setTraceId(Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));
        result.setSuccess(ResultEnum.SUCCESS.getSuccess());
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        return result;
    }

    /**
     * 自定义成功信息
     */
    public static Result success(String msg)
    {
        Result result = new Result();
        result.setTraceId(Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));
        result.setSuccess(ResultEnum.SUCCESS.getSuccess());
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(msg);
        return result;
    }

    /**
     * 成功,返回数据
     */
    public static Result success(Map data)
    {
        Result result = new Result();
        result.setTraceId(Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));
        result.setSuccess(ResultEnum.SUCCESS.getSuccess());
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    /**
     * 失败
     */
    public static Result error(ResultEnum resultEnum)
    {
        Result result = new Result();
        result.setTraceId(Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));
        result.setSuccess(resultEnum.getSuccess());
        result.setCode(resultEnum.getCode());
        result.setMsg(resultEnum.getMsg());
        return result;
    }

    /**
     * 失败
     */
    public static Result error(String code, String msg)
    {
        Result result = new Result();
        result.setTraceId(Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));
        result.setSuccess(ResultEnum.BIZ_ERROR.getSuccess());
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败
     */
    public static Result error(String msg)
    {
        Result result = new Result();
        result.setTraceId(Convert.toStr(ThreadValueUtil.get(ProjectConst.TRACE_ID), UUIDUtil.fastSimpleUUID()));
        result.setSuccess(ResultEnum.BIZ_ERROR.getSuccess());
        result.setCode("-1");
        result.setMsg(msg);
        return result;
    }

}
