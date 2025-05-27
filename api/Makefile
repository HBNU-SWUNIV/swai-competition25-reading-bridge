.PHONY: all build down re

all: build copy up

build:
	@./gradlew clean build -x test

copy:
	@cp ./build/libs/dyslexia-0.0.1-SNAPSHOT.jar ./app.jar

up:
	@docker compose -f docker-compose.yml -p test up --build -d

down:
	@docker compose -f docker-compose.yml -p test down

re: down up