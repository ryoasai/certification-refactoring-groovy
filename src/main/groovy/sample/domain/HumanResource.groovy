package sample.domain

/**
 * 人材エンティティ
 */
class HumanResource extends Party {
    public static final def FIELDS_MAP = [
            //          id: '人材ID',
            name: '氏名',
            postalCode: '郵便番号',
            address: '住所',
            telephoneNo: '電話番号',
            faxNo: 'FAX番号',
            email: 'e-mailアドレス',
            birthDay: '生年月日',
            genderType: '性別',
            occupationId: '業種',
            yearOfExperience: '経験年数',
            schoolBackground: '最終学歴',
            requestedSalary: '希望単価']

    /** 誕生日   */
    String birthDay

    /** 性別   */
    String genderType

    /** 業種ID   */
    long occupationId

    /** 経験年数   */
    String yearOfExperience

    /** 最終学歴   */
    String schoolBackground

    /** 希望単価   */
    String requestedSalary

    // TODO
    // 以下の部分のコードはメタ情報からで自動生成できるはず

    @Override
    String[] toArray() {
        def dataList = [
                id,
                name,
                postalCode,
                address,
                telephoneNo,
                faxNo,
                email,
                birthDay,
                genderType,
                occupationId,
                yearOfExperience,
                schoolBackground,
                requestedSalary]

        dataList + createDateColumns()
    }

    @Override
    void fromArray(String[] data) {
        int i = 0

        id = data[i++]?.toLong()
        name = data[i++]
        postalCode = data[i++]
        address = data[i++]
        telephoneNo = data[i++]
        faxNo = data[i++]
        email = data[i++]
        birthDay = data[i++]
        genderType = data[i++]
        occupationId = data[i++]?.toLong()
        yearOfExperience = data[i++]
        schoolBackground = data[i++]
        requestedSalary = data[i++]

        readMetaCulumns(data, i)
    }

}
