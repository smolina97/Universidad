
<?php
exec("gpio mode 0 out"); // pin 0 in wiring pi is gpio 17
exec("gpio mode 2 out"); // pin 2 in wiring pi is gpio 27
exec("gpio mode 3 out"); // pin 3 in wiring pi is gpio 22
exec("gpio mode 12 out"); // pin 12 in wiring pi is gpio 10
exec("gpio mode 13 out"); // pin 13 in wiring pi is gpio 9
exec("gpio mode 14 out"); // pin 14 in wiring pi is gpio 11
exec("gpio mode 23 out"); // pin 23 in wiring pi is gpio 13

if (isset($_GET['rojoCarros'])) {
    if ($_GET['rojoCarros'] == 1) {
        exec("gpio write 0 1");
    } else {
        exec("gpio write 0 0");
    }
}
if (isset($_GET['amarilloCarros'])) {
    if ($_GET['amarillo'] == 1) {
        exec("gpio write 2 1");
    } else {
        exec("gpio write 2 0");
    }
}
if (isset($_GET['verdeCarros'])) {
    if ($_GET['verdeCarros'] == 1) {
        exec("gpio write 3 1");
    } else {
        exec("gpio write 3 0");
    }
}
if (isset($_GET['rojoPeaton'])) {
    if ($_GET['rojoPeaton'] == 1) {
        exec("gpio write 12 1");
    } else {
        exec("gpio write 12 0");
    }
}
if (isset($_GET['verdePeaton'])) {
    if ($_GET['verdePeaton'] == 1) {
        exec("gpio write 13 1");
    } else {
        exec("gpio write 13 0");
    }
}
?>