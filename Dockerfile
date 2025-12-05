# Use an official Gradle image to build the project
FROM gradle:8.10-jdk21 AS build

# Set the working directory
WORKDIR /home/gradle/project

# Copy the project files
COPY --chown=gradle:gradle . .

# Build the project
# We use --no-daemon because this is a one-off build
RUN gradle build --no-daemon

# This stage is just to keep the image small if we wanted to run it, 
# but for a mod build, the artifact is what matters.
# We can use a scratch image to export the artifact if we used BuildKit custom outputs,
# but standard docker build leaves it in the image.
# We will instruct the user to copy it out.
