package device_service;

import control_center.ControlCenter;
import device_service.dto.*;
import device_service.msg.Code;
import device_service.msg.Message;
import device_service.msg.Route;
import com.alibaba.fastjson.JSON;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.LogUtil;

import java.net.URI;

/**
 * @author darwin_he
 * @date 2019/5/9 15:58
 */
public class DeviceServiceImpl extends WebSocketClient implements DeviceService {
    //消息路由
    private Route toService;
    private Route toAdmin;

    //硬件控制中心
    private ControlCenter controlCenter;

    //环境数据采集线程
    private boolean collectEnviroData = false;
    private Thread collectThread = null;

    public DeviceServiceImpl(URI serverUri) {
        super(serverUri);
    }



    /**
     * 开启设备服务
     *
     * @return
     */
    @Override
    public void start(ControlCenter controlCenter) {
        this.controlCenter = controlCenter;

        toService = new Route();
        toService.setTo("LocalService");
        toService.setFrom("D" + controlCenter.getDeviceNumber());

        toAdmin = new Route();
        toAdmin.setFrom("D"+controlCenter.getDeviceNumber());
        toAdmin.setTo("A"+controlCenter.getDeviceNumber());

        controlCenter.enableHumanHandle(null, null);

        controlCenter.startAutoSearchCard(userCard -> controlCenter.openDoor(null, null),
                userCard -> controlCenter.openDoor(null, null));

        connect();
    }

    /**
     * 重启设备服务
     *
     * @param controlCenter
     */
    @Override
    public void restart(ControlCenter controlCenter) {

    }

    /**
     * 关闭设备服务
     *
     * @return
     */
    public void shutdown() {
        close();
    }




    /**
     * WebSocket连接建立时的回调函数
     *
     * @param serverHandshake
     */
    public void onOpen(ServerHandshake serverHandshake) {
        //主动进行身份验证
        try {
            Thread.sleep(500);
            Message sendData = new Message(toService, Code.UPLOAD_IDENTITY_INFO, null);
            sendMessage(sendData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket收到消息时的回调函数
     *
     * @param msg
     */
    public void onMessage(String msg) {
        Message message = JSON.parseObject(msg).toJavaObject(Message.class);
        Route backRoute = overturnRoute(message.getRoute());
        int code = message.getCode();
        //用户识别成功
        if (code == Code.CHECK_USERINFO_SUCCEDSS.getCode()) {
            onUserCheckedToOpenDoor(message);
            return;
        }
        //读卡片指令
        if (code == Code.UPLOAD_CARD.getCode()) {
            readCardCommand(backRoute);
            return;
        }
        //开门
        if (code == Code.OPENDOOR.getCode()) {
            openDoorCommand(message);
            return;
        }
        //关门
        if (code == Code.CLOSEDOOR.getCode()) {
            closeDoorCommand(message);
            return;
        }
        //与服务器连接成功并进行了身份验证
        if (code == Code.UPLOAD_IDENTITY_INFO_SUCCESS.getCode()) {
            onDeviceAuthPassed();
            return;
        }
        //复位设备
        if (code == Code.RESET_DEVICE.getCode()) {
            resetDeviceCommand(backRoute);
            return;
        }
        //清理日志
        if (code == Code.CLEAN_LOG.getCode()) {
            cleanMemoryCommand(backRoute);
            return;
        }
        //上传门禁状态信息
        if (code == Code.UPLOAD_DOOR_STATE.getCode()) {
            uploadDeviceStateCommand(backRoute);
            return;
        }
        //上传环境数据
        if (code == Code.UPLOAD_ENVIRODATE.getCode()) {
            uploadEnvironmentDateCommand(backRoute);
            return;
        }
        //上传设备信息
        if (code == Code.UPLOAD_DEVICE_INFO.getCode()) {
            uploadDeviceInfoCommand(backRoute);
        }
    }

    /**
     * WebSocket连接断开时的回调函数
     *
     * @param i code
     * @param s 关闭原因
     * @param b 远程
     */
    public void onClose(int i, String s, boolean b) {
        stopCollectEnviroData();
        controlCenter.enableHumanHandle(null, null);
        controlCenter.startAutoSearchCard(userCard -> controlCenter.openDoor(null, null), userCard -> controlCenter.openDoor(null, null));
        new Thread(() -> {
            while (isClosed()) {
                reconnect();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * WebSocket发生错误时的回调函数
     *
     * @param e
     */
    public void onError(Exception e) { }




    /**
     * 设备身份验证通过时响应函数
     */
    private void onDeviceAuthPassed() {
        controlCenter.enableHumanHandle(isSucceed -> {
            Message sendData;
            Handle handle = new Handle(new UserCard("钥匙"), "进", controlCenter.getDeviceNumber());
            if (isSucceed) {
                sendData = new Message(toService, Code.OPENDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.OPENDOOR_DEFAULT);
            }
            sendData.setData(handle);
            sendMessage(sendData);
        }, isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(toService, Code.CCLOSEDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.CLOSEDOOR_DEFAULT);
            }
            sendMessage(sendData);
        });

        controlCenter.startAutoSearchCard(userCard -> {
            Handle handle = new Handle(userCard, "进", controlCenter.getDeviceNumber());
            checkUserInfoOnOpenDoor(handle);
        }, userCard -> {
            Handle handle = new Handle(userCard, "出", controlCenter.getDeviceNumber());
            checkUserInfoOnOpenDoor(handle);
        });

        startCollectEnviroData();
    }

    /**
     * 用户身份校验通过
     *
     * @param message
     */
    private void onUserCheckedToOpenDoor(Message message) {
        Handle handle = (Handle) message.getData();
        controlCenter.openDoor(isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(toService, Code.OPENDOOR_SUCCESS, handle);
            } else {
                sendData = new Message(toService, Code.OPENDOOR_DEFAULT, handle);
            }
            sendMessage(sendData);
        }, isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(toService, Code.CCLOSEDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.CLOSEDOOR_DEFAULT);
            }
            sendMessage(sendData);
        });
    }

    private void readCardCommand(Route backRoute) {
        controlCenter.forbidHumanHandle();

        //searchCardOnce()函数会禁用自动寻卡功能
        UserCard userCard = controlCenter.searchCardOnce();
        sendUserCardToAdmin(backRoute, userCard);

        controlCenter.enableHumanHandle(isSucceed -> {
            Message sendData;
            Handle handle = new Handle(new UserCard("钥匙"), "进", controlCenter.getDeviceNumber());
            if (isSucceed) {
                sendData = new Message(toService, Code.OPENDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.OPENDOOR_DEFAULT);
            }
            sendData.setData(handle);
            sendMessage(sendData);
        }, isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(toService, Code.CCLOSEDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.CLOSEDOOR_DEFAULT);
            }
            sendMessage(sendData);
        });

        controlCenter.startAutoSearchCard(userCard1 -> {
            Handle handle = new Handle(userCard1, "进", controlCenter.getDeviceNumber());
            checkUserInfoOnOpenDoor(handle);
        }, userCard12 -> {
            Handle handle = new Handle(userCard12, "出", controlCenter.getDeviceNumber());
            checkUserInfoOnOpenDoor(handle);
        });
    }

    private void openDoorCommand(Message message) {
        Route backRoute = overturnRoute(message.getRoute());
        Handle handle = (Handle) message.getData();
        controlCenter.openDoor(isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(backRoute, Code.OPENDOOR_SUCCESS, handle);
            } else {
                sendData = new Message(backRoute, Code.OPENDOOR_DEFAULT, handle);
            }
            sendMessage(sendData);
        }, isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(backRoute, Code.CCLOSEDOOR_SUCCESS);
            } else {
                sendData = new Message(backRoute, Code.CLOSEDOOR_DEFAULT);
            }
            sendMessage(sendData);
        });
    }

    private void closeDoorCommand(Message message) {
        Route backRoute = overturnRoute(message.getRoute());
        controlCenter.closeDoor(isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(backRoute, Code.CCLOSEDOOR_SUCCESS);
            } else {
                sendData = new Message(backRoute, Code.CLOSEDOOR_DEFAULT);
            }
            sendMessage(sendData);
        });
    }

    private void resetDeviceCommand(Route backRoute) {
        stopCollectEnviroData();
        controlCenter.reset();

        controlCenter.enableHumanHandle(isSucceed -> {
            Message sendData;
            Handle handle = new Handle(new UserCard("钥匙"), "进", controlCenter.getDeviceNumber());
            if (isSucceed) {
                sendData = new Message(toService, Code.OPENDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.OPENDOOR_DEFAULT);
            }
            sendData.setData(handle);
            sendMessage(sendData);
        }, isSucceed -> {
            Message sendData;
            if (isSucceed) {
                sendData = new Message(toService, Code.CCLOSEDOOR_SUCCESS);
            } else {
                sendData = new Message(toService, Code.CLOSEDOOR_DEFAULT);
            }
            sendMessage(sendData);
        });

        controlCenter.startAutoSearchCard(userCard -> {
            Handle handle = new Handle(userCard, "进", controlCenter.getDeviceNumber());
            checkUserInfoOnOpenDoor(handle);
        }, userCard -> {
            Handle handle = new Handle(userCard, "出", controlCenter.getDeviceNumber());
            checkUserInfoOnOpenDoor(handle);
        });

        startCollectEnviroData();

        Message sendData = new Message(backRoute, Code.RESET_DEVICE_SUCCESS);
        sendMessage(sendData);
    }

    /**
     * 清理内存
     */
    private void cleanMemoryCommand(Route route) {
        Message sendData;
        if (LogUtil.cleanLogData()) {
            sendData = new Message(route, Code.CLEAN_LOG_SUCCESS);
        } else {
            sendData = new Message(route, Code.CLEAN_LOG_DEFAULT);
        }
        sendMessage(sendData);
    }

    /**
     * 上传环境数据
     *
     * @return
     */
    private void uploadEnvironmentDateCommand(Route route) {
        EnviroInfo enviroInfo = controlCenter.getEnviroDate();
        Message sendData;
        if (enviroInfo != null) {
            sendData = new Message(route, Code.UPLOAD_ENVIRODATE_SUCCESS, enviroInfo);
        } else {
            sendData = new Message(route, Code.UPLOAD_ENVIRODATE_DEFAULT);
        }
        sendMessage(sendData);
    }

    /**
     * 上传门禁状态信息
     *
     * @return
     */
    private void uploadDeviceStateCommand(Route route) {
        DeviceState stateDate = controlCenter.getDeviceStateDate();
        Message sendData;
        if (stateDate != null) {
            sendData = new Message(route, Code.UPLOAD_DOOR_STATE_SUCCESS, stateDate);
        } else {
            sendData = new Message(route, Code.UPLOAD_DOOR_STATE_DEFAULT);
        }
        sendMessage(sendData);
    }

    /**
     * 上传设备信息
     */
    private void uploadDeviceInfoCommand(Route route) {
        DeviceInfo deviceInfo = controlCenter.getDeviceInfor();
        Message sendData;
        if (deviceInfo != null) {
            sendData = new Message(route, Code.UPLOAD_DEVICE_INFO_SUCCESS, deviceInfo);
        } else {
            sendData = new Message(route, Code.UPLOAD_DEVICE_INFO_DEFAULT);
        }
        sendMessage(sendData);
    }




    /**
     * 翻转消息路由
     *
     * @param route
     * @return
     */
    private Route overturnRoute(Route route) {
        Route turnedRoute = new Route();
        turnedRoute.setFrom(route.getTo());
        turnedRoute.setTo(route.getFrom());
        return turnedRoute;
    }

    /**
     * 发送卡片号给管理，此函数一般在管理员添加新用户时
     *
     * @param userCard
     * @return
     */
    private void sendUserCardToAdmin(Route backRoute, UserCard userCard) {
        Message sendData;
        if (userCard != null) {
            sendData = new Message(backRoute, Code.UPLOAD_CARD_SUCCESS, userCard);
        } else {
            sendData = new Message(backRoute, Code.UPLOAD_CARD_DEFAULT);
        }
        sendMessage(sendData);
    }

    /**
     * 开门时校验用户信息
     *
     * @param handle 用户操作信息
     */
    private void checkUserInfoOnOpenDoor(Handle handle) {
        Message sendData = new Message(toService, Code.CHECK_USERINFO, handle);
        sendMessage(sendData);
    }

    /**
     * 发送消息给服务器
     *
     * @param message
     */
    private void sendMessage(Message message) {
        String sendString = JSON.toJSONString(message);
        send(sendString);
    }

    /**
     * 自动采集环境数据并上传，30分钟采集一次。
     */
    private void startCollectEnviroData() {
        if (collectEnviroData)
            return;
        collectEnviroData = true;

        if (collectThread != null && collectThread.isAlive())
            return;
        collectThread = new Thread(() -> {
            while (collectEnviroData) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    collectEnviroData = false;
                }
                uploadEnvironmentDateCommand(toService);
                uploadEnvironmentDateCommand(toAdmin);
            }
        });
        collectThread.start();
    }

    /**
     * 停止采集环境数据
     */
    private void stopCollectEnviroData() {
        collectEnviroData = false;
    }

}
