# base image
ARG BASE_IMAGE=adoptopenjdk/openjdk11
FROM ${BASE_IMAGE}

# set envrionment
ENV APP_HOME /apps-batch

# copy resources
RUN mkdir {APP_HOME}
COPY build/libs/apps-batch-*-boot.jar ${APP_HOME}/apps-batch-boot.jar

# entry point
WORKDIR ${APP_HOME}
ENTRYPOINT ["java","-jar","apps-batch-boot.jar"]

