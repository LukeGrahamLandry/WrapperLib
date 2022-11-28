package ca.lukegrahamlandry.lib.data.type;

import ca.lukegrahamlandry.lib.data.DataWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// TODO: only load nessisary if this.useMultipleFiles
// TODO: a MapDataWrapper with useMultipleFiles=false is really a GlobalDataWrapper it just so happens that im writting a map in there instead
// so maybe if useMultipleFiles=false we have a GlobalDataWrapper instance wrapped with one field for the map
// maybe also fields for the key/value class names idk quite how to make that work
public abstract class MapDataWrapper<K, I, V> extends DataWrapper<V> {
    ///// INIT

    /**
     * Save in dir/name/id.etx {data} instead of dir/name.etx {id: data}
     */
    public <W extends MapDataWrapper<K, I, V>> W splitFiles(){
        this.useMultipleFiles = true;
        return (W) this;
    }

    ///// API

    public V get(K key){
        return this.getById(this.keyToId(key));
    }


    ///// IMPL

    private boolean useMultipleFiles = false;
    protected Map<I, V> data = new HashMap<>();
    private final Class<I> idClazz; // for json deserialization
    protected MapDataWrapper(Class<I> idClazz, Class<V> clazz) {
        super(clazz);
        this.idClazz = idClazz;
    }

    public abstract I keyToId(K key);

    public abstract I stringToId(String id);

    protected Path getFilePath(){
        return this.getFilePath(null);
    }

    public V getById(I id){
        if (!this.isLoaded) {
            this.logger.error("cannot call DataWrapper get (a) before server startup (b) on client if unsynced");
            return null;
        }
        if (!data.containsKey(id)) data.put(id, this.createDefaultInstance());
        return data.get(id);
    }

    // Note: im concerned that gson writing maps uses toString instead of type adapters but it's fine for this
    @Override
    public void save() {
        Gson pretty = this.getGson().newBuilder().setPrettyPrinting().create();
        if (this.useMultipleFiles){
            this.data.forEach((key, value) -> {
                Path path = this.getFilePath(key);
                path.toFile().getParentFile().mkdirs();
                String json = pretty.toJson(value);
                try {
                    Files.write(path, json.getBytes());
                } catch (IOException e) {
                    this.logger.error("failed to write data to " + forDisplay(path));
                }
            });
        } else {
            Path path = this.getFilePath(null);
            path.toFile().getParentFile().mkdirs();
            String json = pretty.toJson(this.data);
            try {
                Files.write(path, json.getBytes());
            } catch (IOException e) {
                this.logger.error("failed to write data to " + forDisplay(path));
            }
        }
    }

    @Override
    public void load() {
        if (server == null) {
            String msg = "cannot call DataWrapper#load (a) before server startup (b) on client";
            this.logger.error(msg);
            throw new RuntimeException(msg);
        }

        Path path = this.getFilePath();
        if (!path.toFile().exists()) {
            // first world load. no data will be found
            this.isLoaded = true;
            return;
        }

        if (this.useMultipleFiles){
            if (path.toFile().isFile()) {
                String msg = "(useMultipleFiles=true) failed to load data from " + forDisplay(path) + " because it is a file not a directory";
                this.logger.error(msg);
                throw new RuntimeException(msg);
            }
            for (File file : path.toFile().listFiles()){
                try {
                    Reader reader = Files.newBufferedReader(file.toPath());
                    String filename = file.getName().substring(0, file.getName().lastIndexOf("."));
                    I id = this.stringToId(filename);
                    V value = this.getGson().fromJson(reader, this.clazz);
                    reader.close();
                    this.data.put(id, value);
                } catch (IOException | JsonSyntaxException e) {
                    this.logger.error("failed to load data from " + forDisplay(path));
                    e.printStackTrace();
                    throw new RuntimeException("failed to load data from " + forDisplay(path));
                }
            }
        } else {
            try {
                Reader reader = Files.newBufferedReader(path);
                JsonObject fileInfo = this.getGson().fromJson(reader, JsonObject.class);
                reader.close();
                for (Map.Entry<String, JsonElement> entry : fileInfo.entrySet()){
                    try {
                        I id = this.stringToId(entry.getKey());
                        V value = this.getGson().fromJson(entry.getValue(), this.clazz);
                        this.data.put(id, value);
                    } catch (JsonSyntaxException e){
                        this.logger.error("failed to parse json data of " + entry.getValue() + " in " + forDisplay(path));
                        e.printStackTrace();
                    }
                }
            } catch (IOException | JsonSyntaxException e) {
                this.logger.error("failed to load data from " + forDisplay(path));
                e.printStackTrace();
                throw new RuntimeException("failed to load data from " + forDisplay(path));
            }
        }

        this.isLoaded = true;
    }

    @Override
    public void sync() {

    }

    /**
     * useMultipleFiles=true, id==null  -> the folder where your files will be
     * useMultipleFiles=true, id!=null  -> the file where data for that id is stored
     * useMultipleFiles=false, id==null -> the file where all data is stored
     */
    protected Path getFilePath(I id){
        Path path = server.getWorldPath(LevelResource.ROOT).resolve("data");
        if (this.subDirectory != null) path = path.resolve(this.subDirectory);

        if (this.useMultipleFiles){
            path = path.resolve(this.name);
            if (id != null) path = path.resolve(id.toString() + "." + this.fileExtension);
        } else {
            path = path.resolve(this.name + "." + this.fileExtension);
        }

        return path;
    }
}
