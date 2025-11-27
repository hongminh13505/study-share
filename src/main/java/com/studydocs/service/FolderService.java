package com.studydocs.service;

import com.studydocs.model.entity.Folder;
import com.studydocs.model.entity.User;
import com.studydocs.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FolderService {
    
    private final FolderRepository folderRepository;
    
    public Folder createFolder(String folderName, User user, Integer parentFolderId) {
        Folder folder = new Folder();
        folder.setFolderName(folderName);
        folder.setUser(user);
        
        if (parentFolderId != null) {
            folderRepository.findById(parentFolderId).ifPresent(folder::setParentFolder);
        }
        
        return folderRepository.save(folder);
    }
    
    public List<Folder> getRootFolders(User user) {
        return folderRepository.findByUserAndParentFolderIsNull(user);
    }
    
    public List<Folder> getSubFolders(User user, Integer parentFolderId) {
        if (parentFolderId == null) {
            return getRootFolders(user);
        }
        
        Optional<Folder> parentFolder = folderRepository.findById(parentFolderId);
        return parentFolder.map(folder -> folderRepository.findByUserAndParentFolder(user, folder))
                .orElse(List.of());
    }
    
    public Optional<Folder> getFolderById(Integer folderId) {
        return folderRepository.findById(folderId);
    }
    
    public void deleteFolder(Integer folderId) {
        folderRepository.deleteById(folderId);
    }
    
    public Folder renameFolder(Integer folderId, String newName) {
        Optional<Folder> folder = folderRepository.findById(folderId);
        if (folder.isPresent()) {
            Folder f = folder.get();
            f.setFolderName(newName);
            return folderRepository.save(f);
        }
        throw new RuntimeException("Folder không tồn tại!");
    }
    
    public List<Folder> getAllUserFolders(User user) {
        return folderRepository.findByUser(user);
    }
}

