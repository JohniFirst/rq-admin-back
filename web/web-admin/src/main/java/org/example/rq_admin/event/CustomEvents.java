package org.example.rq_admin.event;

import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.springframework.context.ApplicationEvent;

public class CustomEvents extends ApplicationEvent {

    /**
     * 自定义事件
     * @param source 要发布的事件带的参数
     */
    public CustomEvents(UserLoginDTO source) {
        super(source);
    }
}
