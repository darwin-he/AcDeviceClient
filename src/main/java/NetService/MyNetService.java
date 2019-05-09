package NetService;

import NetService.vo.UserCard;

/**
 * @author darwin_he
 * @date 2019/5/9 15:34
 */
public interface MyNetService {
   
    /**
     * 连接到服务器
     * @return
    */
    void connectToService() throws Exception;

    /**
     * 与服务器断开连接
     * @return
    */
    void disConnect();
    
    /**
     * 通过卡片号获取用户信息
     * @param userCard
     * @return
     */
    void getUserByUserCard(UserCard userCard);
    
    /**
     * 发送卡片号给服务器，此函数一般在管理员添加新用户时
     * 客服端通过此函数将未注册的卡片号返回给服务器。
     * @return
     */
    void sendUserCardToService(UserCard userCard);

    /**
     * 发送环境数据给服务器
     * @return
    */
    void sendEnvironmentDateToService();

    /**
     * 发送门禁状态数据给服务器
     * @return
    */
    void sendStateDateToService();

    /**
     * 发送设备信息给服务器
     */
    void sendDeviceInforToService();
}
