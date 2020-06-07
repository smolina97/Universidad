import { Component, OnInit } from '@angular/core';
import {EmpleadosService} from "../services/empleados.service";
import {Empleado} from "../interfaces/empleado";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  empleados:Empleado[];
  constructor(private empleadosService: EmpleadosService) {
   this.getEmpleados();
  }
  ngOnInit(): void {
  }

  getEmpleados():void{
    this.empleadosService.get().subscribe((data: Empleado[]) => {
      this.empleados = data;
    },(error) =>{
      console.log(error);
      alert('Ocurrio un Error');
    });
  }

  delete(id){
    if(confirm('Desea eliminar este dato')) {
      this.empleadosService.delete(id).subscribe((data) => {
        alert('El dato a sido eliminado');
        console.log(data);
        this.getEmpleados();

      }, (error) => {
        console.log(error);
        alert('Ocurrio un Error');
      });
    }
  }
}
