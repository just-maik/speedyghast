.PHONY: all build clean docker-build gen-wrapper

# Default target
all: build

# Build locally using Gradle Wrapper
# Requires Java 21 installed locally
build:
	./gradlew build

# Clean build directory
clean:
	./gradlew clean

# Build using Docker (no local Java/Gradle required)
# Mounts the current directory to the container so artifacts appear in build/libs/
docker-build:
	docker run --rm -u gradle -v "$(CURDIR):/home/gradle/project" -w /home/gradle/project gradle:8.10-jdk21 gradle build

# Generate Gradle Wrapper scripts using Docker
# Useful if you want to build locally but don't have Gradle installed to generate the wrapper
gen-wrapper:
	docker run --rm -u gradle -v "$(CURDIR):/home/gradle/project" -w /home/gradle/project gradle:8.10-jdk21 gradle wrapper
