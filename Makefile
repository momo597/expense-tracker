.PHONY: build run test docker-build docker-up docker-down

build: 
	mvn clean package -DskipTests

run:
	mvn spring-boot:run

test:
	mvn test

docker-build:
	docker build -t expense-tracker .

docker-up:
	docker-compose up --build -d

docker-down:
	docker-compose down