package org.example;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PaginationConfig<T> {
    private List<T> data;  // 当前页的数据
    private long total;  // 总记录数
    private int totalPages;  // 总页数
    private int currentPage;  // 当前页码
    private int pageSize;  // 每页记录数

    public PaginationConfig() {}

    public PaginationConfig(List<T> data, long total, int totalPages, int currentPage, int pageSize) {
        this.data = data;
        this.total = total;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    /**
     * 从Spring Data JPA的Page对象创建PaginationConfig
     * 注意：Spring Data JPA的页码从0开始，这里转换为从1开始
     */
    public static <T> PaginationConfig<T> fromPage(Page<T> page) {
        return new PaginationConfig<>(
            page.getContent(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber() + 1, // 转换为从1开始的页码
            page.getSize()
        );
    }

    /**
     * 将前端传入的页码（从1开始）转换为Spring Data JPA的页码（从0开始）
     */
    public static int convertToSpringPageNumber(int pageNumber) {
        return Math.max(0, pageNumber - 1);
    }
}
