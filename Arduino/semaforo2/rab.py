from tkinter import *
from time import *
from ubidots import ApiClient

import smbus
import RPi.GPIO as GPIO

bus = smbus.SMBus(1)
address = 0x06
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
api = ApiClient(token='BBFF-QqEBRqhTmEsSj6STt6bOgJoKelwO4z')
MODO_CONTROLADOR = api.get_variable("5dd297cc8683d5247121dd13")

modoControl = 11
modRojoCarros = 17
modAmarilloCarros = 27
modVerdeCarros = 22
modRojoPeaton = 10
modVerdePeaton = 9
Datos_Eviados = 0

start = time()
info = " "


class TrafficLights:

    def __init__(self):

        GPIO.setup(modoControl, GPIO.OUT)
        GPIO.setup(modRojoCarros, GPIO.OUT)
        GPIO.setup(modAmarilloCarros, GPIO.OUT)
        GPIO.setup(modVerdeCarros, GPIO.OUT)
        GPIO.setup(modRojoPeaton, GPIO.OUT)
        GPIO.setup(modVerdePeaton, GPIO.OUT)


        window = Tk()
        window.title("Traffic Lights")
        window.resizable(width=False, height=False)
        frame = Frame(window)
        frame.pack()

        self.tinfo = Text(width=50, height=10)
        self.tinfo.pack(side=RIGHT)
        self.color = StringVar()
        self.pcolor = StringVar()

        self.canvas = Canvas(window, width=150, height=200, bg="black")
        self.canvas.pack()

        self.car_red = self.canvas.create_oval(20, 20, 60, 60, fill="darkred")
        self.car_yellow = self.canvas.create_oval(
            20, 70, 60, 110, fill="yellow4")
        self.car_green = self.canvas.create_oval(
            20, 120, 60, 160, fill="darkgreen")

        self.pedestrian_red = self.canvas.create_oval(
            135, 40, 95, 80, fill="darkred")
        self.pedestrian_green = self.canvas.create_oval(
            135, 90, 95, 130, fill="darkgreen")

        texto_info = self.color.get()
        self.tinfo.delete("1.0", END)
        self.tinfo.insert("1.0", texto_info)
        window.after(100, self.update)
        window.mainloop()

    def update(self):

        datos = bus.read_i2c_block_data(address, 0, 5)
        datosEnviar = [0, 0, 0, 0, 0, 0]

        rojoCarros = datos[0]
        amarilloCarros = datos[1]
        verdeCarros = datos[2]
        rojoPeaton = datos[3]
        verdePeaton = datos[4]

        rojo_Carros = datos[0]
        amarillo_Carros = datos[1]
        verde_Carros = datos[2]
        rojo_Peaton = datos[3]
        verde_Peaton = datos[4]

        global start
        global info
        now = time()
        color = self.color.get()
        pcolor = self.pcolor.get()

        if (GPIO.input(modRojoCarros)) == 1:
            datosEnviar[0] = 1
        else:
            datosEnviar[0] = 0

        if (GPIO.input(modAmarilloCarros)) == 1:
            datosEnviar[1] = 1

        else:
            datosEnviar[1] = 0

        if (GPIO.input(modVerdeCarros)) == 1:
            datosEnviar[2] = 1

        else:
            datosEnviar[2] = 0

        if (GPIO.input(modRojoPeaton)) == 1:
            datosEnviar[3] = 1

        else:
            datosEnviar[3] = 0

        if (GPIO.input(modVerdePeaton)) == 1:
            datosEnviar[4] = 1

        else:
            datosEnviar[4] = 0

        if (GPIO.input(modoControl)) == 1:
            datosEnviar[5] = 1

        else:
            datosEnviar[5] = 0

        bus.write_i2c_block_data(address, 0, datosEnviar)

        if rojoCarros == 1:
            self.canvas.itemconfig(self.car_red, fill="red")
            color = "Rojo"
            Datos_Eviados = 1

        else:
            self.canvas.itemconfig(self.car_red, fill="darkred")
            Datos_Eviados = 2

        if amarilloCarros == 1:
            self.canvas.itemconfig(self.car_yellow, fill="yellow")
            color = "Amarillo"
            Datos_Eviados = 10 + Datos_Eviados
        else:
            self.canvas.itemconfig(self.car_yellow, fill="yellow4")
            Datos_Eviados = 20 + Datos_Eviados

        if verdeCarros == 1:
            self.canvas.itemconfig(self.car_green, fill="lime")
            color = "Verde"
            Datos_Eviados = 100 + Datos_Eviados

        else:
            self.canvas.itemconfig(self.car_green, fill="darkgreen")
            Datos_Eviados = 200 + Datos_Eviados

        if rojoPeaton == 1:
            self.canvas.itemconfig(self.pedestrian_red, fill="red")
            pcolor = "Rojo"
            Datos_Eviados = 1000 + Datos_Eviados

        else:
            self.canvas.itemconfig(self.pedestrian_red, fill="darkred")
            Datos_Eviados = 2000 + Datos_Eviados


        if verdePeaton == 1:
            self.canvas.itemconfig(self.pedestrian_green, fill="lime")
            pcolor = "Verde"
            Datos_Eviados = 10000 + Datos_Eviados


        else:
            self.canvas.itemconfig(self.pedestrian_green, fill="darkgreen")
            Datos_Eviados = 20000 + Datos_Eviados

        MODO_CONTROLADOR.save_value({'value':Datos_Eviados})
        texto_info = "Semaforo Carros: " + color + "\n"
        texto_info += "Semaforo Peatonal: " + pcolor + "\n"
        texto_info += info

        self.tinfo.delete("1.0", END)
        self.tinfo.insert("1.0", texto_info)
        self.tinfo.after(100, self.update)


TrafficLights()
