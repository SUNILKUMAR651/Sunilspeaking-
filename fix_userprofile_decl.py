import os, re

def insert_after_brace(content):
    # Find the first '{' after 'LexiViewModel'
    idx_vm = content.find('LexiViewModel')
    if idx_vm == -1: return content
    
    idx_brace = content.find('{', idx_vm)
    if idx_brace == -1: return content
    
    pos = idx_brace + 1
    return content[:pos] + "\n    val userProfile by viewModel.userProfile.collectAsState()" + content[pos:]

dir_paths = ["app/src/main/java/com/example/ui/screens/", "app/src/main/java/com/example/ui/speaking/"]
for dp in dir_paths:
    for f in os.listdir(dp):
        if not f.endswith('.kt'): continue
        path = os.path.join(dp, f)
        with open(path, 'r') as file:
            content = file.read()
            
        if "userProfile.useFemaleVoice" in content and "val userProfile by viewModel.userProfile" not in content:
            content = insert_after_brace(content)
            
            # also make sure it has the right imports
            if "collectAsState" not in content:
                content = content.replace("import androidx.compose.runtime.*", "import androidx.compose.runtime.*\nimport androidx.compose.runtime.collectAsState")
                
            with open(path, 'w') as file:
                file.write(content)
                print(f"Fixed {f}")
