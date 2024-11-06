package com.gankcode.demo.woker

import com.mybatisflex.core.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface WorkerMapper : BaseMapper<WorkerDO> {
}