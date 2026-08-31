COMPOSE=docker compose \
	--env-file infra/.env \
	-f infra/docker-compose.yml

APP_CONTAINER=jdbc_delivery_app
DB_CONTAINER=jdbc_delivery_db

build:
	$(COMPOSE) build

up:
	$(COMPOSE) up -d

down:
	$(COMPOSE) down

restart:
	$(COMPOSE) restart

logs:
	$(COMPOSE) logs -f

logs-app:
	$(COMPOSE) logs -f app

logs-db:
	$(COMPOSE) logs -f db

ps:
	$(COMPOSE) ps

exec:
	docker exec -it $(APP_CONTAINER) bash

exec-db:
	docker exec -it $(DB_CONTAINER) bash

psql:
	docker exec -it $(DB_CONTAINER) psql -U postgres -d delivery

clean:
	$(COMPOSE) down -v
	rm -rf infra/postgres/data

db-reset:
	$(COMPOSE) down -v
	rm -rf infra/postgres/data
	$(COMPOSE) up -d

mvn-package:
	$(COMPOSE) exec app mvn clean package

mvn-test:
	$(COMPOSE) exec app mvn test

mvn-clean:
	$(COMPOSE) exec app mvn clean

mvn-compile:
	$(COMPOSE) exec app mvn compile

java-version:
	$(COMPOSE) exec app java -version

mvn-version:
	$(COMPOSE) exec app mvn -version

shell:
	docker exec -it $(APP_CONTAINER) bash

# Uso: make run MAIN_CLASS=com.nanoprojeto.delivery.App
run:
	$(COMPOSE) exec app mvn compile exec:java -Dexec.mainClass=$(MAIN_CLASS)