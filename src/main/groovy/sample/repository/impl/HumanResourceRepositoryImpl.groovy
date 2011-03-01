package sample.repository.impl

import javax.inject.Inject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import sample.common.io.CharSeparatedFileRepository
import sample.domain.HumanResource
import sample.repository.HumanResourceRepository

@Repository
class HumanResourceRepositoryImpl extends CharSeparatedFileRepository<Long, HumanResource> implements HumanResourceRepository {

	@Inject
	HumanResourceRepositoryImpl(
			@Value('${hr.master.file}') File masterFile, 
			@Value('${hr.work.file}') File workFile) {

		setMasterFile(masterFile)
		setWorkFile(workFile)
	}
}
