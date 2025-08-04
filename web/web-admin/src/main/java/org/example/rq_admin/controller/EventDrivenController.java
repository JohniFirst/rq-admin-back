package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.rq_admin.enums.ResponseStatus;
import org.example.rq_admin.entity.DTO.UserLoginDTO;
import org.example.rq_admin.event.CustomEvents;
import org.example.rq_admin.event.CustomEvents1;
import org.example.rq_admin.utils.EventPublisher;
import org.example.rq_admin.response_format.FormatResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "事件驱动", description = "触发这个模块的接口，可以借助springboot底层的消息接口，实现事件广播")
@RestController
@RequestMapping("/event")
public class EventDrivenController {

    private final EventPublisher eventPublisher;

    public EventDrivenController(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Operation(summary = "模拟用户登录事件")
    @GetMapping("/userLogin")
    public FormatResponseData publishEvent1() {

        CustomEvents customEvents = new CustomEvents(new UserLoginDTO("zhangsan", "123456", "1"));

        eventPublisher.publishEvent(customEvents);

        return new FormatResponseData<>(ResponseStatus.SUCCESS, "事件发布成功");
    }

    @Operation(summary = "另外的一个事件", description = "消息监听是通过区分事件来实现的，一个服务可以同时监听多个事件，通过传递的参数来区分")
    @GetMapping("/publish2")
    public FormatResponseData publishEvent2() {

        CustomEvents1 customEvents1 = new CustomEvents1(new UserLoginDTO("我是另外一个张三", "123456", "1"));

        eventPublisher.publishEvent(customEvents1);

        return new FormatResponseData<>(ResponseStatus.SUCCESS, "事件发布成功");
    }
}
