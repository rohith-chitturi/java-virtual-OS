package com.rohith.javavirtualos.filesystem;

import com.rohith.javavirtualos.exceptions.FileSystemException;
import com.rohith.javavirtualos.filesystem.model.DirectoryNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemValidatorTest {

    private FileSystemValidator validator;
    private DirectoryNode root;
    private DirectoryNode userDir;

    @BeforeEach
    public void setUp() {
        validator = new FileSystemValidator();
        root = new DirectoryNode("", "root", null);
        userDir = new DirectoryNode("user", "user", root);
        root.addChild(userDir);
    }

    @Test
    public void testValidateFileNameValid() {
        assertDoesNotThrow(() -> validator.validateFileName("valid-name.txt"));
    }

    @Test
    public void testValidateFileNameEmpty() {
        assertThrows(FileSystemException.class, () -> validator.validateFileName(""));
        assertThrows(FileSystemException.class, () -> validator.validateFileName("   "));
    }

    @Test
    public void testValidateFileNameReserved() {
        assertThrows(FileSystemException.class, () -> validator.validateFileName("."));
        assertThrows(FileSystemException.class, () -> validator.validateFileName(".."));
    }

    @Test
    public void testValidateFileNameInvalidChars() {
        assertThrows(FileSystemException.class, () -> validator.validateFileName("file*name.txt"));
        assertThrows(FileSystemException.class, () -> validator.validateFileName("file/name.txt"));
    }

    @Test
    public void testValidateCreationDuplicate() {
        DirectoryNode subDir = new DirectoryNode("docs", "user", userDir);
        userDir.addChild(subDir);
        
        assertThrows(FileSystemException.class, () -> validator.validateCreation(userDir, "docs"));
    }

    @Test
    public void testValidateDeletionRoot() {
        assertThrows(FileSystemException.class, () -> validator.validateDeletion(root, userDir));
    }

    @Test
    public void testValidateDeletionCurrentActive() {
        assertThrows(FileSystemException.class, () -> validator.validateDeletion(userDir, userDir));
    }
}
