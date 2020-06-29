# -*- coding: UTF-8 -*-
import socket
import time
import random
import string


def store_results(plain_text, cipher_text):
    filename = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    file = open("data_" + filename + ".csv", "w")
    assert plain_text.__len__() == cipher_text.__len__()
    try:
        for p, c in zip(plain_text, cipher_text):
            file.write(p + "," + c + '\n')
        file.close()
    except Exception as e:
        print("[Server]Error while storing: " + str(e))
        return 0
    return 1


def generate_rand_str(str_len):
    rand_str = ''.join(random.sample(string.ascii_letters + string.digits, str_len))
    return rand_str


def parse_input(user_input):
    instructions, times = "", 1
    user_input = user_input.lower()
    if user_input == "stop":
        instructions = "110"
    else:
        params = user_input.split('/')
        instructions = "100;" + ';'.join([i for i in params[:2]])
        times = int(params[-1]) if params.__len__() is 3 else 1

    return instructions, times


class AndroidServer:
    host = ''  # empty for localhost
    port = 0  # port
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # socket object
    conn, client_addr = None, None  # socket connection and client ip address
    plain_text = []
    cipher_text = []
    plain_len = 16

    def __init__(self, port=9090):
        self.port = port
        self.s.bind((self.host, port))
        self.s.listen(4)

    def connect(self):
        print("[Server] Waiting for Client connecting")

        while True:
            try:
                self.conn, self.client_addr = self.s.accept()
                data = self.conn.recv(1024)
                if data == b"200":
                    print('[Server] Connected by', self.client_addr[0])
                    break
            except socket.error as e:
                print("[Server] Error while sending : " + str(e))

    def run(self):
        # start transmitting data
        while True and self.conn:
            # sending the user instruction
            user_input = input("[User] Input your desired service (e.g. aes/enc/10 or aes/enc): ")
            if not user_input.__len__():
                continue
            # parse user input
            instructions, times = parse_input(user_input)
            self.plain_text, self.cipher_text = [], []
            stopped = False

            while times:  # may be multiple times
                if instructions == "110":
                    server_msg = instructions + "\n"
                else:
                    plain = generate_rand_str(self.plain_len)
                    print("[Server] Generating Plain text : ", plain)
                    self.plain_text.append(plain)
                    server_msg = instructions + ";" + plain + "\n"  # line separator needed

                # sending the instruction
                self.conn.sendall(bytearray(server_msg, "utf-8"))
                # receiving the client result
                client_msg = self.conn.recv(1024)
                msg = client_msg.split(b";")
                times -= 1
                if msg[0] == b'210':  # stop
                    print("[Server] Good Bye.")
                    stopped = True
                    self.conn.close()
                    self.s.close()
                    break
                elif msg[0] == b'201':  # receive the result
                    self.cipher_text.append(msg[1].decode())
                    print("[Server] Receiving message : ", repr(msg[1]))
                else:
                    print("[Server] Unrecognized Response Code")

            if stopped is True or not store_results(self.plain_text, self.cipher_text):
                break

    def close(self):
        self.conn.close()
        self.s.close()
