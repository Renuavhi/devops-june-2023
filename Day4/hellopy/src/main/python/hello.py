#!/usr/bin/python3

class Hello:
    def __init__(self):
        print( "Hello constructor")

    def sayHello(self):
        return "Hello Python!"

if __name__ == "__main__": 
    hello = Hello()
    print ( hello.sayHello() )
