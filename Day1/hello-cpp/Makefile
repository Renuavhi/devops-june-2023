all: bin/main.o bin/hello.o 
	g++ bin/main.o bin/hello.o -lstdc++ -o bin/hello.exe

bin/main.o: src/main.cpp
	mkdir -p bin
	g++ -c src/main.cpp -o bin/main.o

bin/hello.o: src/hello.cpp
	mkdir -p bin
	g++ -c src/hello.cpp -o bin/hello.o

clean:
	rm -rf bin

