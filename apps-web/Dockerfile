# base image
ARG BASE_IMAGE=adoptopenjdk/openjdk11
FROM ${BASE_IMAGE}

# set envrionment
ENV APP_HOME /apps-admin

# copy resources
RUN mkdir {APP_HOME}
COPY build/libs/apps-web-*-boot.jar ${APP_HOME}/apps-web-boot.jar

# expose
EXPOSE 8080

# entry point
WORKDIR ${APP_HOME}
ENTRYPOINT ["java","-jar","apps-web-boot.jar"]

