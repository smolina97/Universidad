<?php

    include("../db/conexion.php");
    $nombres = $_POST['nombres'];
    $apellidos = $_POST['apellidos'];
    $conn = mysqli_connect("$host:$port",$user,$password,$db);

    if (!$conn) {
        die("Connection failed: ".mysqli_connect_error());
    }

    $sql = "INSERT INTO `usuarios` (`nombres`, `apellidos`)
            VALUES ('$nombres', '$apellidos')";

    if (mysqli_query($conn, $sql)) {
        $message = "Usuario Creado";
        echo "<script type='text/javascript'>alert('$message');
        window.location.href='index.html'
        </script>";
    } else {
        echo "Error: ".$sql."<br>".mysqli_error($conn);
    }

    mysqli_close($conn);





