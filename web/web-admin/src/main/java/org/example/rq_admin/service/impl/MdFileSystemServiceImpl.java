package org.example.rq_admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.enums.MdFileStatus;
import org.example.rq_admin.entity.MdFileSystem;
import org.example.rq_admin.mapper.MdFileSystemMapper;
import org.example.rq_admin.service.MdFileSystemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MdFileSystemServiceImpl extends ServiceImpl<MdFileSystemMapper, MdFileSystem> implements MdFileSystemService {

    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusById(Long id, Integer status) {
        // 1、参数校验
        if(id == null || status == null){
            log.error("id和status均不能为空");
            throw new IllegalArgumentException("id和status均不能为空");
        }

        LambdaUpdateWrapper<MdFileSystem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MdFileSystem::getId, id)
                .set(MdFileSystem::getStatus, status);

        int isUpdated = baseMapper.update(null, updateWrapper);

        return isUpdated > 0;
    }
}
