package cn.matcheasy.framework.exception;

/**
 * @class: BusinessException
 * @author: wangjing
 * @date: 2021/11/26/0026
 * @desc: TODO
 */
public class BusinessException extends RuntimeException
{
    private String success;

    private String msg;

    public BusinessException()
    {
        super();
    }

    public BusinessException(String msg)
    {
        super(msg);
        this.success = "false";
        this.msg = msg;
    }

    public BusinessException(String msg, Throwable e)
    {
        super(msg, e);
        this.success = "false";
        this.msg = msg;
    }

    public BusinessException(String success, String msg)
    {
        super(msg);
        this.success = success;
        this.msg = msg;
    }

    public String getSuccess()
    {
        return success;
    }

    public void setSuccess(String success)
    {
        this.success = success;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}

