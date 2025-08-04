package org.example.rq_admin.enums;

import lombok.Getter;

// 手动创建的一个枚举类
@Getter
public enum SeasonEnum {
    SPRING("春天", "万物复苏"),
    SUMMER("夏天", "夏日炎炎"),
    AUTUMN("秋天", "秋高气爽"),
    WINTER("东天", "白雪皑皑");

    private final String seasonName;
    private final String seasonDesc;

    SeasonEnum(String seasonName, String seasonDesc) {
        this.seasonName = seasonName;
        this.seasonDesc = seasonDesc;
    }
}
