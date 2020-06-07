@extends('plantilla')

@section('content')
    <h3 class="text-center mb-3 pt-3">Editar el empleado {{$empleadoActualizar->id}}</h3>

    <form action="{{route('update' , $empleadoActualizar->id)}}" method="POST">
        @method('PUT')
        @csrf

        <div class="form-group">
            <input type="text" name="nombre" id="nombre" value="{{$empleadoActualizar->nombre}}" class="form-control">
        </div>

        <div class="form-group">
            <input type="text" name="direccion" id="direccion" value="{{$empleadoActualizar->direccion}}" class="form-control">
        </div>

        <div class="form-group">
            <input type="text" name="telefono" id="telefono" value="{{$empleadoActualizar->telefono}}" class="form-control">
        </div>

        <button type="submit" class="btn btn-warning btn-block">Editar empleado</button>
    </form>
    @if (session('update'))
    
        <div class="alert alert-success mt-3">
            {{session('update')}}
            echo '<meta http-equiv="Refresh" content="0;URL=../">';
        </div>
    @endif

@endsection