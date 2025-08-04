package org.example.rq_admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.rq_admin.enums.ResponseStatus;
import org.example.rq_admin.entity.MybatisPlusTest;
import org.example.rq_admin.mapper.MybatisPlusTestMapper;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.impl.MybatisPlusTestServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MybatisPlus测试", description = "MybatisPlus测试接口")
@RestController
public class MybatisPlusTestController {

    private final MybatisPlusTestMapper mybatisPlusTestMapper;
    private final MybatisPlusTestServiceImpl mybatisPlusTestService;

    public MybatisPlusTestController(MybatisPlusTestMapper mybatisPlusTestMapper, MybatisPlusTestServiceImpl mybatisPlusTestService) {
        this.mybatisPlusTestMapper = mybatisPlusTestMapper;
        this.mybatisPlusTestService = mybatisPlusTestService;
    }

    @Operation(summary = "测试新增", description = "新增接口测试")
    @PostMapping("/add")
    public FormatResponseData<Integer> addNewList(@Valid @RequestBody MybatisPlusTest mybatisPlusTest) {
        int insert = mybatisPlusTestMapper.insert(mybatisPlusTest);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, insert);
    }

    @Operation(summary = "查看测试列表", description = "获取所有列表")
    @GetMapping("/list/all")
    public FormatResponseData<List<MybatisPlusTest>> listAll() {
        List<MybatisPlusTest> list = mybatisPlusTestMapper.selectList(null);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, list);
    }

    @Operation(summary = "列表分页", description = "分页获取列表数据")
    @GetMapping("/list/pagination")
    public FormatResponseData<List<MybatisPlusTest>> listPagination(
            @Parameter(description = "当前页数") @RequestParam Integer current,
            @Parameter(description = "分页大小") @RequestParam Integer size
    ) {
        Page<MybatisPlusTest> page = new Page<>(current, size);
        List<MybatisPlusTest> list = mybatisPlusTestService.list(page);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, list);
    }

    @Operation(summary = "单个删除", description = "根据id删除对应的数据")
    @DeleteMapping("/delete/{id}")
    public FormatResponseData<Integer> delete(@PathVariable Long id) {
        int i = mybatisPlusTestMapper.deleteById(id);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, i);
    }

    @Operation(summary = "批量删除", description = "根据id数组批量物理删除数据")
    @DeleteMapping("/batch-delete")
    public FormatResponseData<Integer> batchDelete(@RequestBody List<Long> ids) {
        int i = mybatisPlusTestMapper.deleteByIds(ids);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, i);
    }

    @Operation(summary = "编辑数据", description = "编辑整个数据")
    @PutMapping("/edit")
    public FormatResponseData<Integer> edit(@RequestBody MybatisPlusTest mybatisPlusTest) {
        int i = mybatisPlusTestMapper.updateById(mybatisPlusTest);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, i);
    }
}
