.PHONY: init-web init-desktop init-api init-all start-web start-desktop start-api

init-web:
	@echo "dependencies installeren voor web..."
	cd web/ && bun install

init-desktop:
	@echo "dependencies installeren voor desktop..."
	cd desktop/ && mvn dependency:resolve

init-api:
	@echo "dependencies installeren voor api..."
	cd api/ && mvn dependency:resolve

init-all: init-web init-desktop init-api
	@echo "Alle dependencies geïnstalleerd"

start-desktop:
	@echo "desktop opstarten..."
	cd desktop && ./mvnw javafx:run

start-api:
	@echo "api opstarten..."
	cd api && ./mvnw spring-boot:run

start-web:
	cd web/ && bun run dev


start-all:start-api start-desktop start-web
	@echo "Alle services gestart"

sync-env:
