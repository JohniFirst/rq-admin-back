package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.response_format.FormatResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "根据参数返回状态", description = "测试接口，根据请求参数返回接口请求成功还是失败")
@RestController
@RequestMapping
public class ResponseByRequestData {
    private static final Logger logger = LoggerFactory.getLogger(ResponseByRequestData.class);


    @Operation(summary = "状态相应", description = "根据请求参数，接口返回处理成功/失败")
    @GetMapping("/test/response-by-params/{isSuccess}")
    public FormatResponseData ResponseByRequestDataFn(
            @Parameter(description = "期待响应成功还是失败") @PathVariable("isSuccess") Boolean isSuccess
    ) {
        logger.info("测试接口接收到了测试请求，此次请求" + isSuccess.toString());
        return new FormatResponseData<>(isSuccess ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE);
    }
}
