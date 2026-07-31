import requests

api_key = "c6fe65b595354573b51a572583381ada"
url = "https://api.fish.audio/v1/tts"
headers = {
    "Authorization": f"Bearer {api_key}",
    "Content-Type": "application/json"
}
data = {
    "text": "Hello, this is a test.",
    "format": "mp3"
}

try:
    response = requests.post(url, headers=headers, json=data)
    print(response.status_code)
    if response.status_code != 200:
        print(response.text)
    else:
        print("Success, length:", len(response.content))
except Exception as e:
    print(e)
