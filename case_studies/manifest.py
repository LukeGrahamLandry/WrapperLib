import os, json

script_folder = os.getcwd()
data = {}
project_folder = "/Users/luke/Documents/mods/Torcherino" # input("full path to project folder:  ")  # 
output_name = "torcherino-1.19-17.0.4.json" # input("name of output data file (no extension):  ") + ".json"
ignored_folders = ["/.gradle", "/gradle", "/.git", "/.idea"]

def fileObject(path):
    next_folder = data
    parts = path.split("/")
    for part in parts:
        if part not in next_folder:
            next_folder[part] = {"files": {}}
        
        next_folder = next_folder[part]
    
    return next_folder
        

for root, dirs, files in os.walk(project_folder):
    path = root.replace(project_folder, "")

    shouldIgnore = False
    for ignored in ignored_folders:
        if ignored in path:
            shouldIgnore = True
    if shouldIgnore:
        continue

    for name in dirs:
        fileObject(path)
        pass

    for name in files:
        extension = name.split(".")[-1]

        if extension in ["java", "gradle"]:
            file_path = root + "/" + name
            print(file_path)
            file_data = {}
            fileObject(path)["files"][name] = file_data
            file_data["bytes"] = os.stat(file_path).st_size
            
            with open(file_path, "r") as f:
                file_data["lines"] = len(f.readlines())


with open(script_folder + "/generated/manifest/" + output_name, "w") as f:
    f.write(json.dumps(data, indent=4))