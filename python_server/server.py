#!/usr/bin/env python3

import socket
import threading

# Server configuration
HOST = '127.0.0.1'  # Listen on all available network interfaces
PORT = 3601         # Port number

clients = []  # List to store connected clients

running = True

def handle_client(client_socket, address):
    """Handles incoming messages from a client and broadcasts them to others."""
    print(f"[+] New connection from {address}")
    clients.append(client_socket)
    try:
        while running:
            message = client_socket.recv(1024)
            if not message:
                print("[-] Client {address} has disconnected")
                break
            decoded_message = message.decode()

            echo_message = f"ECHO: [{decoded_message.strip()}]"
            print(echo_message)

            client_socket.sendall(f"{echo_message}\n".encode())
            # print(f"[Message from {address}]: {message.decode('utf-8')}")
    except ConnectionResetError:
        print(f"[-] Connection lost from {address}")
    finally:
        clients.remove(client_socket)
        client_socket.close()

def start_server():
    global running
    """Starts the TCP server and listens for new connections."""
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind((HOST, PORT))
    server.listen(5)
    clients = []
    print(f"[*] Server listening on {HOST}:{PORT}")
    while running:
        try:
            client_socket, address = server.accept()
            client_thread = threading.Thread(target=handle_client, args=(client_socket, address))
            clients.append(client_thread)
            client_thread.start()
        except KeyboardInterrupt:
            running = False
            print("\b\b", end="")
            [c.join() for c in clients]
            print("** Server terminated **")
            


if __name__ == "__main__":
    start_server()