import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Empleado} from "../interfaces/empleado";

@Injectable({
  providedIn: 'root'
})
export class EmpleadosService {

  API_ENDPOINT = 'http://127.0.0.1:8000/api'
  constructor(private httpClient:HttpClient) { }
  get(){
   return  this.httpClient.get(this.API_ENDPOINT + '/empleados');
  }

  post(empleado: Empleado){
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    return this.httpClient.post(this.API_ENDPOINT+'/empleados',empleado,{headers: headers});
  }

  put(empleado: Empleado){
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    return this.httpClient.put(this.API_ENDPOINT+'/empleados/' + empleado.id ,empleado,{headers: headers});
  }

  delete(id){
    return  this.httpClient.delete(this.API_ENDPOINT + '/empleados/' + id);
  }

}
