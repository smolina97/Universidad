import { Component, OnInit } from '@angular/core';
import {Empleado} from "../interfaces/empleado";
import {EmpleadosService} from "../services/empleados.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-formulario',
  templateUrl: './formulario.component.html',
  styleUrls: ['./formulario.component.css']
})
export class FormularioComponent implements OnInit {
  empleados:Empleado ={
    nombres: null,
    apellidos: null,
    edad: null,
    direccion: null,
    cargo: null,
    telefono: null
  };
  id: any;
  editar: boolean = false;
  empleado: Empleado[];
  constructor(private empleadosService: EmpleadosService, private activatedRoute: ActivatedRoute) {
    this.id = this.activatedRoute.snapshot.params['id'];
    if(this.id){
      this.editar = true;
      this.empleadosService.get().subscribe((data:Empleado[])=>{
        this.empleado = data;
        this.empleados = this.empleado.find((m)=>{ return m.id == this.id});
      }, (error)=>{
        alert('Ocurrio un error');
        console.log(error);

      });
    }else {
      this.editar = false;
    }
  }

  ngOnInit(): void {
  }

  guardarEmpleado(): void{
    if(this.editar){
      this.empleadosService.put(this.empleados).subscribe((data)=>{
        alert('Datos Actualizados');
        console.log(data);
      }, (error)=>{
        alert('Ocurrio un error');
        console.log(error);
      });
    }else {
      this.empleadosService.post(this.empleados).subscribe((data)=>{
        alert('Empleado Contratado');
        console.log(data);
      }, (error)=>{
        alert('Ocurrio un error');
        console.log(error);
      });
    }

  }

}
