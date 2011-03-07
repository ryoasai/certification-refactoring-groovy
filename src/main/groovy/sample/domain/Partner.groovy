package sample.domain

class Partner extends Party {

    /**
     * URL
     */
    String url

    /**
     * 担当者
     */
    String personInCharge

    /**
     * 締め日
     */
    String cutoffDay

    /**
     * 支払い日
     */
    String paymentDay

    @Override
    String[] toArray() {

        def dataList = [
                id,
                name,
                postalCode,
                address,
                telephoneNo,
                faxNo,
                url,
                personInCharge,
                email,
                cutoffDay,
                paymentDay]

        dataList + createDateColumns()
    }


    @Override
    void fromArray(String[] data) {
        int i = 0

        id = data[i++].toLong()
        name = data[i++]
        postalCode = data[i++]
        address = data[i++]
        telephoneNo = data[i++]
        faxNo = data[i++]
        url = data[i++]
        email = data[i++]
        personInCharge = data[i++]
        cutoffDay = data[i++]
        paymentDay = data[i++]

        readMetaCulumns(data, i)
    }

}
