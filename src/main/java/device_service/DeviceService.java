package device_service;

import control_center.ControlCenter;

/**
 * @author darwin_he
 * @date 2019/5/9 15:34
 */
public interface DeviceService {
   
    /**
     * 开启设备服务
     * @return
    */
    void start(ControlCenter controlCenter);

    /**
     * 重启设备服务
     * @param controlCenter
     */
    void restart(ControlCenter controlCenter);

    /**
     * 关闭设备服务
     * @return
    */
    void shutdown();
    
}
