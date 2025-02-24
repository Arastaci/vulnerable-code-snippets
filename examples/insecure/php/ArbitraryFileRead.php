<?php
const LEAK_ME = '/key.pem';

function debugCertificate()
{   
    if (!array_key_exists('cert', $_POST)) {
        die('Please provide your certificate!');
    }
    if (strstr($_POST['cert'], 'BEGIN PUBLIC')) {
        $res = openssl_pkey_get_public($_POST['cert']);
    } else {
        $res = openssl_pkey_get_private($_POST['cert']);
    }
    $res = openssl_pkey_get_details($res);
    echo 'Here is your key:<br><pre>' . serialize($res) . '</pre>';
}
