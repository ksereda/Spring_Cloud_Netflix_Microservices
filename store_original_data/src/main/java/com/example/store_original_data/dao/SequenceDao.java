package com.example.store_original_data.dao;

public interface SequenceDao {

    int getNextSequenceId(String key) throws SequenceException;

}
