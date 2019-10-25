FROM maven as build
WORKDIR /bbs
COPY . .
RUN mvn clean install package

FROM openjdk
EXPOSE 6510
WORKDIR /bbs
COPY --from=build /bbs/target/* /bbs/
ENTRYPOINT ["java", "-jar","petscii-bbs-1.0-SNAPSHOT.jar","-b","CsdbReleases"]
