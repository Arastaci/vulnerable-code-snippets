import sqlite3

def get_user_data(username):
    conn = sqlite3.connect("example.db")
    cursor = conn.cursor()
    query = f"SELECT * FROM users WHERE username = '{username}'"  # SQL Injection!
    cursor.execute(query)
    return cursor.fetchall()

get_user_data("admin' OR '1'='1")
