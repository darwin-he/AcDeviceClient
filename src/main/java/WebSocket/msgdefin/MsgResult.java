package WebSocket.msgdefin;


/**
 * \* Created with IntelliJ IDEA.
 * \* User: hxl
 * \* Date: 2018/12/3
 * \* Time: 14:21
 * \* Description:
 * \
 */
public class MsgResult {
    private MsgRoute msgRoute;
    private int code = CodeEnum.SUCCESS.getCode();
    private String msg = CodeEnum.SUCCESS.getMsg();
    private Object data = "";
    

    public MsgResult() {}

    public MsgResult(MsgRoute msgRoute, int code, String msg) {
        this(msgRoute,code, msg, "");
    }

    public MsgResult(MsgRoute msgRoute, int code, String msg, Object data) {
        this.msgRoute=msgRoute;
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
    
    public MsgRoute getMsgRoute() {
        return msgRoute;
    }
    
    public void setMsgRoute(MsgRoute msgRoute) {
        this.msgRoute = msgRoute;
    }
    
}