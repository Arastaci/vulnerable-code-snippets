import sqlite3
import pytest
from examples.insecure.python.sql_injection import get_user_data as insecure_get_user_data
from examples.secure.python.sql_injection_fixed import get_user_data as secure_get_user_data

@pytest.fixture
def setup_test_db():
    conn = sqlite3.connect(":memory:")  # Geçici veritabanı
    cursor = conn.cursor()
    cursor.execute("CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT, password TEXT)")
    cursor.executemany(
        "INSERT INTO users (username, password) VALUES (?, ?)",
        [("admin", "securepassword"), ("user", "userpassword")],
    )
    conn.commit()
    yield conn
    conn.close()

def test_insecure_query_allows_injection(setup_test_db):
    conn = setup_test_db
    result = insecure_get_user_data("admin' OR '1'='1")  # SQL Injection payload
    assert len(result) > 1, "Insecure function should be vulnerable to SQL Injection"

def test_secure_query_prevents_injection(setup_test_db):
    conn = setup_test_db
    result = secure_get_user_data("admin' OR '1'='1")
    assert len(result) == 0, "Secure function should NOT allow SQL Injection"
