<?php

session_start();

function changePassword($token, $newPassword)
{
    $db = new SQLite3('/srv/users.sqlite', SQLITE3_OPEN_READWRITE);
    $p = $db->prepare('SELECT id FROM users WHERE reset_token = :token'); 
    $p->bindValue(':token', $token, SQLITE3_TEXT); 
    $res = $p->execute()->fetchArray(1);
    if (strlen($token) == 32 && $res) 
    {
        $p = $db->prepare('UPDATE users SET password = :password WHERE id = :id');
        $p->bindValue(':password', $newPassword, SQLITE3_TEXT); 
        $p->bindValue(':id', $res['id'], SQLITE3_INTEGER);
        $p->execute();
        # TODO: notify the user of the new password by email
        die('Password changed!');
    }
    http_response_code(403);
    die('Invalid reset token!');
}

function generatePasswordResetToken($user) 
{
    $db = new SQLite3('/srv/users.sqlite', SQLITE3_OPEN_READWRITE);
    $token = md5(mt_rand(1, 100) . $user . time() . session_id());
    $p = $db->prepare('UPDATE users SET reset_token = :token WHERE name = :user');
    $p->bindValue(':user', $user, SQLITE3_TEXT);
    $p->bindValue(':token', $token, SQLITE3_TEXT);
    $p->execute();
}
?>
