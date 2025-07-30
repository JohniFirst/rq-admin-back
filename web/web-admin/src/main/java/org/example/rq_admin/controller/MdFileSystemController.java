package org.example.rq_admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.enums.ResponseStatus;
import org.example.rq_admin.DTO.MdFileSystemAddDTO;
import org.example.rq_admin.entity.MdFileSystem;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.impl.MdFileSystemServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "md文档管理", description = "md文档管理系统")
@RestController
@RequestMapping("/markdown")
public class MdFileSystemController {

    final MdFileSystemServiceImpl mdFileSystemService;

    public MdFileSystemController(MdFileSystemServiceImpl mdFileSystemService) {
        this.mdFileSystemService = mdFileSystemService;
    }

    @Operation(summary = "新增md文件", description = "新建一个md文档")
    @PostMapping("/new")
    public FormatResponseData newMdFileSystem(@RequestBody MdFileSystemAddDTO mdFileSystemAddDTO) {
        // 通过DTO的方式，使得接口文档变得很干净，同时也满足需求
        MdFileSystem mdFileSystem = new MdFileSystem();
        BeanUtils.copyProperties(mdFileSystemAddDTO, mdFileSystem);
        boolean isSuccess = mdFileSystemService.save(mdFileSystem);
        return new FormatResponseData<>(isSuccess ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE);
    }

    @Operation(summary = "文件列表", description = "文档列表")
    @GetMapping("/list")
    public FormatResponseData<List<MdFileSystem>> listMdFileSystem() {
        List<MdFileSystem> mdFileSystemList = mdFileSystemService.list();
        return new FormatResponseData<>(ResponseStatus.SUCCESS, mdFileSystemList);
    }

    @Operation(summary = "获取文件内容", description = "根据id获取文件内容")
    @PostMapping("/getFileById/{id}")
    public FormatResponseData<MdFileSystem> getFileById(@PathVariable Long id) {
        MdFileSystem mdFileSystem = mdFileSystemService.getById(id);
        return new FormatResponseData<>(ResponseStatus.SUCCESS, mdFileSystem);
    }

    @Operation(summary = "修改文件状态", description = "更新文件状态")
    @PostMapping("/updateStatus/{id}/{status}")
    public FormatResponseData updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        boolean b = mdFileSystemService.updateStatusById(id, status);
        return new FormatResponseData<>(b ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE);
    }

    @Operation(summary = "编辑文件内容", description = "编辑文件内容/备注信息")
    @PutMapping("/update")
    public FormatResponseData update(@RequestBody MdFileSystem mdFileSystem) {
        boolean b = mdFileSystemService.updateById(mdFileSystem);
        return new FormatResponseData<>(b ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE);
    }

    @Operation(summary = "删除文件", description = "支持批量删除，删除一个或多个文件")
    @DeleteMapping("/delete")
    public FormatResponseData delete(@RequestParam List<Long> ids) {
        boolean b = mdFileSystemService.removeByIds(ids);
        return new FormatResponseData<>(b ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE);
    }
}
