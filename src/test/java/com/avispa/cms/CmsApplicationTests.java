package com.avispa.cms;

import com.avispa.cms.model.filestore.FileStore;
import com.avispa.cms.model.filestore.FileStoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CmsApplicationTests {

	@Autowired
	private FileStoreRepository repository;

	@Test
	void contextLoads() {
		FileStore fileStore = new FileStore();
		fileStore.setObjectName("TestFileStore");
		fileStore.setRootPath("C:/TestPath");
		repository.save(fileStore);

		log.info("Result: {}", fileStore.getId());
	}

}
