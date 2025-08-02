package org.example.rq_admin.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.event.CustomEvents;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * 消息订阅者，所有的消息订阅者都推荐实现ApplicationListener接口
 */
@Slf4j
@Service
public class AddCouponListener implements ApplicationListener<CustomEvents> {

    /**
     * 发放优惠券的处理函数
     *
     * @param username 需要发放优惠券的用户名
     */
    private void AddCoupon(String username) {
        log.info("这里是自定义事件函数，接收到了事件发布者发布的数据：===========>恭喜【" + username + "】");
    }

    /**
     * 接收到事件的时候的响应
     *
     * @param event 自定义事件的参数
     */
    @Override
    public void onApplicationEvent(CustomEvents event) {
        log.info("增加优惠券的service接收到了事件，可以在下方触发事件处理函数");
        UserLoginDTO events = (UserLoginDTO) event.getSource();
        AddCoupon(events.getUsername());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
