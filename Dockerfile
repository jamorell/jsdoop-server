#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package



# Install python
FROM openjdk:11-jdk-slim-buster

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        python3.7 \
        python3-pip \
        python3.7-dev \
        python3-setuptools \
        python3-wheel



# Install python libraries
RUN mkdir -p /opt/files/topology
RUN chmod -R 777 /opt/files
COPY requirements.txt /opt/files/
RUN /usr/bin/python3 -m pip install --upgrade pip
RUN /usr/bin/python3 -m pip install -r /opt/files/requirements.txt
COPY conda_model_conversion.sh /opt/files/
COPY model_conversion.py /opt/files/


#
# Package stage
#
#FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/jsdoop-server-0.0.1-SNAPSHOT.jar /usr/local/lib/jsdoop-server-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/usr/local/lib/jsdoop-server-0.0.1-SNAPSHOT.jar"]
