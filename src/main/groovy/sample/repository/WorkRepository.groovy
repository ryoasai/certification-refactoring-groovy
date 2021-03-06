package sample.repository

import sample.common.io.Repository
import sample.domain.Work
import sample.domain.WorkKey

interface WorkRepository extends Repository<WorkKey, Work> {

}
