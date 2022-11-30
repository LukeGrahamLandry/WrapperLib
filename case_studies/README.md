# WrapperLib Case Studies

## manifest.py

- `manifest.py` walks through a source code directory and saves the file structure and the sizes of each file in `generated/manifest`

## results.py

- each file in input defines one project, before and after porting to WrapperLib, and splits its files into wrapper modules by loader
- `results.py` walks through these files, reads data from `generated/manifest`, and saves the sizes of each loader module to `generated/results`

## output.py

- converts the files in `generated/results` to a readable markdown report describing how the size of the project changed by using WrapperLib 
