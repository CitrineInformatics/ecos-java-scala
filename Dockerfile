FROM sbtscala/scala-sbt:eclipse-temurin-jammy-11.0.17_8_1.9.4_2.13.12

ENV SBT_OPTS="-Xmx4G -XX:+UseG1GC"

WORKDIR /opt/ecos-java-scala

RUN apt-get -yq update && apt-get -yq install cmake build-essential

COPY . .

RUN --mount=type=cache,target=/opt/ecos-java-scala/ecos git submodule update --init --recursive

RUN sed -i 's/PRINTLEVEL (2)/PRINTLEVEL (0)/g' ecos/include/glblopts.h && \
    sed -i 's/PROFILING (1)/PROFILING (0)/g' ecos/include/glblopts.h