package WebSocket.Local;

import DeviceCenter.ACCDeviceCenter;

/**
 * @author darwin_he
 * @date 2019/5/9 15:34
 */
public interface LocalClient {
   
    /**
     * 连接到服务器
     * @return
    */
    void connectToService(ACCDeviceCenter accDeviceCenter) throws Exception;

    /**
     * 与服务器断开连接
     * @return
    */
    void disConnect();
    
}
