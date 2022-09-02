# ordinaryroad-blog-quarkus Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory. Be aware that it’s not an _über-jar_ as
the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/ordinaryroad-blog-quarkus-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

```shell

docker run -p 8080:8080 -e MYSQL_HOST=192.168.5.127 -e MYSQL_PORT=3306 -e MYSQL_USERNAME=root -e MYSQL_PASSWORD=root -e REDIS_HOST=192.168.5.127 -e REDIS_PORT=6379 ordinaryroad-blog-quarkus
```

```shell

docker run -p 8080:8080 -e MYSQL_HOST=192.168.5.127 -e MYSQL_PORT=3306 -e MYSQL_USERNAME=root -e MYSQL_PASSWORD=root -e REDIS_HOST=192.168.5.127 -e REDIS_PORT=6379 ordinaryroad-blog-quarkus
```

# OrdinaryRoad博客设计

## 导航

## 用户

支持多用户

主账号：OrdinaryRoad账号

[//]: # (第三方账号：)

## 文章

- [x] MarkDown格式
- [ ] 文章归档

## 分类

每个文章对应一个分类

分类为每个用户私有的

## 标签

每个文章对应多个标签

标签为每个用户公有的

## 评论

# 使用容器进行Maven构建

```shell

docker run -it --rm --name ordinaryroad-blog \
--platform=linux/amd64 \
-v /Users/ordinaryroad/Environment/maven/repo:/root/.m2/repository \
-v "$(pwd)":/usr/src/mymaven \
-w /usr/src/mymaven \
maven:3.8.6-jdk-11 \
mvn install

```

# 构建镜像

> jvm

```shell

docker build -f src/main/docker/Dockerfile.jvm -t quarkus/ordinaryroad-blog-quarkus-jvm .

```

> 运行测试

```shell

docker run -i --rm -p 8080:8080 -e ORDINARYROAD_OAUTH2_CLIENT_ID=xxx -e ORDINARYROAD_OAUTH2_CLIENT_SECRET=xxx -e MAIL_QQ_PASSWORD=xxxxxx -e MYSQL_HOST=192.168.6.231 -e MYSQL_PORT=3306 -e MYSQL_USERNAME=root -e MYSQL_PASSWORD=Root123. -e REDIS_HOST=192.168.6.231 -e REDIS_PORT=6379 quarkus/ordinaryroad-blog-quarkus-jvm

```

> 修改tag并push

```shell

docker tag quarkus/ordinaryroad-blog-quarkus-jvm ordinaryroad-docker.pkg.coding.net/ordinaryroad/docker-pro/ordinaryroad-blog:20220831_3

```

```shell

docker push ordinaryroad-docker.pkg.coding.net/ordinaryroad/docker-pro/ordinaryroad-blog:20220831_3

```
