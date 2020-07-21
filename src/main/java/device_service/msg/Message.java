package device_service.msg;


/**
 * \* Created with IntelliJ IDEA.
 * \* User: hxl
 * \* Date: 2018/12/3
 * \* Time: 14:21
 * \* Description:
 * \
 */
public class Message {
    private Route route;
    
    private int code = Code.SUCCESS.getCode();
    
    private String msg = Code.SUCCESS.getDesc();
    
    private String data = "";
    
    public Message() {}

    public Message(Route route, int code, String msg) {
        this(route,code, msg, "");
    }

    public Message(Route route, int code, String msg, String data) {
        this.route = route;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Message(Route route, Code code, String data) {
        this.route = route;
        this.code = code.getCode();
        this.msg = code.getDesc();
        this.data = data;
    }

    public Message(Route route, Code code) {
        this.route = route;
        this.code = code.getCode();
        this.msg = code.getDesc();
        this.data = "";
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
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }
    
}