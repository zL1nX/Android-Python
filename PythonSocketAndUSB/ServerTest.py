# -*- coding: UTF-8 -*-

# python version 3.7
from Server import AndroidServer


def server_main():
    server = AndroidServer()
    server.connect()
    server.run()  # main loop
    server.close()


server_main()
