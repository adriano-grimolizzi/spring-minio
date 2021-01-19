package com.example.uploadingfiles;

import com.example.uploadingfiles.model.Metadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetadataRepository extends MongoRepository<Metadata, String> {
//
//    public Metadata findByFileName(String fileName);
//
//    public List<Metadata> findByType(String type);

}