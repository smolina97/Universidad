from tkinter import *
from time import *
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

        rojoCarros = datos[0]
        amarilloCarros = datos[1]
        verdeCarros = datos[2]
        rojoPeaton = datos[3]
        verdePeaton = datos[4]

        global start
        global info
        now = time()
        color = self.color.get()
        pcolor = self.pcolor.get()

        if (GPIO.input(modRojoCarros)) == 1:
            bus.write_byte(address, 0)
        else:
            bus.write_byte(address, 1)

        if (GPIO.input(modAmarilloCarros)) == 1:
            bus.write_byte(address, 2)
        else:
            bus.write_byte(address, 3)

        if (GPIO.input(modVerdeCarros)) == 1:
            bus.write_byte(address, 4)
        else:
            bus.write_byte(address, 5)

        if (GPIO.input(modRojoPeaton)) == 1:
            bus.write_byte(address, 6)
        else:
            bus.write_byte(address, 7)

        if (GPIO.input(modVerdePeaton)) == 1:
            bus.write_byte(address, 8)
        else:
            bus.write_byte(address, 9)

        if (GPIO.input(modoControl)) == 1:
            bus.write_byte(address, 10)
        else: 
            bus.write_byte(address, 11)

        if rojoCarros == 0:
            self.canvas.itemconfig(self.car_red, fill="red")
            color = "Rojo"
        else:
            self.canvas.itemconfig(self.car_red, fill="darkred")

        if amarilloCarros == 2:
            self.canvas.itemconfig(self.car_yellow, fill="yellow")
            color = "Amarillo"
        else:
            self.canvas.itemconfig(self.car_yellow, fill="yellow4")

        if verdeCarros == 4:
            self.canvas.itemconfig(self.car_green, fill="lime")
            color = "Verde"

        else:
            self.canvas.itemconfig(self.car_green, fill="darkgreen")

        if rojoPeaton == 6:
            self.canvas.itemconfig(self.pedestrian_red, fill="red")
            pcolor = "Rojo"

        else:
            self.canvas.itemconfig(self.pedestrian_red, fill="darkred")

        if verdePeaton == 8:
            self.canvas.itemconfig(self.pedestrian_green, fill="lime")
            pcolor = "Verde"

        else:
            self.canvas.itemconfig(self.pedestrian_green, fill="darkgreen")

        texto_info = "Semaforo Carros: " + color + "\n"
        texto_info += "Semaforo Peatonal: " + pcolor + "\n"
        texto_info += info

        self.tinfo.delete("1.0", END)
        self.tinfo.insert("1.0", texto_info)
        self.tinfo.after(100, self.update)


TrafficLights()
