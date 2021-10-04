package com.avispa.ecm;

import com.avispa.ecm.model.filestore.FileStore;
import com.avispa.ecm.model.filestore.FileStoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class EcmApplicationTests {

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
