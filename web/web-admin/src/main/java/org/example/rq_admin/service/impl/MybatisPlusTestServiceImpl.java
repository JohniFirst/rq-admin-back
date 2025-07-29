package org.example.rq_admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.rq_admin.entity.MybatisPlusTest;
import org.example.rq_admin.mapper.MybatisPlusTestMapper;
import org.example.rq_admin.service.MybatisPlusTestService;
import org.springframework.stereotype.Service;

@Service
public class MybatisPlusTestServiceImpl extends ServiceImpl<MybatisPlusTestMapper, MybatisPlusTest> implements MybatisPlusTestService {
}
