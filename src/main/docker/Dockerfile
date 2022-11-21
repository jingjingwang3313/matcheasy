# 指定基础镜像
FROM java:8

# VOLUME 挂载卷
VOLUME /app/matcheasy/upload /app/matcheasy/log
#VOLUME ["/app/matcheasy/upload","/app/matcheasy/log"]

# 复制命令
ADD matcheasy.jar matcheasy.jar

# RUN命令
RUN bash -c 'touch /matcheasy.jar'
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo 'Asia/Shanghai' > /etc/timezone

# 映射端口
EXPOSE 80

# 容器启动时运行得启动命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "matcheasy.jar"]