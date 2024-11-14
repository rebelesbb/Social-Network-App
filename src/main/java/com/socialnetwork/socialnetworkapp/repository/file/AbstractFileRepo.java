package com.socialnetwork.socialnetworkapp.repository.file;

import com.socialnetwork.socialnetworkapp.domain.Entity;
import com.socialnetwork.socialnetworkapp.domain.validators.Validator;
import com.socialnetwork.socialnetworkapp.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

public abstract class AbstractFileRepo<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final String filename;

    public AbstractFileRepo(Validator<E> validator, String filename) {
        super(validator);
        this.filename = filename;
        loadData();
    }

    /**
     * Creates an entity from the provided string
     * @param line the provided string
     * @return the newly created entity
     */
    public abstract E createEntity(String line);
    /**
     * Coverts the data of an entity to a string , having the required format for saving
     * @param entity the entity to be converted
     * @return the string with the data of the entity
     */
    public abstract String saveEntity(E entity);

    /**
     * Loads the data into memory from the file
     */
    private void loadData(){
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;
            while((line = br.readLine()) != null){
                E e = createEntity(line);
                super.save(e);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads the data into the file
     */
    private void writeToFile(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
            for(E entity : entities.values()){
                String e = saveEntity(entity);
                bw.write(e);
                bw.newLine();
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Optional<E> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if(e.isEmpty())
            writeToFile();
        return e;
    }

    @Override
    public Optional<E> delete(ID id){
        Optional<E> e = super.delete(id);
        if(e.isPresent())
            writeToFile();
        return e;
    }
}
