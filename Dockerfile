FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y \
    maven \
    git \
    nginx \
    openssh-server \
    php-cli \
    default-mysql-client \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . /app

RUN mkdir -p /var/run/sshd

RUN echo 'root:Hello@123' | chpasswd
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

COPY nginx.conf /etc/nginx/sites-available/default
COPY start.sh /start.sh

RUN chmod +x /start.sh

EXPOSE 8080 22

CMD ["/start.sh"]