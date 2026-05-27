import json
import urllib.request
import urllib.error
from pathlib import Path

BASE_URL = "http://localhost:8080"

# Script to seed the database with test data. Run this after starting the server.

def post(path, body):
    data = json.dumps(body).encode()
    req = urllib.request.Request(
        f"{BASE_URL}{path}",
        data=data,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(req) as resp:
        return json.loads(resp.read())


data = json.loads(Path(__file__).with_name("data.json").read_text())

for i, (form_def, entries) in enumerate(zip(data["forms"], data["entries"]), start=1):
    form = post("/forms", form_def)
    print(f"Form {i}: {form['title']!r}  →  id={form['id']}")

    for j, entry_body in enumerate(entries, start=1):
        entry = post(f"/forms/{form['id']}/entries", entry_body)
        print(f"  Entry {j}: id={entry['id']}")
