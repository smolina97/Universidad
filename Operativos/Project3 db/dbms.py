import socket

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(('localhost', 12345))
server_socket.listen()

robot_dict = {}


while True:
    client_socket, addr = server_socket.accept()
    log = client_socket.recv(1024).decode('utf-8')
    # tipo_robot, id_robot, mensaje = log.split(' - ')
    # robot_dict[id_robot] = {'tipo_robot': tipo_robot, 'mensaje': mensaje}
    print(log)
    client_socket.close()
