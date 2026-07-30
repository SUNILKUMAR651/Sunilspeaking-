import re

path = "app/src/main/AndroidManifest.xml"
with open(path, "r") as f:
    content = f.read()

old_perms = """    <uses-permission android:name="android.permission.RECORD_AUDIO" />"""

new_perms = """    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />"""

old_app = """        </activity>
    </application>"""

new_app = """        </activity>
        <receiver android:name=".utils.NotificationReceiver" android:exported="false" />
    </application>"""

content = content.replace(old_perms, new_perms)
content = content.replace(old_app, new_app)

with open(path, "w") as f:
    f.write(content)
