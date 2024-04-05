# SE NECESITA INSTALAR PYDUB CON: pip install pydub
# SE NECESITA INSTALAR FFmpeg Y AGREGARLO AL PATH

import argparse
import os
import time

from multiprocessing import Pool
from pydub import AudioSegment
from io import BytesIO


def convertir_a_mp3(audio_cd):
    start_time = time.time()
    print("Convirtiendo audio MP3")
    output = BytesIO()
    audio_cd.export(output, format="mp3")
    end_time = time.time()
    print(f"Tiempo transcurrido para conversión a MP3: {(end_time - start_time):.2f} segundos")
    return output.getvalue()


def convertir_a_ogg(audio_cd):
    start_time = time.time()
    print("Convirtiendo audio OGG")
    output = BytesIO()
    audio_cd.export(output, format="ogg")
    end_time = time.time()
    print(f"Tiempo transcurrido para conversión a OGG: {(end_time - start_time):.2f} segundos")
    return output.getvalue()


def convertir_a_flac(audio_cd):
    start_time = time.time()
    print("Convirtiendo audio FLAC")
    output = BytesIO()
    audio_cd.export(output, format="flac")
    end_time = time.time()
    print(f"Tiempo transcurrido para conversión a FLAC: {(end_time - start_time):.2f} segundos")
    return output.getvalue()


def guardar_archivo(nombre_archivo, contenido):
    with open(nombre_archivo, "wb") as f:
        f.write(contenido)
    print(f"Archivo guardado como {nombre_archivo}")


def procesar_formato(archivo, nombre_archivo, formato):
    print(f"Procesando {nombre_archivo}")
    if formato == "mp3":
        # mp3_content = convertir_a_mp3(archivo)1
        # guardar_archivo(f"{nombre_archivo}.mp3", mp3_content)
        archivo.export(f"{os.path.splitext(os.path.basename(nombre_archivo))[0]}.mp3", format="mp3")
        print(f"Archivo guardado como: {os.path.splitext(os.path.basename(nombre_archivo))[0]}.mp3")
    if formato == "ogg":
        # ogg_content = convertir_a_ogg(archivo)
        # guardar_archivo(f"{nombre_archivo}.ogg", ogg_content)
        archivo.export(f"{os.path.splitext(os.path.basename(nombre_archivo))[0]}.ogg", format="ogg")
        print(f"Archivo guardado como: {os.path.splitext(os.path.basename(nombre_archivo))[0]}.ogg")
    if formato == "flac":
        # flac_content = convertir_a_flac(archivo)
        # guardar_archivo(f"{nombre_archivo}.flac", flac_content)
        archivo.export(f"{os.path.splitext(os.path.basename(nombre_archivo))[0]}.flac", format="flac")
        print(f"Archivo guardado como: {os.path.splitext(os.path.basename(nombre_archivo))[0]}.flac")


def convertir_carpeta(carpeta, formato):
    start_time = time.time()
    archivo = [archivo for archivo in os.listdir(carpeta) if archivo.endswith("aif")]
    print(archivo)
    with Pool() as pool:
        pool.starmap(procesar_formato, [(AudioSegment.from_file(os.path.join(carpeta, archivo)),
                                         archivo, formato) for archivo in archivo])

    end_time = time.time()  # Finalizar el contador de tiempo°
    print(f"Tiempo transcurrido: {(end_time - start_time):.2f} segundos")


def main():
    start_time = time.time()
    # Crear y configurar Parser para obtener argumentos
    parser = argparse.ArgumentParser(description="---")
    parser.add_argument("-f", "--file", required=True, help="Ruta al archivo AIFF a convertir o a la carpeta")
    parser.add_argument("-e", "--encoding", type=str, choices=["mp3", "ogg", "flac"],
                        help="Formato al que se desea convertir (mp3, ogg, flac)", required=False)
    args = parser.parse_args()

    # Comprobar si file es una carpeta
    if os.path.isdir(args.file):
        if args.encoding is not None:
            convertir_carpeta(args.file, args.encoding)
        else:
            print("No se sabe a que formato convertir la carpeta")
        return

    # Si es un archivo .aif cargarlo
    if args.file.endswith("aif"):
        audio_cd = AudioSegment.from_file(args.file, format="aiff")
    else:
        print("La dirección no es una carpeta ni un archivo valido")
        return

    with Pool(processes=3) as pool:
        mp3_result = pool.apply_async(convertir_a_mp3, (audio_cd,))
        ogg_result = pool.apply_async(convertir_a_ogg, (audio_cd,))
        flac_result = pool.apply_async(convertir_a_flac, (audio_cd,))

        mp3_content = mp3_result.get()
        ogg_content = ogg_result.get()
        flac_content = flac_result.get()

    # Convertir a MP3, AAC y FLAC
    # mp3_content = convertir_a_mp3(audio_cd)
    # ogg_content = convertir_a_ogg(audio_cd)
    # flac_content = convertir_a_flac(audio_cd)

    end_time = time.time()  # Finalizar el contador de tiempo
    print(f"Tiempo total transcurrido: {(end_time - start_time):.2f} segundos")

    # Calcular Pesos de cada archivo
    sizeMP3 = len(mp3_content) / 1024 / 1024
    sizeOGG = len(ogg_content) / 1024 / 1024
    sizeFLAC = len(flac_content) / 1024 / 1024

    # Mostrar opciones al usuario
    print("\nOpciones disponibles:")
    print(f"1. MP3 (Tamaño Estimado: {sizeMP3:.2f}MB)")
    print(f"2. OGG (Tamaño Estimado: {sizeOGG:.2f}MB)")
    print(f"3. FLAC (Tamaño Estimado: {sizeFLAC:.2f}MB)")

    while True:
        opcion = input("Elija el formato que desea guardar en disco: ")
        if opcion == "1":
            guardar_archivo(f"{os.path.splitext(os.path.basename(args.file))[0]}.mp3", mp3_content)
            break
        elif opcion == "2":
            guardar_archivo(f"{os.path.splitext(os.path.basename(args.file))[0]}.ogg", ogg_content)
            break
        elif opcion == "3":
            guardar_archivo(f"{os.path.splitext(os.path.basename(args.file))[0]}.flac", flac_content)
            break
        else:
            print("Opción no válida por favor elija entre las siguientes opciones")
            print(f"1. MP3 (Tamaño Estimado: {sizeMP3:.2f}MB)")
            print(f"2. OGG (Tamaño Estimado: {sizeOGG:.2f}MB)")
            print(f"3. FLAC (Tamaño Estimado: {sizeFLAC:.2f}MB)")


if __name__ == "__main__":
    main()
