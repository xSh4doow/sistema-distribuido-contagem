FROM openjdk:17-slim

WORKDIR /app

COPY Comum/ ./Comum/
COPY Receptor/ ./Receptor/

RUN javac Comum/*.java && \
    javac -cp ".:Comum" Receptor/*.java

EXPOSE 12345

CMD ["java", "-cp", ".:Comum:Receptor", "Receptor", "12345"]
