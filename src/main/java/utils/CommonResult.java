package utils;


/**
 * \* Created with IntelliJ IDEA.
 * \* User: hxl
 * \* Date: 2018/12/3
 * \* Time: 14:21
 * \* Description:
 * \
 */
public class CommonResult {
    private int code = DeviceCodeEnum.SUCCESS.getCode();
    private String msg = DeviceCodeEnum.SUCCESS.getMsg();
    private Object data = "";

    public CommonResult() {}

    public CommonResult(Object data) {
        this(DeviceCodeEnum.SUCCESS, data);
    }

    public CommonResult(DeviceCodeEnum codeEnum, Object data) {
        this(codeEnum.getCode(), codeEnum.getMsg(), data);
    }

    public CommonResult(DeviceCodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getMsg());
    }

    public CommonResult(int code, String msg) {
        this(code, msg, "");
    }

    public CommonResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    
}