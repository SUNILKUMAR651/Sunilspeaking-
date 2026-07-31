import requests

api_key = "cb4905e8b23a48b5b1ee3946315e2403"
url = "https://api.fish.audio/v1/tts"
headers = {
    "Authorization": f"Bearer {api_key}",
    "Content-Type": "application/json"
}
data = {
    "text": "Hello, this is a test.",
    "format": "mp3",
    "reference_id": "c6fe65b595354573b51a572583381ada"
}

response = requests.post(url, headers=headers, json=data)
print(response.status_code)
if response.status_code != 200:
    print(response.text)
else:
    print("Success, length:", len(response.content))
