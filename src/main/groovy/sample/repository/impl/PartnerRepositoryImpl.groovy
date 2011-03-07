package sample.repository.impl

import javax.inject.Inject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import sample.common.io.CharSeparatedFileRepository
import sample.domain.Partner
import sample.repository.PartnerRepository

@Repository
class PartnerRepositoryImpl extends CharSeparatedFileRepository<Long, Partner> implements PartnerRepository {

    @Inject PartnerRepositoryImpl(
    @Value('${partner.master.file}') File masterFile,
    @Value('${partner.work.file}') File workFile) {

        setMasterFile(masterFile)
        setWorkFile(workFile)
    }
}

