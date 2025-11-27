package com.studydocs.repository;

import com.studydocs.model.entity.Folder;
import com.studydocs.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    
    List<Folder> findByUserAndParentFolderIsNull(User user);
    
    List<Folder> findByUserAndParentFolder(User user, Folder parentFolder);
    
    List<Folder> findByUser(User user);
    
    List<Folder> findByParentFolder(Folder parentFolder);
}

