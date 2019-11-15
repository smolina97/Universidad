from tkinter import *
from time import *
import smbus
import RPi.GPIO as GPIO

bus = smbus.SMBus(1)
address = 0x06
GPIO.setmode(GPIO.BCM)
GPIO.setup(17, GPIO.OUT)
start = time()
info = " "


class TrafficLights:

    def __init__(self):

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

        number = bus.read_byte(address)
        peaton = int(number/10)
        carros = number % 10

        global start
        global info
        now = time()
        color = self.color.get()
        pcolor = self.pcolor.get()

        if (GPIO.input(17)):
             bus.write_byte(address, 1)

        if carros == 1:
            self.color.set("Rojo")

        elif carros == 2:
            self.color.set("Amarillo")

        elif carros == 3:
            self.color.set("Verde")

        if peaton == 1:
            self.pcolor.set("Peaton Rojo")

        elif peaton == 2:
            self.pcolor.set("Peaton Verde")

        elif peaton == 0:
            self.pcolor.set("Peaton Titilando")

        if color == "Rojo":
            self.canvas.itemconfig(self.car_red, fill="red")
            self.canvas.itemconfig(self.car_yellow, fill="yellow4")
            self.canvas.itemconfig(self.car_green, fill="darkgreen")
            start = now

        elif color == "Amarillo":
            self.canvas.itemconfig(self.car_red, fill="darkred")
            self.canvas.itemconfig(self.car_yellow, fill="yellow")
            self.canvas.itemconfig(self.car_green, fill="darkgreen")

        elif color == "Verde":
            self.canvas.itemconfig(self.car_red, fill="darkred")
            self.canvas.itemconfig(self.car_yellow, fill="yellow4")
            self.canvas.itemconfig(self.car_green, fill="lime")

        if pcolor == "Peaton Rojo":
            self.canvas.itemconfig(self.pedestrian_red, fill="red")
            self.canvas.itemconfig(self.pedestrian_green, fill="darkgreen")

        elif pcolor == "Peaton Verde":
            self.canvas.itemconfig(self.pedestrian_red, fill="darkred")
            self.canvas.itemconfig(self.pedestrian_green, fill="lime")
            info = "Peaton en Verde"

        elif pcolor == "Peaton Titilando":
            self.canvas.itemconfig(self.pedestrian_red, fill="darkred")
            self.canvas.itemconfig(self.pedestrian_green, fill="darkgreen")
            info = "Va a cambiar a rojo"

        texto_info = "Semaforo Carros: " + color + "\n"
        texto_info += "Semaforo Peatonal: " + pcolor + "\n"
        texto_info += info

        self.tinfo.delete("1.0", END)
        self.tinfo.insert("1.0", texto_info)
        self.tinfo.after(100, self.update)


TrafficLights()
