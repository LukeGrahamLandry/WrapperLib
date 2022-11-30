import os, json

script_folder = os.getcwd()
data = ""

def toReport(state):
    global data
    for module_name, module_data in state.items():
        data += "- {}\n".format(module_name)
        total = {
            "lines": 0,
            "bytes": 0,
            "files": 0
        }
        for loader_name, loader_data in module_data.items():
            data += "   - {}: {} lines, {} kb, {} files\n".format(loader_name, loader_data["lines"], round(loader_data["bytes"] / 1024, 1), loader_data["files"])
            total["lines"] += loader_data["lines"]
            total["bytes"] += loader_data["bytes"]
            total["files"] += loader_data["files"]
        data += "   - **TOTAL: {} lines, {} kb, {} files**\n".format(total["lines"], round(total["bytes"] / 1024, 1), total["files"])
        data += "\n"

for root, dirs, files in os.walk(script_folder + "/input"):
    for input_file in files:
        with open(script_folder + "/generated/results/" + input_file, "r") as f:
            results = json.loads("\n".join(f.readlines()))

        data += "## {}\n\n".format(input_file.split(".")[0])
        data += "### Before WrapperLib\n\n"
        toReport(results["before"])
        # data += "### After WrapperLib\n\n"
        # toReport(results["after"])
        # data += "### Change\n\n"

        with open(script_folder + "/generated/output/" + input_file.split(".")[0] + ".md", "w") as f:
            f.write(data)
    
    break