package com.wiltech.fileupload.files;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {
}
