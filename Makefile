# Makefile for running Redis and MongoDB with Docker

# Run Redis CLI
run-redis-cli:
	docker run -it --rm redis redis-cli -h host.docker.internal -p 6379

# Run MongoDB
run-mongo-db:
	docker run -d --name mongodb -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=secret mongo

# Stop MongoDB container
stop-mongo-db:
	docker stop mongodb && docker rm mongodb

start-mongo-compass:
	docker run -d --name mongo-express -p 8081:8081 --link mongodb:mongo \
      -e ME_CONFIG_MONGODB_ADMINUSERNAME=admin \
      -e ME_CONFIG_MONGODB_ADMINPASSWORD=secret \
      -e ME_CONFIG_MONGODB_URL="mongodb://admin:secret@mongodb:27017/" \
      mongo-express

stop-mongo-compass:
	docker stop mongo-express && docker rm mongo-express

# Stop Redis container
stop-redis:
	docker stop redis-server && docker rm redis-server

# Run Redis Server
run-redis-server:
	docker run -d --name redis-server -p 6379:6379 -e REDIS_PASSWORD=ichu redis redis-server --requirepass "ichu"

# Restart Redis Server
restart-redis:
	make stop-redis && make run-redis-server

# Restart MongoDB
restart-mongo:
	make stop-mongo-db && make run-mongo-db

# Show running containers
ps:
	docker ps

# Clean up all stopped containers
clean:
	docker system prune -f

create:
	CREATE TABLE yt_user ( \
        id SERIAL PRIMARY KEY, \
        account_status VARCHAR(20) NOT NULL, \
        email_id VARCHAR(255) UNIQUE NOT NULL, \
        is_email_verified BOOLEAN DEFAULT FALSE, \
        is_user_approved BOOLEAN DEFAULT FALSE, \
        mobile_number VARCHAR(15) UNIQUE NOT NULL, \
        password VARCHAR(255) NOT NULL, \
        user_role VARCHAR(50) NOT NULL, \
        username VARCHAR(50) UNIQUE NOT NULL, \
        full_name VARCHAR(100) NOT NULL, \
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP \
    );

admin-setup:
	INSERT INTO yt_user (id, account_status, email_id, is_email_verified, is_user_approved, mobile_number, password, user_role, username, full_name, created_at ) VALUES ( 1, 'ACTIVE', 'abhijith.anjana@gmail.com', true, true, '08848331138', '$2a$10$WxZEOlK0PcfYXEuNFfnRZ.0Lz.gZBiIfAz8Zv.Sl6FEmDHO1EcJzO','ADMIN', 'admin', 'Admin', NOW()); \
       &&\
     INSERT INTO public.yt_channel(channel_name, created_at, youtube_channel_id, admin_id) values('test_channel', now(), 'abhianil', 1)


#update yt_user set password ='$2a$10$OY7DreRy4DMbTLucoh9y0uOxGK7FU13wSw826e4YWo5vvpk4xrcNa' where id =1