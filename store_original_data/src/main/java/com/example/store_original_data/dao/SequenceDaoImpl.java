package com.example.store_original_data.dao;

import com.example.store_original_data.entity.SequenceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceDaoImpl implements SequenceDao {
    @Autowired
    private MongoOperations mongoOperations;


    @Override
    public int getNextSequenceId(String key) {
        Query query = new Query(Criteria.where("_id").is(key));

        Update update = new Update();
        update.inc("seq", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        SequenceId seqId = mongoOperations.findAndModify(query, update, options, SequenceId.class);

        if (seqId == null) {
            throw new SequenceException("Unable to get sequence id for key : " + key);
        }

        return seqId.getSeq();
    }
}
