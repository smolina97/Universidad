import asyncio
import re
import threading
import pandas



direccion_servidor = ('localhost', 12345)
robots = []
logs = []

cola_robot = asyncio.Queue()
cola_log = asyncio.Queue()
lock_robot = asyncio.Lock()
lock_log = asyncio.Lock()

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

async def procesar_cola(cola, lock):
    while True:
        # Espera a que haya un dato en la cola
        tarea = await cola.get()
        operacion = tarea[0]
        tabla = tarea[1]

        if operacion == 'actualizar':
            clave, valor, nuevo_dato = tarea[2], tarea[3], tarea[4]
        elif operacion == 'agregar':
            nuevo_dato = tarea[2]
            clave = valor = None
        elif operacion == 'eliminar':
            clave, valor = tarea[2], tarea[3]
            nuevo_dato = None

        # Realiza la operación correspondiente
        async with lock:
            if operacion == 'actualizar':
                tabla = await actualizar_datos(tabla, clave, valor, nuevo_dato)
               # print ("Datos actualizados")
            elif operacion == 'agregar':
                tabla = await agregar_datos(tabla, nuevo_dato)
                #print ("Datos agregados")
            elif operacion == 'eliminar':
                tabla = await eliminar_datos(tabla, clave, valor)
                #print ("Datos eliminados")
        # Indica que la tarea está terminada
        cola.task_done()


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
        await cola_robot.put(('actualizar', robots, 'id', id, robot))
        #robots = await actualizar_datos(robots, 'id', id, robot)
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
        await cola_log.put(('agregar',logs, log))
        #logs = await agregar_datos(logs, log)
        return robots, logs

    return robots, logs

async def guardar_datos(robots, logs):
    if robots:  # Verificar que la lista no esté vacía
        df_robots = pandas.DataFrame(robots)
        df_robots.to_csv('robots.csv', index=False, encoding='utf-8')
    if logs:  # Verificar que la lista no esté vacía
        df_logs = pandas.DataFrame(logs)
        df_logs.to_csv('logs.csv', index=False, encoding='utf-8')


async def guardar_datos_periodicamente(robots, logs):
    while True:
        if cola_robot and cola_log:
        # Guardar los datos
            await guardar_datos(robots, logs)
            # Esperar un cierto tiempo antes de guardar los datos de nuevo
            await asyncio.sleep(0.5) 

def cargar_datos():
    global robots, logs
    try:
        robots = pandas.read_csv('robots.csv').to_dict('records')
        print("Datos anteriores de Robots cargados")
    except FileNotFoundError:
        print("No se encontraron datos anteriores de Robots")
        robots = []
    try:
        logs = pandas.read_csv('logs.csv').to_dict('records')
        print("Datos anteriores de Logs cargados")
    except FileNotFoundError:
        print("No se encontraron datos anteriores de Logs")
        logs = []
    # print("robots: ", robots)
    # print("logs: ", logs)
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
       # print("El cliente cerró la conexión inesperadamente")
        pass
    finally:
        if 'guardar_datos_task' in locals():
            guardar_datos_task.cancel()
            await guardar_datos(robots, logs)
           # print("Robots almacenados en 'robots.json'")
        writer.close()

async def manejar_conexiones(direccion_servidor, robots, logs):
    server = await asyncio.start_server(lambda r, w: procesar_datos(r, w, robots, logs), *direccion_servidor)
    
    async with server:
        await server.serve_forever()

async def main():

    await asyncio.gather(
        manejar_conexiones(direccion_servidor, robots, logs),
        procesar_cola(cola_robot, lock_robot),
        procesar_cola(cola_log, lock_log)
    )

#---------------------------------------------------------------------------------------------------------------

def mostrar_menu():
    tabla = ""
    tipoBusqueda = ""
    valor = ""

    while True:
        while (tabla == ""):
            print("\nElige una Tabla para consultar")
            print("r. robots")
            print("l. logs")
            test = input("Tabla (r/l): ")

            if test == "r" or test == "robots":
                tabla = "robots"
            elif test == "l" or test == "logs":
                tabla = "logs"
            else:
                print("Digite una opción valida... ")

        while (tipoBusqueda == ""):
            print("\nElija un parametro de busqueda")
            print("1. Busqueda por id")
            print("2. Busqueda por tipo de robot")
            print("3. Mostrar toda la tabla")
            test = input("Parametro (1/2/3): ")

            if test == "1":
                tipoBusqueda = "id"
            elif test == "2":
                tipoBusqueda = "robotType"
            elif test == "3":
                tipoBusqueda = "all"
            else:
                print("Digite una opción valida... ")

        if tipoBusqueda != "all":
            while (valor == ""):
                print("\nElija un valor para su busqueda")
                valor = input("Valor: ")

        resultados = realizar_busqueda(tabla, tipoBusqueda, valor, robots, logs)
        
        df = pandas.DataFrame(resultados)
        print(df)

        # Guardar el DataFrame en un archivo de Excel
        filename = f"consulta_{tabla}.csv"
        df.to_csv(filename, index=False)
        print(f"La consulta se ha guardado en el archivo: {filename}")

        tabla = ""
        tipoBusqueda = ""
        valor = ""

def realizar_busqueda(table, searchType, value, robots, logs):
    resultados = []

    # Seleccionar el vector correspondiente según la tabla especificada
    vectorBusqueda = robots if table == "robots" else logs

    # Realizar la búsqueda según el tipo especificado
    if searchType == "all":
        resultados = vectorBusqueda
    else:
        for elemento in vectorBusqueda:
            if searchType == "id":
                if str(elemento["id"]) == str(value):
                    resultados.append(elemento)
            elif searchType == "robotType":
                if str(elemento["tipoRobot"]) == str(value):
                    resultados.append(elemento)

    return resultados



# Iniciar el servidor
if __name__ == '__main__':

    cargar_datos()

    #Iniciar hilo del menú
    hilo_menu = threading.Thread(target=mostrar_menu)
    hilo_menu.start()

    asyncio.run(main())


