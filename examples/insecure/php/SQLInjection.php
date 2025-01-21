<?php
$db = new SQLite3('/srv/users.sqlite');

if(!isset($_POST['mail'])) {
    die("Need mail.\n");
}

$mail = $_POST['mail'];

$filter_chain = array(FILTER_DEFAULT, FILTER_SANITIZE_ADD_SLASHES, FILTER_VALIDATE_EMAIL, FILTER_SANITIZE_STRING);

for($i=0; $i < count($filter_chain); $i++){
    if(filter_var($mail, $filter_chain[$i]) === false){
        die("Invalid Email.\n");
    }
}

// check if email exists
$user = $db->querySingle("SELECT username FROM users WHERE email='$mail' LIMIT 1");
if(!$user){
    die('No user found with given email.');
}

echo sprintf("Hello %s we sent you an email ;).\n", htmlspecialchars($user, ENT_QUOTES));