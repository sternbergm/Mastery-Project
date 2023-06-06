package learn.reservations.dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class PersonRepository<Obj> implements Repository<Obj> {
    protected String fileName;
    protected ObjectMapper<Obj> mapper;
    protected String header;

    @Override
    public List<Obj> readAll() throws DALException {
        List<Obj> result = new ArrayList<>();
        Path filePath = Path.of(fileName);
        try {
            String content = Files.readString(filePath);
            List<String> serializedObjects = Arrays.stream(content.split("\n")).collect(Collectors.toList());
            serializedObjects.remove(0);
            result = serializedObjects.stream().map(mapper::deserialize).collect(Collectors.toList());

        } catch (IOException e) {
            throw new DALException("Error reading from file");
        }
        return result;
    }


    private void writeAll(List<Obj> objects) throws DALException{
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new DALException("Unable to create file");
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            PrintWriter finalWriter = writer;
            finalWriter.print(header+"\n");
            objects.forEach(obj -> finalWriter.print(mapper.serialize(obj)+"\n"));
        } catch (FileNotFoundException e) {
            throw new DALException("Error writing to file");
        } finally {
            if(writer != null) writer.close();
        }
    }

    abstract void setId(Obj object, List<Obj> objects);

    @Override
    public Obj create(Obj object) throws DALException{
        List<Obj> objects = readAll();
        setId(object, objects);
        objects.add(object);
        writeAll(objects);
        return object;
    }

    @Override
    public void update(Obj object) throws DALException{
        List<Obj> objects = readAll();
        IntStream.range(0, objects.size()).filter(i -> objects.get(i).equals(object)).findFirst().ifPresent(i -> objects.set(i, object));
        writeAll(objects);
    }

    @Override
    public void delete(Obj object) throws DALException {
        List<Obj> objects = readAll();
        IntStream.range(0, objects.size()).filter(i -> objects.get(i).equals(object)).findFirst().ifPresent(objects::remove);
        writeAll(objects);
    }
}
