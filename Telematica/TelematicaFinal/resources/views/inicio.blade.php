@extends('plantilla')

@section('content')
    <div class="row">
        <div class="col-md-7">
            <table class="table">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Direccion</th>
                    <th>Telefono</th>
                    <th>Acciones</th>
                </tr>
               
                @foreach ($notas as $item)
                    <tr>
                        <td>{{$item->id}}</td>
                        <td>{{$item->nombre}}</td>
                        <td>{{$item->direccion}}</td>
                        <td>{{$item->telefono}}</td>
                        <td>
                            <a href="{{route('editar' , $item->id)}}" class="btn btn-warning">Editar</a>
                            <form action="{{route('eliminar' , $item->id)}}" method="POST" class="d-inline">
                                @method('DELETE')
                                @csrf
                                <button type="submit" class="btn btn-danger">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                @endforeach
            </table>
            @if (session('eliminar'))
                <div class="alert alert-success mt-3">
                    {{session('eliminar')}}
                </div>
            @endif
            {{$notas->links()}}
        </div>
        {{-- Fila formulario --}}
        <div class="col-md-5">
            <h3 class="text-center mb-4">Agregar empleado</h3>

            <form action="{{route('store')}}" method="POST">
                @csrf

                <div class="form-group">
                <input type="text" name="nombre" id="nombre" class="form-control" value="{{old('nombre')}}" placeholder="Nombre del empleado" required>
                </div>

                @error('nombre')
                    <div class="alert alert-danger">
                        El nombre es requerido
                    </div>
                @enderror

                <div class="form-group">
                <input type="text" name="direccion" id="direccion" class="form-control" value="{{old('direccion')}}" placeholder="Direccion del empleado " required>
                </div>
                @error('direccion')
                    <div class="alert alert-danger">
                        La descripción es requerida
                    </div>
                @enderror

                <div class="form-group">
                    <input type="text" name="telefono" id="telefono" class="form-control" value="{{old('telefono')}}" placeholder="Telefono del empleado" required>
                    </div>
                    @error('telefono')
                        <div class="alert alert-danger">
                            La descripción es requerida
                        </div>
                    @enderror

                <button type="submit" class="btn btn-success btn-block">Registrar empleado</button>
            </form>

            @if (session('agregar'))
                <div class="alert alert-success mt-3">
                    {{session('agregar')}}
                </div>
            @endif
        </div>
        {{-- Fin fila formulario --}}
    </div>
@endsection