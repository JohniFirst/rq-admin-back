package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationConfig<T> {
    private List<T> data;  // 当前页的数据
    private long total;  // 总记录数
    private int totalPages;  // 总页数
    private int currentPage;  // 当前页码
    private int pageSize;  // 每页记录数
}
