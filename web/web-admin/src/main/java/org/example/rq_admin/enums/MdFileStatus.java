package org.example.rq_admin.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MdFileStatus {
    DEPRECATED(3, "已废弃"),
    PUBLISH(2, "已发布"),
    APPROVING(1, "审核中"),
    EDITING(0, "编辑中");

    @EnumValue
    private final Integer code;

    private final String desc;

    @JsonValue
    public String getDesc() {
        return desc;
    }

    // 反序列化：根据code获取枚举（前端传code时使用）
    public static MdFileStatus getByCode(Integer code) {
        for (MdFileStatus mdFileStatus : MdFileStatus.values()) {
            if (mdFileStatus.getCode().equals(code)) {
                return mdFileStatus;
            }
        }

        return null;
    }
}
