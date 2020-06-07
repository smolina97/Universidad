<?php

namespace App\Http\Controllers;

use App\Empleados;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class
EmpleadosController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return void
     */
    public function index()
    {
        $empleados = Empleados::get();
        echo json_encode($empleados);
    }

    public function saveEmpleados($empleados, $request){

        $empleados->nombres = $request->input('nombres');
        $empleados->apellidos = $request->input('apellidos');
        $empleados->edad = $request->input('edad');
        $empleados->cargo = $request->input('cargo');
        $empleados->direccion = $request->input('direccion');
        $empleados->telefono = $request->input('telefono');
        $empleados->save();
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param Request $request
     * @return void
     */
    public function store(Request $request)
    {
        $empleados = new Empleados();
        $this->saveEmpleados($empleados, $request);
    }

    /**
     * Update the specified resource in storage.
     *
     * @param Request $request
     * @param Empleados $empleados_id
     * @return void
     */
    public function update(Request $request, $empleados_id)
    {
        $empleados = Empleados::find($empleados_id);
        $this->saveEmpleados($empleados, $request);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param $empleados_id
     * @return void
     */
    public function destroy($empleados_id)
    {
        $empleados = Empleados::find($empleados_id);
        $empleados->delete();
    }
}
