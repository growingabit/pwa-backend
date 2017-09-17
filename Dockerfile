FROM java:7-jdk

ARG MAVEN_VERSION=3.5.0
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

ENV CLOUD_SDK_VERSION 171.0.0

RUN apt-get update
RUN apt-get install -qqy apt-transport-https lsb-release apt-utils
RUN export CLOUD_SDK_REPO="cloud-sdk-$(lsb_release -c -s)" && echo "deb https://packages.cloud.google.com/apt $CLOUD_SDK_REPO main" >> /etc/apt/sources.list
RUN curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -

RUN apt-get update

RUN apt-get install -y kubectl google-cloud-sdk google-cloud-sdk-app-engine-java
RUN wget http://ftp-stud.hs-esslingen.de/pub/Mirrors/ftp.apache.org/dist/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.zip
RUN unzip apache-maven-3.5.0-bin.zip

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

VOLUME "$USER_HOME_DIR/.m2"

WORKDIR "apache-maven-3.5.0/bin"

EXPOSE 8888

ENTRYPOINT ["./mvn", "-f", "/pwa-backend/pom.xml", "clean", "package", "appengine:run"]
