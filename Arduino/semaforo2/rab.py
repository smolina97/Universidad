bus = smbus.SMBus(1)
address = 0x06

def writeNumber(value):
    bus.write_i2c_block_data(address, 1, [5,0,1,255, 6]) #dummy array as of now. This can go upto 50 values
    return -1


def readNumber():
    # number = bus.read_byte(address)
    data_received_from_Arduino = bus.read_byte(address)
    for i in data_received_from_Arduino:
       print(i)

    return number

while i1:
    writeNumber(1)
    readNumber()

    #!/usr/bin/env python
# -*- coding: utf-8 -*-

from tkinter import *
from tkinter import ttk



class Aplicacion():
    def __init__(self):

        self.raiz = Tk()
        self.raiz.geometry('800x600')

        self.raiz.resizable(width=True, height=True)
        self.raiz.title('Ver info')

        self.tinfo = Text(self.raiz, width=60, height=30)

        # Sitúa la caja de texto 'self.tinfo' en la parte
        # superior de la ventana 'self.raiz':

        self.tinfo.pack(side=TOP)

        # Define el widget Button 'self.binfo' que llamará
        # al metodo 'self.verinfo' cuando sea presionado

        self.binfo = ttk.Button(self.raiz, text='Info',
                                command=self.verinfo)

        # Coloca el botón 'self.binfo' debajo y a la izquierda
        # del widget anterior

        self.binfo.pack(side=LEFT)

        # Define el botón 'self.bsalir'. En este caso
        # cuando sea presionado, el método destruirá o
        # terminará la aplicación-ventana 'self.raíz' con
        # 'self.raiz.destroy'

        self.bsalir = ttk.Button(self.raiz, text='Salir',
                                 command=self.raiz.destroy)

        # Coloca el botón 'self.bsalir' a la derecha del
        # objeto anterior.

        self.bsalir.pack(side=RIGHT)

        # El foco de la aplicación se sitúa en el botón
        # 'self.binfo' resaltando su borde. Si se presiona
        # la barra espaciadora el botón que tiene el foco
        # será pulsado. El foco puede cambiar de un widget
        # a otro con la tecla tabulador [tab]

        self.binfo.focus_set()
        self.raiz.mainloop()

    def verinfo(self):
        # Borra el contenido que tenga en un momento dado
        # la caja de texto

        self.tinfo.delete("1.0", END)

        # Obtiene información de la ventana 'self.raiz':

        info1 = self.raiz.winfo_class()
        info2 = self.raiz.winfo_geometry()
        info3 = str(self.raiz.winfo_width())
        info4 = str(self.raiz.winfo_height())
        info5 = str(self.raiz.winfo_rootx())
        info6 = str(self.raiz.winfo_rooty())
        info7 = str(self.raiz.winfo_id())
        info8 = self.raiz.winfo_name()
        info9 = self.raiz.winfo_manager()

        # Construye una cadena de texto con toda la
        # información obtenida:

        texto_info = "Clase de 'raiz': " + info1 + "\n"
        texto_info += "Resolución y posición: " + info2 + "\n"
        texto_info += "Anchura ventana: " + info3 + "\n"
        texto_info += "Altura ventana: " + info4 + "\n"
        texto_info += "Pos. Ventana X: " + info5 + "\n"
        texto_info += "Pos. Ventana Y: " + info6 + "\n"
        texto_info += "Id. de 'raiz': " + info7 + "\n"
        texto_info += "Nombre objeto: " + info8 + "\n"
        texto_info += "Gestor ventanas: " + info9 + "\n"

        # Inserta la información en la caja de texto:

        self.tinfo.insert("1.0", texto_info)


def main():
    mi_app = Aplicacion()
    return 0


if __name__ == '__main__':
    main()

