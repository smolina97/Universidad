<?php

namespace App\Http\Controllers;

use App\Empleado;
use Illuminate\Http\Request;
use App;

class NotaController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $notas = App\Empleado::paginate(5);
        return view('inicio' , compact('notas'));
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $empleadoAgregar = new Empleado;
        $request->validate([
            'nombre' => 'required',
            'direccion' => 'required',
            'telefono' => 'required',
        ]);
        $empleadoAgregar->nombre = $request->nombre;
        $empleadoAgregar->direccion = $request->direccion;
        $empleadoAgregar->telefono = $request->telefono;
        $empleadoAgregar->save();
        return back()->with('agregar' , 'El empleado se ha agregado correctamente');
    }

    /**
     * Display the specified resource.
     *
     * @param  \App\Nota  $nota
     * @return \Illuminate\Http\Response
     */
    public function show(Nota $nota)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  \App\Nota  $nota
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        $empleadoActualizar = App\Empleado::findOrFail($id);
        return view('editar' , compact('empleadoActualizar'));
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \App\Nota  $nota
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        $empleadoUpdate = App\Empleado::findOrFail($id);
        $empleadoUpdate->nombre = $request->nombre;
        $empleadoUpdate->direccion = $request->direccion;
        $empleadoUpdate->telefono = $request->telefono;
        $empleadoUpdate->save();
        return back()->with('update' , 'El empleado se ha sido actualizado correctamente');
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  \App\Nota  $nota
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $empleadoEliminar = App\Empleado::findOrFail($id);
        $empleadoEliminar->delete();
        return back()->with('eliminar' , 'El empleado ha sido eliminado correctamente');
    }
}
