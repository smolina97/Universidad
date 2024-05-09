import socket
import json
import re
import threading
import time

def procesar_mensaje(mensaje, robots, logs):
    patron_tipo1 = r"robot= id: (\d+) - tipoRobot: (\d+) - color: (\w+) - encendido: (\w+) - calle: (\d+) - avenida: (\d+) - beepers: (\d+)"
    patron_tipo2 = r"log= timestamp: (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - id: (\d+) - tipoRobot: (\d+) - calle: (\d+) - avenida: (\d+) - beepers: (\d+) - (.+)"
    coincidencia_tipo1 = re.match(patron_tipo1, mensaje)
    if coincidencia_tipo1:
        id, tipoRobot, color, encendido, calle, avenida, beepers = coincidencia_tipo1.groups()
        robot = {
            "id": id,
            "tipoRobot": tipoRobot,
            "color": color,
            "encendido": encendido,
            "calle": calle,
            "avenida": avenida,
            "beepers": beepers
        }
        for i, r in enumerate(robots):
            if r['id'] == id:
                robots[i] = robot
                break
        else:
            robots.append(robot)
        return robots, logs

    coincidencia_tipo2 = re.match(patron_tipo2, mensaje)
    if coincidencia_tipo2:
        timestamp, id, tipoRobot, calle, avenida, beepers, mensaje = coincidencia_tipo2.groups()
        log = {
            "timestamp": timestamp,
            "id": id,
            "tipoRobot": tipoRobot,
            "calle": calle,
            "avenida": avenida,
            "beepers": beepers,
            "mensaje": mensaje
        }
        logs.append(log)
        return robots, logs

    return robots, logs

def guardar_datos_periodicamente(robots, logs):
    while True:
        # Guardar los robots en un archivo JSON
        with open("robots.json", "w") as json_file:
            json.dump(robots, json_file, indent=4)

        # Guardar los logs en otro archivo JSON
        with open("logs.json", "w") as json_file:
            json.dump(logs, json_file, indent=4)

        print("Robots almacenados en 'robots.json'")
        print("Logs almacenados en 'logs.json'")

        # Esperar 30 segundos antes de guardar los datos nuevamente
        time.sleep(30)

# Crear el socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(('localhost', 12345))
server_socket.listen()

robots = []
logs = []

# Iniciar el hilo para guardar los datos periódicamente
hilo_guardar_datos = threading.Thread(target=guardar_datos_periodicamente, args=(robots, logs))
hilo_guardar_datos.start()

while True:

    client_socket, addr = server_socket.accept()

    data = client_socket.recv(1024).decode('utf-8')
    robots, logs = procesar_mensaje(data, robots, logs)
    client_socket.close()
