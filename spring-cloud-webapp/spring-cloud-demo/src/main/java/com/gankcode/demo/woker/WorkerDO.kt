package com.gankcode.demo.woker

import com.mybatisflex.annotation.Id
import com.mybatisflex.annotation.Table


@Table("t_worker_node1")
class WorkerDO {
    @Id
    var id: Long? = null
}