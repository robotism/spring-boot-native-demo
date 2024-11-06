
## 部署服务(Nacos版)


```
mkdir /opt/data/seata/config
cd /opt/data/seata/

git clone https://github.com/seata/seata.git src

# init nacos
cd /opt/data/seata/src/script/config-center/nacos
vim ../config.txt # 修改为`db`模式, 配置用户名密码; 配置vgroupmaping(与客户端一致, 否则找不到服务错误)
sh nacos-config.sh data.kluster.xyz

# init sql
#cd /opt/data/seata/src/script/server/db
# CREATE DATABASE IF NOT EXISTS seata CHARACTER SET utf8mb4;
# 

# config registry
cd /opt/data/seata/config
\cp -rf /opt/data/seata/src/script/server/config/registry.conf ./registry.conf
vim registry.conf # 配置为nacos

# start seata server
docker rm -f seata
docker run -dit \
    --restart=always \
    --privileged \
    -p 8091:8091 \
    -e SEATA_IP=139.159.176.67 \
    -v /opt/data/seata/config:/root/seata-config  \
    -e SEATA_CONFIG_NAME=file:/root/seata-config/registry \
    --name=seata \
    seataio/seata-server:latest

```