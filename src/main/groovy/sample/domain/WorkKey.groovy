package sample.domain

import sample.common.entity.Sequence

class WorkKey implements Sequence<WorkKey>, Comparable<WorkKey> {

    final long hrId
    final long workStatusId


    WorkKey(long hrId, long workStatusId) {
        this.hrId = hrId
        this.workStatusId = workStatusId
    }

    @Override
    boolean equals(Object obj) {
        if (obj == this) return true
        if (!(obj instanceof WorkKey)) return false

        WorkKey key = ((WorkKey) obj)

        return key.hrId == this.hrId && key.workStatusId == this.workStatusId
    }

    @Override
    int hashCode() {
        return (int) (hrId) ^ (int) workStatusId
    }

    @Override
    WorkKey next() {
        return new WorkKey(hrId, workStatusId + 1)
    }

    @Override
    int compareTo(WorkKey o) {
        if (this.hrId > o.hrId) {
            return 1
        } else if (this.hrId < o.hrId) {
            return -1
        } else {
            if (this.workStatusId > o.workStatusId) {
                return 1
            } else if (this.workStatusId < o.workStatusId) {
                return -1
            } else {
                return 0
            }
        }
    }

    @Override
    String toString() {
        return String.valueOf(workStatusId)
    }
}
