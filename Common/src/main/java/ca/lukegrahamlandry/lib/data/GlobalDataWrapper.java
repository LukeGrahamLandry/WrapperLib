package ca.lukegrahamlandry.lib.data;

import ca.lukegrahamlandry.lib.data.sync.GlobalDataSyncMessage;
import ca.lukegrahamlandry.lib.network.NetworkWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public class GlobalDataWrapper<T> extends DataWrapper<T> implements Supplier<T> {
    T value;
    public GlobalDataWrapper(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T get() {
        if (!this.isLoaded) {
            this.logger.error("cannot call DataWrapper#get (a) before server startup (b) on client if unsynced");
            return null;
        }

        return this.value;
    }

    @Override
    public void load() {
        if (server == null) {
            String msg = "cannot call DataWrapper#load (a) before server startup (b) on client";
            this.logger.error(msg);
            throw new RuntimeException(msg);
        }

        if (!this.getFilePath().toFile().exists()) {
            // first world load. no data will be found
            this.isLoaded = true;
            return;
        }

        try {
            Reader reader = Files.newBufferedReader(this.getFilePath());
            this.value = this.getGson().fromJson(reader, this.clazz);
            reader.close();
        } catch (IOException | JsonSyntaxException e) {
            String msg = "failed to load data from " + forDisplay(this.getFilePath());
            this.logger.error(msg);
            e.printStackTrace();
            throw new RuntimeException(msg);
        }

        this.isLoaded = true;
    }

    @Override
    public void save() {
        Path path = this.getFilePath();
        path.toFile().getParentFile().mkdirs();
        Gson pretty = this.getGson().newBuilder().setPrettyPrinting().create();
        String json = pretty.toJson(this.value);
        try {
            Files.write(path, json.getBytes());
        } catch (IOException e) {
            this.logger.error("failed to write data to " + forDisplay(path));
        }
    }

    @Override
    public void sync() {
        if (!this.shouldSync) {
            this.logger.error("called DataWrapper#sync but shouldSync=false");
            return;
        }
        if (!canFindClass("ca.lukegrahamlandry.lib.network.NetworkWrapper")){
            this.logger.error("called ConfigWrapper#sync but WrapperLib-Packets module is missing");
            return;
        }

        NetworkWrapper.sendToAllClients(new GlobalDataSyncMessage(this));
    }

    protected Path getFilePath(){
        Path path = server.getWorldPath(LevelResource.ROOT).resolve("data");
        if (this.subDirectory != null) path = path.resolve(this.subDirectory);
        path = path.resolve(this.name + "." + this.fileExtension);
        return path;
    }

    // NEVER CALL THIS
    // its just for the sync stuff
    // TODO: apionly annotation or whatever
    public void set(Object v){
        this.value = (T) v;
    }
}
