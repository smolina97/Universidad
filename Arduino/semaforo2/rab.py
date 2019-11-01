from tkinter import *
from time import *
import smbus

bus = smbus.SMBus(1)
address = 0x06
start = time()
info = " "


def readNumber():
    number1 = bus.read_byte(address)
    return number1


while True:
    number = readNumber()


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

            if number == 0x01:
                self.color.set("Rojo")

            elif number == 0x02:
                self.color.set("Amarillo")

            elif number == 0x03:
                self.color.set("Verde")

            elif number == 0x04:
                self.pcolor.set("Peaton Rojo")

            elif number == 0x05:
                self.pcolor.set("Peaton Verde")

            self.canvas = Canvas(window, width=150, height=200, bg="black")
            self.canvas.pack()

            self.car_red = self.canvas.create_oval(20, 20, 60, 60, fill="darkred")
            self.car_yellow = self.canvas.create_oval(20, 70, 60, 110, fill="yellow4")
            self.car_green = self.canvas.create_oval(20, 120, 60, 160, fill="darkgreen")

            self.pedestrian_red = self.canvas.create_oval(135, 40, 95, 80, fill="darkred")
            self.pedestrian_green = self.canvas.create_oval(135, 90, 95, 130, fill="darkgreen")

            texto_info = "Semaforo no esta conectado"
            self.tinfo.delete("1.0", END)
            self.tinfo.insert("1.0", texto_info)

            window.mainloop()
            global start
            global info
            now = time()
            color = self.color.get()
            pcolor = self.pcolor.get()

            if int(now - start) >= 15 and pcolor == "Peaton Rojo":
                info = "Aprete en boton para que el semaforo cambie: " + str(int(now - start))
            else:
                info = "Tiempo en Verde: " + str(int(now - start))

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

            texto_info = "Semaforo Carros: " + color + "\n"
            texto_info += "Semaforo Peatonal: " + pcolor + "\n"
            texto_info += info

            self.tinfo.delete("1.0", END)
            self.tinfo.insert("1.0", texto_info)
            self.tinfo.after(1000, self.__init__())

    TrafficLights()
