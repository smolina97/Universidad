from tkinter import *
from time import *
from ubidots import ApiClient

import smbus
import RPi.GPIO as GPIO

bus = smbus.SMBus(1)
address = 0x06
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

modoControl = 11
modRojoCarros = 17
modAmarilloCarros = 27
modVerdeCarros = 22
modRojoPeaton = 10
modVerdePeaton = 9

api = ApiClient(token='BBFF-QqEBRqhTmEsSj6STt6bOgJoKelwO4z')

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

        datos = bus.read_i2c_block_data(address, 0, 6)
        datosEnviar = [0, 0, 0, 0, 0, 0]

        rojoCarros = datos[0]
        amarilloCarros = datos[1]
        verdeCarros = datos[2]
        rojoPeaton = datos[3]
        verdePeaton = datos[4]
        boton_En = datos[5]

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


        if boton_En == 1:

            new_boton = 1
        else:
            new_boton = 0


        if rojoCarros == 1:
            self.canvas.itemconfig(self.car_red, fill="red")
            color = "Rojo"
            new_carros_rojo = 1
        else:
            self.canvas.itemconfig(self.car_red, fill="darkred")
            new_carros_rojo = 0

        if amarilloCarros == 1:
            self.canvas.itemconfig(self.car_yellow, fill="yellow")
            color = "Amarillo"
            new_carros_amarillo = 1
        else:
            self.canvas.itemconfig(self.car_yellow, fill="yellow4")
            new_carros_amarillo = 0

        if verdeCarros == 1:
            self.canvas.itemconfig(self.car_green, fill="lime")
            color = "Verde"
            new_carros_verde = 1
        else:
            self.canvas.itemconfig(self.car_green, fill="darkgreen")
            new_carros_verde = 0
            
        if rojoPeaton == 1:
            self.canvas.itemconfig(self.pedestrian_red, fill="red")
            pcolor = "Rojo"
            new_peaton_rojo = 1
        else:
            self.canvas.itemconfig(self.pedestrian_red, fill="darkred")
            new_peaton_rojo = 0

        if verdePeaton == 1:
            self.canvas.itemconfig(self.pedestrian_green, fill="lime")
            pcolor = "Verde"
            new_peaton_verde = 1
        else:
            self.canvas.itemconfig(self.pedestrian_green, fill="darkgreen")
            new_peaton_verde = 0

        bus.write_i2c_block_data(address, 0, datosEnviar)
        api.save_collection([{'variable': '5dd297b48683d522e9f2c5b0', 'value': new_carros_rojo}, {'variable': '5dd2940a1d847225901ba46e', 'value':new_carros_amarillo},
                             {'variable': '5dd293fd1d8472286057c797', 'value':new_carros_verde}, {'variable': '5dd293e31d8472288eb77ae0', 'value':new_peaton_rojo},
                             {'variable': '5dd293db1d8472277fec0167', 'value':new_peaton_verde}, {'variable': '5dd297bd8683d52369ce2c99', 'value':new_boton}])
        
        texto_info = "Semaforo Carros: " + color + "\n"
        texto_info += "Semaforo Peatonal: " + pcolor + "\n"
        texto_info += info

        self.tinfo.delete("1.0", END)
        self.tinfo.insert("1.0", texto_info)
        self.tinfo.after(10, self.update)     

TrafficLights()
