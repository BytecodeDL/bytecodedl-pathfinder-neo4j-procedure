From neo4j:5.12.0

LABEL maintainer="yxxx <yxwuman@gmail.com>"

COPY target/bytecodedl-pathfinder-1.0.0.jar /var/lib/neo4j/plugins

ENV NEO4J_AUTH=neo4j/bytecodedl \
    NEO4J_dbms_security_procedures_unrestricted=bytecodedl.*