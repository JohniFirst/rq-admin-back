package org.example.rq_admin.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.event.CustomEvents;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AddScoreListener implements ApplicationListener<CustomEvents> {
    /**
     * @param event 接收到的事件传递过来的数据
     */
    @Override
    public void onApplicationEvent(CustomEvents event) {
        log.info("这里是增加积分服务，接收到了自定义事件的发布，同样可以在下方自定义事件");
        UserLoginDTO events = (UserLoginDTO) event.getSource();
        addScore(events.getUsername());
    }

    /**
     * 增加积分服务的处理函数
     *
     * @param username 需要增加积分的用户名
     */
    private void addScore(String username) {
        log.info("触发了增加积分服务：============>恭喜【" + username + "】");
    }
}
