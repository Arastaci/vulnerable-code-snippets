import sqlite3

def get_user_data(username):
    conn = sqlite3.connect("example.db")
    cursor = conn.cursor()
    query = "SELECT * FROM users WHERE username = ?"  # Parameterized queries kullanarak bu sorunu ortadan kaldırabiliriz.
    cursor.execute(query, (username,))
    return cursor.fetchall()
