import os, json

script_folder = os.getcwd()
data = {}

def fileObject(manifest, path):
    next_folder = manifest
    parts = path.split("/")
    for part in parts:
        if part not in next_folder:
            next_folder[part] = {"files": {}}
        
        next_folder = next_folder[part]
    
    return next_folder
        

def parseProjectInput(input_data):
    data = {}
    with open(script_folder + "/generated/manifest/" + input_data["manifest"], "r") as f:
        manifest_data = json.loads("\n".join(f.readlines()))

    for loader, modules in input_data["projects"].items():
        for module_name, module_files in modules.items():
            info = {
                "lines": 0,
                "bytes": 0,
                "files": 0,
                "paths": []
            }
            if module_name not in data:
                data[module_name] = {
                    loader: info
                }
            else:
                data[module_name][loader] = info


            def handleFile(path):
                info["paths"].append(path)
                dir = "/".join(path.split("/")[:-1])
                filename = path.split("/")[-1]
                file_data = fileObject(manifest_data, dir)["files"][filename]
                info["files"] += 1
                info["bytes"] += file_data["bytes"]
                info["lines"] += file_data["lines"]

            def handleDir(path):
                file_data = fileObject(manifest_data, path)
                for key, value in file_data.items():
                    if key == "files":
                        for filename, file_info in value.items():
                            info["paths"].append(path + "/" + filename)
                            info["files"] += 1
                            info["bytes"] += file_info["bytes"]
                            info["lines"] += file_info["lines"]
                    else:
                        handleDir(path + "/" + key)
                        

            def walkFile(path):
                if "." in path:
                    handleFile(path)
                else:
                    handleDir(path)

            for path in module_files:
                full_path = "/" + loader + "/" + path
                walkFile(full_path)

    return data
                

for root, dirs, files in os.walk(script_folder + "/input"):
    for input_file in files:
        with open(script_folder + "/input/" + input_file, "r") as f:
            input_data = json.loads("\n".join(f.readlines()))

        data["before"] = parseProjectInput(input_data["before"])
        # parseProjectInput(input_data["after"])

        with open(script_folder + "/generated/results/" + input_file, "w") as f:
            f.write(json.dumps(data, indent=4))
    
    break