#!/usr/bin/python3

import unittest

from hello import *

class HelloTest( unittest.TestCase ):

    def test_sayHello(self):
        hello = Hello()
        self.assertEqual ( "Hello Python!", hello.sayHello() ) 

if __name__ == "__main__":
    unittest.main()
