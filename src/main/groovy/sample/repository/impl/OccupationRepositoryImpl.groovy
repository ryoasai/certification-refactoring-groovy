package sample.repository.impl

import java.io.File

import javax.inject.Inject

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

import sample.common.io.CharSeparatedFileRepository
import sample.domain.Occupation
import sample.repository.OccupationRepository

@Repository
class OccupationRepositoryImpl extends CharSeparatedFileRepository<Long, Occupation> implements OccupationRepository {

	@Inject
	OccupationRepositoryImpl(
			@Value('${occupation.master.file}') File masterFile, 
			@Value('${occupation.work.file}') File workFile) {

		setMasterFile(masterFile)
		setWorkFile(workFile)
	}
	

}
