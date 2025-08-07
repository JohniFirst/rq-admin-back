package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.rq_admin.response_format.FormatResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "根据参数返回状态", description = "测试接口，根据请求参数返回接口请求成功还是失败")
@Slf4j
@RestController
@RequestMapping
public class ResponseByRequestData {

    @Operation(summary = "状态相应", description = "根据请求参数，接口返回处理成功/失败")
    @GetMapping("/test/response-by-params/{isSuccess}")
    public FormatResponseData ResponseByRequestDataFn(
            @Parameter(description = "期待响应成功还是失败") @PathVariable("isSuccess") Boolean isSuccess
    ) {
        log.info("测试接口接收到了测试请求，此次请求{}", isSuccess.toString());
        return isSuccess ? FormatResponseData.ok() : FormatResponseData.error("请求失败");
    }
}
