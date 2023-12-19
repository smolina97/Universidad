using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PruebaVector16

{
    class PruebaVector16
    {
        private string[] nombres;
        private int[] notas;
        public void Cargar()
        {
            nombres = new string[5];
            notas = new int[5];
            Console.WriteLine("Carga de nombres y notas");
            for (int f = 0; f < nombres.Length; f++)
            {
                Console.Write("Ingese el nombre del alumno:");
                nombres[f] = Console.ReadLine();
                Console.Write("Ingrese la nota del alumno:");
                string linea;
                linea = Console.ReadLine();
                notas[f] = int.Parse(linea);
            }
        }
        public void Ordenar()
        {
            for (int k = 0; k < notas.Length; k++)
            {
                for (int f = 0; f < notas.Length - 1 - k; f++)
                {
                    if (notas[f] < notas[f + 1])
                    {
                        int auxnota;
                        auxnota = notas[f];
                        notas[f] = notas[f + 1];
                        notas[f + 1] = auxnota;
                        string auxnombre;
                        auxnombre = nombres[f];
                        nombres[f] = nombres[f + 1];
                        nombres[f + 1] = auxnombre;
                    }
                }
            }
        }
        public void Imprimir()
        {
            Console.WriteLine("Nombres de alumnos y notas de mayor a menor");
            for (int f = 0; f < notas.Length; f++)
            {
                Console.WriteLine(nombres[f] + " - " + notas[f]);
            }
            Console.ReadLine();
        }
        static void Main(string[] args)
        {
            PruebaVector16 pv = new PruebaVector16();
            pv.Cargar();
            pv.Ordenar();
            pv.Imprimir();
        }
    }
}