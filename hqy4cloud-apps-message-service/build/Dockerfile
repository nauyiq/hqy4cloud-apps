FROM registry.cn-shenzhen.aliyuncs.com/hqy-parent-all/jdk:17
MAINTAINER hongqy<759428167@qq.com>

# set environment
ENV BASE_DIR="/hqy4cloud/hqy4cloud-apps-message-service" \
    JAVA_JAR="hqy4cloud-apps-message-service.jar" \
    JAVA_HOME="/usr/local/java/jdk-17.0.9" \
    JAVA="${JAVA_HOME}/bin/java" \
    JVM_XMS="512m" \
    JVM_XMX="768m" \
    JVM_XMN="256m" \
    JVM_MS="50m" \
    JVM_MMS="100m" \
    TOMCAT_ACCESSLOG_ENABLED="false" \
    TIME_ZONE="Asia/Shanghai" \
    JAVA_OPT_EXT=""

WORKDIR ${BASE_DIR}

RUN  ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

ADD lib target/lib
ADD ${SERVICE_JAR} target/${SERVICE_JAR}
ADD bin/start.sh bin/start.sh

RUN mkdir -p logs \
    && chmod +x bin/* \
    && chmod +x target/*

EXPOSE 8099 9099 18099

ENTRYPOINT ["bin/start.sh"]
