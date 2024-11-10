# İlk aşama: Maven ile projenin build edilmesi
FROM maven:3.8.8-eclipse-temurin-17 AS build

# Uygulama kaynak kodunu konteynere kopyalayın
COPY . /app

# Çalışma dizinini ayarlayın
WORKDIR /app

# Maven ile projeyi build edin
RUN mvn clean package -DskipTests

# İkinci aşama: Çalıştırma için optimize edilmiş bir image kullanarak jar dosyasını kopyalayın
FROM eclipse-temurin:17-jdk-alpine

# Birinci aşamada build edilen jar dosyasını buraya kopyalayın
COPY --from=build /app/target/*.jar app.jar

# Uygulamanın çalışacağı portu açın
EXPOSE 8080

# Uygulamayı başlatın
ENTRYPOINT ["java", "-jar", "/app.jar"]
