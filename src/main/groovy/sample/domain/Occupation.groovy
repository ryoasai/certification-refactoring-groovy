package sample.domain

import sample.common.entity.EntityBase
import sample.common.entity.NameId

class Occupation extends EntityBase<Long> implements NameId<Long> {

    /** 業種名  */
    String name

    @Override
    String[] toArray() {
        def dataList = [id, name]
        dataList + createDateColumns()
    }


    @Override
    void fromArray(String[] data) {
        int i = 0

        id = data[i++].toLong()
        name = data[i++]

        readMetaCulumns(data, i)
    }
}
