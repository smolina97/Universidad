import multiprocessing
import asyncio
import re
import shelve


direccion_servidor = ('localhost', 12345)
robots = []
logs = []

# Patrones para buscar los datos en los mensajes
patron_tipo1 = re.compile(r"robot= id: (\d+) - tipoRobot: (\d+) - color: (\w+) - encendido: (\w+) - calle: (\d+) - avenida: (\d+) - beepers: (\d+)")
patron_tipo2 = re.compile(r"log= timestamp: (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - id: (\d+) - tipoRobot: (\d+) - calle: (\d+) - avenida: (\d+) - beepers: (\d+) - (.+)")


async def actualizar_datos(tabla, clave, valor, nuevo_dato):
    for i, r in enumerate(tabla):
        if r[clave] == valor:
            tabla[i] = nuevo_dato
            return tabla
    else:
        await agregar_datos(tabla, nuevo_dato)
    return tabla

async def agregar_datos(tabla, nuevo_dato):
    tabla.append(nuevo_dato)
    return tabla

async def eliminar_datos(tabla, clave, valor):
    tabla = [r for r in tabla if r[clave] != valor]
    return tabla


async def procesar_mensaje(mensaje, robots, logs):
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
        robots = await actualizar_datos(robots, 'id', id, robot)
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
        logs = await agregar_datos(logs, log)
        return robots, logs

    return robots, logs

async def guardar_datos(robots, logs):
    if robots:  # Verificar que la lista no esté vacía
        with shelve.open('robots.db') as db:
            db['robots'] = robots
    if logs:  # Verificar que la lista no esté vacía
        with shelve.open('logs.db') as db:
            db['logs'] = logs

async def guardar_datos_periodicamente(robots, logs):
    while True:
        # Guardar los datos
        await guardar_datos(robots, logs)
        # Esperar un cierto tiempo antes de guardar los datos de nuevo
        await asyncio.sleep(0.5) 

async def cargar_datos():
    global robots, logs
    with shelve.open('robots.db') as db:
        robots = db.get('robots', [])
    with shelve.open('logs.db') as db:
        logs = db.get('logs', [])
    return robots, logs

async def procesar_datos(reader, writer, robots, logs):
    try:
        mensaje_incompleto = b''  # Inicializar un buffer para manejar mensajes incompletos
        while True:
            data = await reader.read(4096)
            if not data:
                break  # Si no hay datos, salir del bucle
            mensaje_incompleto += data  # Agregar los datos recibidos al mensaje incompleto
            mensajes = mensaje_incompleto.split(b'\n')  # Dividir los datos en mensajes usando el delimitador
            mensaje_incompleto = mensajes.pop()  # El último elemento puede ser un mensaje incompleto, guárdalo para el próximo ciclo
            for mensaje in mensajes:
                #print(f"Mensaje recibido de {addr}: {mensaje.decode('utf-8')}")
                robots, logs = await procesar_mensaje(mensaje.decode('utf-8'), robots, logs)
                if robots and 'guardar_datos_task' not in locals():  # Si robots no está vacío y la tarea no se ha creado aún
                    guardar_datos_task = asyncio.create_task(guardar_datos_periodicamente(robots, logs))  # Crear la tarea
    except ConnectionResetError:
        print("El cliente cerró la conexión inesperadamente")
    
    finally:
        if 'guardar_datos_task' in locals():
            guardar_datos_task.cancel()
            await guardar_datos(robots, logs)
            print("Robots almacenados en 'robots.json'")
        writer.close()

async def manejar_conexiones(direccion_servidor, robots, logs):
    server = await asyncio.start_server(lambda r, w: procesar_datos(r, w, robots, logs), *direccion_servidor)
    
    async with server:
        await server.serve_forever()


# Iniciar el servidor
if __name__ == '__main__':
    asyncio.run(cargar_datos())
    asyncio.run(manejar_conexiones(direccion_servidor, robots, logs))


