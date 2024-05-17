import asyncio
import hashlib
import re
import shutil
import signal
import threading
import pandas
import os
import tempfile
from datetime import datetime



direccion_servidor = ('localhost', 12345)
robots = []
logs = []
estados = []

cola_robot = asyncio.Queue()
cola_log = asyncio.Queue()
cola_estado = asyncio.Queue()

lock_robot = asyncio.Lock()
lock_log = asyncio.Lock()
lock_estado = asyncio.Lock()

# Patrones para buscar los datos en los mensajes
patron_tipo1 = re.compile(r"robot= id: (\d+) - tipoRobot: (\d+) - color: (\w+) - encendido: (\w+) - calle: (\d+) - avenida: (\d+) - beepers: (\d+)")
patron_tipo2 = re.compile(r"log= timestamp: (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - id: (\d+) - tipoRobot: (\d+) - calle: (\d+) - avenida: (\d+) - beepers: (\d+) - (.+)")
patron_tipo3 = re.compile(r"estado= timestamp: (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - estado: (.+)")



async def actualizar_datos(tabla, clave, valor, nuevo_dato):
    try:
        for i, r in enumerate(tabla):
            if r[clave] == valor:
                tabla[i] = nuevo_dato
                return tabla
        else:
            await agregar_datos(tabla, nuevo_dato)
        return tabla
    except KeyError:
        print("Error: clave no existe")
        raise

async def agregar_datos(tabla, nuevo_dato):
    try:
        tabla.append(nuevo_dato)
        return tabla
    except KeyError:
        print("Error: clave no existe")
        raise

async def eliminar_datos(tabla, clave, valor):
    try:
        tabla[:] = [r for r in tabla if r[clave] != valor]
        return tabla
    except KeyError:
        print("Error: clave no existe")
        raise

async def ordenar_datos(tabla, clave):
    try:
        tabla[:] = sorted(tabla, key=lambda r: r[clave])
        return tabla
    except KeyError:
        print("Error: clave no existe")
        raise

async def procesar_cola(cola, lock , run_once = False):
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
        elif operacion == 'ordenar':
            clave = tarea[2]
            valor = nuevo_dato = None

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
            elif operacion == 'ordenar':
                tabla = await ordenar_datos(tabla, clave)
                #print ("Datos ordenados")
        # Indica que la tarea está terminada

        if cola.empty() and run_once:
            break

        cola.task_done()


async def procesar_mensaje(mensaje, robots, logs, estados):
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
        #print (cola_robot)
        return robots, logs, estados

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
        #print(cola_log)
        return robots, logs, estados

    coincidencia_tipo3 = re.match(patron_tipo3, mensaje)
    if coincidencia_tipo3:
        timestamp, estado = coincidencia_tipo3.groups()
        state = {
            "timestamp": timestamp,
            "estado": estado,
        }
        await cola_estado.put(('agregar',estados, state))
        #print(cola_estado)
        #estados = await agregar_datos(estados, state)
        #print(estados)
        return robots, logs, estados

    return robots, logs, estados

async def guardar_datos(datos, nombre_archivo):
    try:
        df_robots = pandas.DataFrame(datos)
        
        # Crear un archivo temporal
        with tempfile.NamedTemporaryFile(delete=False, mode='w', suffix='.csv', encoding='utf-8') as temp_file:
            temp_filename = temp_file.name
            df_robots.to_csv(temp_filename, index=False, encoding='utf-8')
            #print(f'Guardando datos en {temp_filename}')
        # Mover el archivo temporal al destino final
        shutil.move(temp_filename, f'{nombre_archivo}.csv')
        
    except Exception as e:
        print(f'Ocurrió un error al guardar los datos: {e}')
        if os.path.exists(temp_filename):
            os.remove(temp_filename)

def calcular_hash(datos):
    datos_str = str(datos)
    return hashlib.md5(datos_str.encode('utf-8')).hexdigest()

async def guardar_datos_periodicamente(robots, logs, estados):
    hash_robots_anterior = None
    hash_logs_anterior = None
    hash_estados_anterior = None
    
    while True:
        hash_robots_actual = calcular_hash(robots)
        hash_logs_actual = calcular_hash(logs)
        hash_estados_actual = calcular_hash(estados)
        
        if robots and hash_robots_actual != hash_robots_anterior:
            await guardar_datos(robots, 'robots')
            hash_robots_anterior = hash_robots_actual
        
        if logs and hash_logs_actual != hash_logs_anterior:
            await guardar_datos(logs, 'logs')
            hash_logs_anterior = hash_logs_actual
        
        if estados and hash_estados_actual != hash_estados_anterior:
            await guardar_datos(estados, 'estados')
            hash_estados_anterior = hash_estados_actual
        
        await asyncio.sleep(0.5)

def cargar_datos():
    global robots, logs, estados
    
    try:
        robots = pandas.read_csv('robots.csv', dtype=str).to_dict('records')
        print("Datos de Robots anteriores cargados correctamente")
    except (FileNotFoundError, pandas.errors.EmptyDataError):
        robots = []
        print("No se encontraron datos de Robots anteriores")
    try:
        logs = pandas.read_csv('logs.csv', dtype=str).to_dict('records')
        print("Datos de Logs anteriores cargados correctamente")
    except (FileNotFoundError, pandas.errors.EmptyDataError):
        logs = []
        print("No se encontraron datos de Logs anteriores")
    try:
        estados = pandas.read_csv('estados.csv', dtype=str).to_dict('records')
        print("Datos de Estados anteriores cargados correctamente")
    except (FileNotFoundError, pandas.errors.EmptyDataError):
        estados = []
        print("No se encontraron datos de Estados anteriores")

    return robots, logs, estados


async def procesar_datos(reader, writer, robots, logs, estados):
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
                robots, logs, estados = await procesar_mensaje(mensaje.decode('utf-8'), robots, logs, estados)
                if robots and 'guardar_datos_task' not in locals():  # Si robots no está vacío y la tarea no se ha creado aún
                    guardar_datos_task = asyncio.create_task(guardar_datos_periodicamente(robots, logs, estados))  # Crear la tarea
    except ConnectionResetError:
        print("\nEl cliente cerró la conexión inesperadamente")

        # Definir el formato deseado
        formato = "%Y-%m-%d %H:%M:%S"
        # Obtener la fecha y hora actual en el formato deseado
        timestamp = datetime.now().strftime(formato)
        msg = "estado= " + "timestamp: " + timestamp + " - estado: Apagado - Conexion Cerrada Inesperadamente"
        robots, logs, estados = await procesar_mensaje(msg, robots, logs, estados)

        if not cola_robot.empty():
            await procesar_cola(cola_robot, lock_robot, True)
        
        if not cola_log.empty():
            await procesar_cola(cola_log, lock_log, True)

        if not  cola_estado.empty() :
            await procesar_cola(cola_estado, lock_estado, True)


        print("Datos guardados correctamente")
        
    finally:
        if 'guardar_datos_task' in locals():
            guardar_datos_task.cancel()
            await guardar_datos(robots, 'robots')
            await guardar_datos(logs, 'logs')
            await guardar_datos(estados, 'estados')
            #print("Programa finalizado, Datos guardados correctamente")
        writer.close()

async def manejar_conexiones(direccion_servidor, robots, logs, estados):
    server = await asyncio.start_server(lambda r, w: procesar_datos(r, w, robots, logs, estados), *direccion_servidor)
    
    async with server:
        await server.serve_forever()

async def main():

 await asyncio.gather(
    manejar_conexiones(direccion_servidor, robots, logs, estados),
    procesar_cola(cola_robot, lock_robot),
    procesar_cola(cola_log, lock_log),
    procesar_cola(cola_estado, lock_estado),
    return_exceptions=True
)
    
#---------------------------------------------------------------------------------------------------------------

def mostrar_menu():
    opcion = ""
    tabla = ""
    tipoBusqueda = ""
    valor = ""

    try:
        while True:
            while (opcion == ""):
                print("\n¿Que quieres hacer?")
                print("1. Consultar Tablas")
                print("2. Crear/Actualizar Registro")
                print("3. Eliminar Registro")
                print("4. Ordenar Tabla")
                test = input("Opcion (1/2/3/4): ")

                if (test == "1"):
                    opcion = "consultar"
                elif (test == "2"):
                    opcion = "crear"
                elif (test == "3"):
                    opcion = "eliminar"
                elif (test == "4"):
                    opcion = "ordenar"
                else:
                    print("Digite una opción valida... ")
            if opcion == "consultar":
                while (tabla == ""):
                    print("\nElige una Tabla para consultar")
                    print("r. robots")
                    print("l. logs")
                    print("e. estados")
                    test = input("Tabla (r/l/e): ")

                    if test == "r" or test == "robots":
                        tabla = "robots"
                    elif test == "l" or test == "logs":
                        tabla = "logs"
                    elif test == "e" or test == "estados":
                        tabla = "estados"
                    else:
                        print("Digite una opción valida... ")

                if (tabla != "estados"):
                    while (tipoBusqueda == ""):
                        print("\nElija un parametro de busqueda")
                        print("1. Busqueda por id")
                        print("2. Busqueda por tipo de robot")
                        print("3. Mostrar todos los registros")
                        test = input("Parametro (1/2/3): ")

                        if test == "1":
                            tipoBusqueda = "id"
                        elif test == "2":
                            tipoBusqueda = "robotType"
                        elif test == "3":
                            tipoBusqueda = "todos"
                        else:
                            print("Digite una opción valida... ")

                    if tipoBusqueda != "todos":
                        while (valor == ""):
                            print("\nElija un valor para su busqueda")
                            valor = input("Valor: ")
                
                else:
                    while (tipoBusqueda == ""):
                        print("\nElija un parametro de busqueda")
                        print("1. Estado actual del Programa")
                        print("2. Log de los estados del Programa")
                        test = input("Parametro (1/2): ")

                        if test == "1":
                            tipoBusqueda = "actual"
                        elif test == "2":
                            tipoBusqueda = "todos"
                        else:
                            print("Digite una opción valida... ")

                resultados = realizar_busqueda(tabla, tipoBusqueda, valor, robots, logs, estados)
                df = pandas.DataFrame(resultados)
                print(df)

                # Guardar el DataFrame en un archivo de Excel
                filename = f"consulta_{tabla}.csv"
                df.to_csv(filename, index=False)

                tabla = ""
                tipoBusqueda = ""
                valor = ""
                opcion = ""
            elif (opcion == "crear"):
                asyncio.run(auto_enviar_mensaje())
                opcion = ""
            elif (opcion == "eliminar"):
                asyncio.run(eliminar_mensaje())
                opcion = ""
            elif (opcion == "ordenar"):
                asyncio.run(ordenar_tabla())
                opcion = ""
    except (EOFError, KeyboardInterrupt):
        print("Saliendo del menu.")

def realizar_busqueda(table, searchType, value, robots, logs, estados):
    resultados = []

    # Seleccionar el vector correspondiente según la tabla especificada
    if (table == "robots"):
        vectorBusqueda = robots
    elif (table == "logs"):
        vectorBusqueda = logs
    else:
        vectorBusqueda = estados

    if searchType != "todos":
        for elemento in vectorBusqueda:
                if searchType == "id":
                    if elemento["id"] == value:
                        resultados.append(elemento)
                elif searchType == "robotType":
                    if elemento["tipoRobot"] == value:
                        resultados.append(elemento)
                elif searchType == "actual":
                    resultados.append(vectorBusqueda[-1])
                    break
    else:
        resultados = vectorBusqueda

    return resultados

async def auto_enviar_mensaje():
    try:
        id = input("id: ")
        tipoRobot = input("tipoRobot: ")
        colorName = input("color: ")
        encendido = input("encendido: ")
        calleActual = input("calle actual: ")
        avenidaActual = input("avenida actual: ")
        beepersEnBolsa = input("beepers: ")

        robot = {
                "id": id,
                "tipoRobot": tipoRobot,
                "color": colorName,
                "encendido": encendido,
                "calle": calleActual,
                "avenida": avenidaActual,
                "beepers": beepersEnBolsa
            }
        await cola_robot.put(('actualizar', robots, 'id', id, robot))
        await procesar_cola(cola_robot, lock_robot, True)
        await guardar_datos(robots, 'robots')
        print("Mensaje enviado exitosamente")
    except KeyError:
        print("No se pudo hacer la operación")

async def eliminar_mensaje():
    try:
        id = input("Introduce el id del robot a eliminar: ")
        await cola_robot.put(('eliminar', robots, 'id', id))
        await procesar_cola(cola_robot, lock_robot, True)
        await guardar_datos(robots, 'robots')
        print("Eliminacion exitosa")
    except KeyError:
        print("No se pudo hacer la operación")

async def ordenar_tabla():
    try:
        clave = input("Introduce el campo por el cual deseas ordenar la tabla robots: ")
        await cola_robot.put(('ordenar', robots, clave))
        await procesar_cola(cola_robot, lock_robot, True)
        await guardar_datos(robots, 'robots')
        print("Ordenamiento exitoso")
    except KeyError:
        print("No se pudo hacer la operación")

def manejar_ctrl_c(sig, frame):
    print("\nSalida de base de datos")
    asyncio.create_task(guardar_y_salir())

async def guardar_y_salir():
    global robots, logs, estados

    if not cola_robot.empty():
        await procesar_cola(cola_robot, lock_robot, True)
        print("Datos de Robots guardados correctamente")
    if not cola_log.empty():
        await procesar_cola(cola_log, lock_log, True)
        print("Datos de Logs guardados correctamente")
    if not cola_estado.empty():
        await procesar_cola(cola_estado, lock_estado, True)
        print("Datos de Estados guardados correctamente")

    # Guardar datos
    await guardar_datos(robots, 'robots')
    await guardar_datos(logs, 'logs')
    await guardar_datos(estados, 'estados')

    os._exit(0)

# Iniciar el servidor
if __name__ == '__main__':

    cargar_datos()
    signal.signal(signal.SIGINT, manejar_ctrl_c)
    
    try:
        threading.Thread(target=mostrar_menu).start()
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\nSalida de base de datos")
    except Exception as e:
        print(f'Ocurrió un error: {e}')


