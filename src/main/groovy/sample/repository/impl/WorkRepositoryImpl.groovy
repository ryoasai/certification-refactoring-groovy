package sample.repository.impl

import javax.inject.Inject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import sample.common.io.CharSeparatedFileRepository
import sample.domain.Work
import sample.domain.WorkKey
import sample.repository.WorkRepository

@Repository
class WorkRepositoryImpl extends CharSeparatedFileRepository<WorkKey, Work> implements WorkRepository {

    @Inject WorkRepositoryImpl(
    @Value('${work.master.file}') File masterFile,
    @Value('${work.work.file}') File workFile) {

        setMasterFile(masterFile)
        setWorkFile(workFile)

        println(masterFile)
        println(workFile)
    }
}

