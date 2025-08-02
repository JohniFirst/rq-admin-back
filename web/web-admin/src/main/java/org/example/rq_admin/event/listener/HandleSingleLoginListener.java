package org.example.rq_admin.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.event.CustomEvents;
import org.example.rq_admin.event.CustomEvents1;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandleSingleLoginListener {

    // 可以同时监听多个事件，事件到达后触发对应的函数处理
    @EventListener
    public void onListener(CustomEvents customEvents) {
        log.info("接收到用户登录的广播，下方触发单点登录处理函数");
        UserLoginDTO userLoginDTO = (UserLoginDTO) customEvents.getSource();
        handleSingleLogin(userLoginDTO.getUsername());
    }

    @EventListener
    public void onListener(CustomEvents1 customEvents) {
        log.info("接收到的另外的一个事件，看看响应");
        UserLoginDTO userLoginDTO = (UserLoginDTO) customEvents.getSource();
        handleAnotherEventListener(userLoginDTO.getUsername());
    }

    private void handleAnotherEventListener(String username) {
        log.info("这里接收到了另外的一个事件的触发，================>" + username);
    }

    private void handleSingleLogin(String username) {
        log.info("检测到了用户登录，将确保该用户只在单一客户端登录：================>" + username);
    }
}
