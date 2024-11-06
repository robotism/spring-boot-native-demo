package com.gankcode.springboot.db.config.druid;

import com.alibaba.druid.pool.DruidDataSource;

public class NamedDruidDataSource extends DruidDataSource {

    @Override
    public DruidDataSource cloneDruidDataSource() {
        final NamedDruidDataSource x = new NamedDruidDataSource();
        cloneTo(x);
        return x;
    }

    @Override
    public String getName() {
        return (this.name != null ? this.name : "DataSource") + "-" + System.identityHashCode(this);
    }

}
