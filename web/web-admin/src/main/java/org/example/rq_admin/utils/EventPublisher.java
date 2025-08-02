package org.example.rq_admin.utils;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    /**
     * 所有的事件都可以发
     * @param event 发送的事件的参数
     */
    public void publishEvent(ApplicationEvent event) {
        // 调用底层的api发送事件
        publisher.publishEvent(event);
    }

    /**
     * 会被自动调用，把真正发送事件的底层组件注入过来
     * @param applicationEventPublisher springboot官方的事件注册
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
