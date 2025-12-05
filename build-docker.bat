@echo off
echo Building SpeedyGhast with Docker...

REM Build the image
docker build -t speedyghast-builder .

REM Create a container to copy the jar from
docker create --name speedyghast-temp speedyghast-builder

REM Copy the jar to the current directory
if not exist "build\libs" mkdir build\libs
docker cp speedyghast-temp:/home/gradle/project/build/libs/. build/libs/

REM Remove the temporary container
docker rm speedyghast-temp

echo.
echo Build complete! Check build/libs/ for your jar file.
pause
