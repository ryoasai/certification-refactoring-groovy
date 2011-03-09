package sample.common.io

import java.lang.reflect.ParameterizedType
import javax.annotation.PostConstruct
import sample.common.SystemException
import sample.common.entity.EntityBase
import sample.common.entity.Sequence

/**
 * 区切り文字を使ったテキストファイル上のデータを読み書きするためのレポジトリー実装クラスです。
 * このクラスはステートを持ちスレッドセーフではない点に注意すること。
 */
class CharSeparatedFileRepository<K extends Comparable<K>, E extends EntityBase<K>> implements
Repository<K, E> {

    // ====================================================
    // フィールド
    // ====================================================

    File masterFile

    File workFile

    String separator = '\t'

    Class<E> entityClass

    String encoding = 'utf-8'

    // ====================================================
    // メソッド
    // ====================================================

    @PostConstruct
    void init() {
        if (entityClass != null) return

        // 親クラスの総称パラメータの型を取得
        entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1]
    }

    private List<E> doFind(Closure matcher) {
        try {
            List<E> result = []

            // マスタから1行ずつ読込み
            masterFile.eachLine(encoding) { line ->

                E entity = toEntity(line)
                if (!entity.logicalDeleted && matcher.call(entity)) {
                    result.add(entity)
                }
            }

            return result

        } catch (IOException e) {
            throw new SystemException('検索処理実行時にIO例外が発生しました。', e)
        }
    }

    @Override
    E findById(K id) {
        def result = doFind {
            id == it.id
        }

        if (result.isEmpty()) {
            throw new EntityNotFoundException("id = ${id}のエンティティは存在しません。")
        }

        // TODO 一意性チェックはしていない
        result.get(0)
    }

    @Override
    List<E> findAll() {
        doFind {
            !it.logicalDeleted
        }
    }

    @Override
    Map<K, E> findAllAsMap() {
        def result = [:]

        findAll().each {entity ->
            result[entity] = entity
        }

        result
    }


    @Override
    List<E> findByExample(E example) {

        doFind {
            if (it == null) return false

            def hasUnequalValue = example.properties.any { key, value ->
                // 以下のプロパティ値は比較対象外とする
                if (key == 'class' ||
                        key == 'metaClass' ||
                        key == 'persisted') return false

                if (value == null) return false
                if (value == 0L) return false // 基本型のlongの0は無視（いまいち）

                def targetValue = it[key]

                if (targetValue instanceof String && value instanceof String) {
                    !targetValue.contains(value) // 文字列は部分一致
                } else {
                    value != targetValue // その他の値は完全一致
                }
            }

            !hasUnequalValue
        }
    }

    void processUpdate(Closure fileUpdator) {
        try {
            workFile.withWriter(encoding) { writer ->
                fileUpdator.call(writer)
            }

        } catch (IOException e) {
            throw new SystemException('削除処理実行時にIO例外が発生しました。', e)
        }

        commit()
    }

    void writeEntity(E data, Writer writer) throws IOException {
        String outputLine = fromEntity(data)
        writer.write(outputLine)
        writer.newLine()
    }

    @Override
    void create(final E data) {
        if (data == null) throw new IllegalArgumentException('パラメーターが不正です。')

        processUpdate { writer ->

            List<K> idList = []
            // マスタから1行ずつ読込み
            masterFile.eachLine(encoding) { line ->
                E entity = toEntity(line)
                idList.add(entity.getId())

                writeEntity(entity, writer)
            }

            K maxId = Collections.max(idList)
            data.setId(nextId(maxId))

            data.preCreate(); // 更新、作成日付の発行
            writeEntity(data, writer)
        }
    }

    K nextId(K maxId) {
        if (maxId instanceof Long) {
            maxId + 1
        } else if (maxId instanceof Sequence) {
            maxId.next()
        } else {
            throw new IllegalArgumentException('自動採番できません。')
        }
    }


    @Override
    void update(final E data) {
        if (data == null)
            throw new IllegalArgumentException('パラメーターが不正です。')
        if (!data.isPersisted())
            throw new IllegalArgumentException('パラメーターが永続化されていません。')

        processUpdate {writer ->

            // マスタから1行ずつ読込み
            masterFile.eachLine(encoding) { line ->
                E entity = toEntity(line)
                if (data.id == entity.id) {
                    if (entity.logicalDeleted) { // 既に論理削除済みの場合
                        throw new EntityNotFoundException("id = ${entity.id}のエンティティは既に論理削除されています。")
                    }

                    data.preUpdate()
                    entity = data
                }

                writeEntity(entity, writer)
            }
        }
    }

    @Override
    void delete(final K id) {
        processUpdate { writer ->
            boolean deleted = false

            // マスタから1行ずつ読込み
            masterFile.eachLine(encoding) { line ->
                E entity = toEntity(line)

                if (id == entity.id) {
                    if (entity.logicalDeleted) { // 既に論理削除済みの場合
                        throw new EntityNotFoundException("id = ${id}のエンティティは既に論理削除されています。")
                    }

                    entity.logicalDelete()
                    deleted = true
                }

                writeEntity(entity, writer)
            }

            if (!deleted) {
                // パラメーターで指定されたエンティティが存在しなかった場合
                throw new EntityNotFoundException("id = ${id}のエンティティは存在しません。")
            }
        }
    }

    String fromEntity(E entity) {
        entity.toArray().join(getSeparator())
    }

    E toEntity(String line) {

        try {
            E entity = entityClass.newInstance()
            String[] data = parseLine(line)

            entity.fromArray(data)

            return entity
        } catch (InstantiationException e) {
            throw new SystemException('エンティティの復元時に例外が発生しました。', e)
        } catch (IllegalAccessException e) {
            throw new SystemException('エンティティの復元時に例外が発生しました。', e)
        }
    }

    protected String[] parseLine(String line) {
        def st = new StringTokenizer(line, getSeparator(), true)
        def result = []
        def prevToken = ''
        while (st.hasMoreTokens()) {
            def token = st.nextToken()

            if ((prevToken == getSeparator()) && token == getSeparator()) {
                result << '' // 区切りが連続する場合は空文字をつめる。
            } else if (!getSeparator().equals(token)) {
                result << token
            }

            prevToken = token
        }

        result
    }

    private void commit() {
        try {
            if (!masterFile.delete()) {
                throw new IOException()
            }

            // テンポラリーファイルをマスタに置換え
            workFile.renameTo(masterFile)

        } catch (IOException e) {
            throw new SystemException('ワークファイルの変更をマスターファイルに反映できません。', e)
        }
    }

}
