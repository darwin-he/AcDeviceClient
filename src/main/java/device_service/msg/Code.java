package device_service.msg;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: hxl
 * \* Date: 2018/12/3
 * \* Time: 13:00
 * \* Description:
 * \
 */
public enum Code {
    SUCCESS(0, "成功！"),
    DEFALUT(-1,"失败！"),
    OPENDOOR(2,"开门！"),
    OPENDOOR_DEFAULT(3,"开门失败！"),
    OPENDOOR_SUCCESS(4,"开门成功！"),
    CLOSEDOOR(5,"关门！"),
    CLOSEDOOR_DEFAULT(6,"关门失败！"),
    CCLOSEDOOR_SUCCESS(7,"关门成功！"),
    RESET_DEVICE(9,"复位设备！"),
    RESET_DEVICE_DEFAULT(10,"复位设备失败！"),
    RESET_DEVICE_SUCCESS(11,"复位设备成功！"),
    CLEAN_LOG(12,"清理日志！"),
    CLEAN_LOG_DEFAULT(13,"清理日志失败！"),
    CLEAN_LOG_SUCCESS(14,"清理日志成功！"),
    UPLOAD_CARD(15,"上传卡片数据!"),
    UPLOAD_CARD_DEFAULT(16,"上传卡片数据失败！"),
    UPLOAD_CARD_SUCCESS(17,"上传卡片数据成功！"),
    UPLOAD_IDENTITY_INFO(18,"上传身份信息！"),
    UPLOAD_IDENTITY_INFO_DEFAULT(19,"上传身份信息失败！"),
    UPLOAD_IDENTITY_INFO_SUCCESS(20,"上传身份信息成功！"),
    UPLOAD_ENVIRODATE(21,"上传环境数据！"),
    UPLOAD_ENVIRODATE_DEFAULT(22,"上传环境数据失败！"),
    UPLOAD_ENVIRODATE_SUCCESS(23,"上传环境数据成功！"),
    UPLOAD_DOOR_STATE(24,"上传门禁状态数据！"),
    UPLOAD_DOOR_STATE_DEFAULT(25,"上传门禁状态数据失败！"),
    UPLOAD_DOOR_STATE_SUCCESS(26,"上传门禁状态数据成功！"),
    ACCEPT_ADVERTISMENT(27,"广告数据！"),
    ACCEPT_PUSHMSG(28,"推送消息！"),
    CHECK_USERINFO(30,"获取用户信息！"),
    CHECK_USERINFO_DEFAULT(31,"获取用户信息失败！"),
    CHECK_USERINFO_SUCCESS(32,"获取用户信息成功！"),
    UPLOAD_DEVICE_INFO(33,"上传设备信息！"),
    UPLOAD_DEVICE_INFO_SUCCESS(34,"上传设备信息成功！"),
    UPLOAD_DEVICE_INFO_DEFAULT(35,"上传设备信息失败！");
    
    private final int code;
    private final String desc;

    Code(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(int code) {
        for (Code item : values()) {
            if (item.getCode() == code) {
                return item.getDesc();
            }
        }
        return "";
    }

}