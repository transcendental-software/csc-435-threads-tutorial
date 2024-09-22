## CSC 435 Multithreading Tutorial
**Jarvis College of Computing and Digital Media - DePaul University**

### Requirements

To run the C++ solution you will need to have GCC 14.x and CMake 3.28.x installed on your system. On Ubuntu 24.04 LTS you can install GCC and set it as default compiler using the following commands:

```
sudo apt install build-essential cmake g++-14 gcc-14 cmake
sudo update-alternatives --remove-all gcc
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-13 130
sudo update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-14 140
sudo update-alternatives --remove-all g++
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-13 130
sudo update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-14 140
```

To run the Java solution you will need to have Java 21.x and Maven 3.8.x installed on your systems. On Ubuntu 24.04 LTS you can install Java and Maven using the following commands:

```
sudo apt install maven openjdk-21-jdk

```

### C++ solution
#### How to build/compile

To build the C++ solution use the following commands:
```
cd app-cpp
mkdir build
cmake -S . -B build
cmake --build build --config Release
```

#### How to run application

To run the C++ solution (after you build the project) use the following command:
```
./build/run-tasks <number of worker threads> <number of tasks>
```

#### Example

```
./build/run-tasks 4 10
Worker 3 completed task 2 (5229506)
Worker 2 completed task 1 (6932)
Worker 4 completed task 3 (7238558)
Worker 1 completed task 0 (9350910)
Worker 2 completed task 5 (5918333)
Worker 4 completed task 6 (7941609)
Worker 3 completed task 4 (5570009)
Worker 1 completed task 7 (7588356)
Worker 2 completed task 8 (2550421)
Worker 4 completed task 9 (3824327)
```

### Java solution
#### How to build/compile

To build the Java solution use the following commands:
```
cd app-java
mvn compile
mvn package
```

#### How to run application

To run the Java solution (after you build the project) use the following command:
```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.RunTasks <number of worker threads> <number of tasks>
```

#### Example

```
java -cp target/app-java-1.0-SNAPSHOT.jar csc435.app.RunTasks 4 10
Worker 1 completed task 1 (6536490)
Worker 4 completed task 3 (9865992)
Worker 2 completed task 2 (6708618)
Worker 3 completed task 4 (1148802)
Worker 3 completed task 8 (1052421)
Worker 2 completed task 7 (3324156)
Worker 1 completed task 5 (2000304)
Worker 4 completed task 6 (4927527)
Worker 3 completed task 9 (2324344)
Worker 2 completed task 10 (9686705)
```
