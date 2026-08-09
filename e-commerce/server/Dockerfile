# syntax=docker/dockerfile:1.7
# ---------------------------------------------------------------
#  ShopLane backend - production Docker image
#
#  Build:   docker build -t shoplane-backend server/
#  Run:     docker run -p 8080:8080 \
#             -e DB_URL='jdbc:mysql://host:3306/shoplane...' \
#             -e DB_USER=... -e DB_PASSWORD=... \
#             -e JWT_SECRET=... -e JWT_REFRESH_SECRET=... \
#             -e CORS_ORIGINS='https://you.github.io' \
#             shoplane-backend
# ---------------------------------------------------------------

# ---------- Stage 1: build the fat jar with Maven --------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache dependencies first (this layer only invalidates when pom.xml changes)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now copy sources and build
COPY src ./src
RUN mvn -B -q -DskipTests clean package && \
    cp target/shoplane-backend-*.jar /workspace/app.jar

# ---------- Stage 2: slim runtime ------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for a smaller attack surface
RUN addgroup -S shoplane && adduser -S shoplane -G shoplane && \
    mkdir -p /app/logs && chown -R shoplane:shoplane /app
USER shoplane

COPY --from=build --chown=shoplane:shoplane /workspace/app.jar /app/app.jar

# Railway / Fly / Render set PORT dynamically — honour it, default to 8080
ENV SERVER_PORT=8080
EXPOSE 8080

# JVM flags tuned for a small container (256-512 MB free tier)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://127.0.0.1:${SERVER_PORT}/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh","-c","exec java -jar /app/app.jar --server.port=${PORT:-${SERVER_PORT}}"]
