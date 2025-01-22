<?php
session_start();

function client_ip(){
    return !empty($_SERVER['HTTP_X_FORWARDED_FOR']) ? $_SERVER['HTTP_X_FORWARDED_FOR'] : $_SERVER['REMOTE_ADDR'];
}

$ip = client_ip();
if(!filter_var($ip, FILTER_VALIDATE_IP) || !in_array($ip, array('localhost', '127.0.0.1'))){
    die(htmlspecialchars("Not allowed!\n"));
}

if(!isset($_SESSION['auth'])){
    header("Location: error.php");
    
}

// interact with API endpoints
echo call_user_func($_GET['cmd'], $_GET['arg']);