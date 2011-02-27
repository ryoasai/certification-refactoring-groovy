﻿package sample.common.io

import java.beans.PropertyDescriptor
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.StringTokenizer

import javax.annotation.PostConstruct

import org.apache.commons.lang.ObjectUtils
import org.apache.commons.lang.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.util.ReflectionUtils

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
	
	private BufferedReader reader

	private BufferedWriter writer

	// ====================================================
	// メソッド
	// ====================================================

	@PostConstruct
	void init() {
		if (entityClass != null) return
		
		// 親クラスの総称パラメータの型を取得
		entityClass = (Class<E>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1]
	}
	
	private List<E> doFind(Closure matcher) {
		try {
			List<E> result = new ArrayList<E>()

			openForRead()
			String line

			// マスタから1行ずつ読込み
			while ((line = reader.readLine()) != null) {
				E entity = toEntity(line)
				if (!entity.isLogicalDeleted() && matcher.call(entity)) {

					result.add(entity)
				}
			}

			return result

		} catch (IOException e) {
			throw new SystemException('検索処理実行時にIO例外が発生しました。', e)
		} finally {
			close()
		}
	}

	@Override
	E findById(K id) {
		def result = doFind {
			ObjectUtils.equals(id, it.getId())
		}

		if (result.isEmpty()) {
			throw new EntityNotFoundException('id = ' + id + 'のエンティティは存在しません。')
		}

		// TODO 一意性チェックはしていない
		result.get(0)
	}

	@Override
	List<E> findAll() {
		doFind {
			!it.isLogicalDeleted()
		}
	}

	@Override
	Map<K, E> findAllAsMap() {
		Map<K, E> result = new HashMap<K, E>()
		
		for (E entity: findAll()) {
			result.put(entity.getId(), entity)
		}
		
		return result
	}

	
	@Override
	List<E> findByExample(E example) {

		doFind {
			if (it == null) return false
		
			PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(example.getClass())
			
			for (PropertyDescriptor prop in props) {
				Method readMethod = prop.getReadMethod()
				if (readMethod == null) continue
				if (prop.getName().equals('class') || prop.getName().equals('persisted')) continue
				
				Object exampleValue = ReflectionUtils.invokeMethod(readMethod, example)
				if (exampleValue == null) continue
				if (exampleValue instanceof Long && (Long)(exampleValue) == 0) continue; // 基本型のlongの0は無視（いまいち）
	
				Object targetValue = ReflectionUtils.invokeMethod(readMethod, it)
	
				if (targetValue instanceof String && exampleValue instanceof String) { // 部分文字列一致
					if ( ! ((String)targetValue).contains((String)exampleValue)) return false
				} else {
					if (!ObjectUtils.equals(exampleValue, targetValue)) return false
				}
			}
			
			true
		}
	}

	private void processUpdate(Closure fileUpdator) {
		try {
			openForWrite()

			fileUpdator.call()

		} catch (IOException e) {
			throw new SystemException('削除処理実行時にIO例外が発生しました。', e)
		} finally {
			close()
		}

		commit()
	}

	private void writeEntity(E data) throws IOException {
		String outputLine = fromEntity(data)
		writer.write(outputLine)
		writer.newLine()
	}

	@Override
	void create(final E data) {
		if (data == null) throw new IllegalArgumentException('パラメーターが不正です。')

		processUpdate {
			
			@Override
			void handle() throws IOException {
				String line

				List<K> idList = new ArrayList<K>()
				// マスタから1行ずつ読込み
				while ((line = reader.readLine()) != null) {
					E entity = toEntity(line)
					idList.add(entity.getId())

					writeEntity(entity)
				}

				K maxId = Collections.max(idList)
				data.setId(nextId(maxId))

				data.preCreate(); // 更新、作成日付の発行
				writeEntity(data)
			}

		}
	}

	private K nextId(K maxId){
		if (maxId instanceof Long) {
			Object nextId = (Long)maxId + 1
			return (K) nextId
		} else if (maxId instanceof Sequence) {
			return ((Sequence<K>)maxId).next()
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

		processUpdate {
			@Override
			void handle() throws IOException {
				String line

				// マスタから1行ずつ読込み
				while ((line = reader.readLine()) != null) {
					E entity = toEntity(line)
					if (data.getId().equals(entity.getId())) {
						if (entity.isLogicalDeleted()) { // 既に論理削除済みの場合
							throw new EntityNotFoundException('id = '
									+ entity.getId() + 'のエンティティは既に論理削除されています。')
						}

						data.preUpdate()
						entity = data
					}

					writeEntity(entity)
				}
			}
		}
	}

	@Override
	void delete(final K id) {
		processUpdate {
			@Override
			void handle() throws IOException {
				String line
				boolean deleted = false

				// マスタから1行ずつ読込み
				while ((line = reader.readLine()) != null) {
					E entity = toEntity(line)

					if (ObjectUtils.equals(id, entity.getId())) {
						if (entity.isLogicalDeleted()) { // 既に論理削除済みの場合
							throw new EntityNotFoundException('id = ' + id
									+ 'のエンティティは既に論理削除されています。')
						}

						entity.logicalDelete()
						deleted = true
					}

					writeEntity(entity)
				}

				if (!deleted) {
					// パラメーターで指定されたエンティティが存在しなかった場合
					throw new EntityNotFoundException('id = ' + id
							+ 'のエンティティは存在しません。')
				}
			}
		}
	}

	private String fromEntity(E entity) {
		return StringUtils.join(entity.toArray(), getSeparator())
	}

	private E toEntity(String line) {

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
		StringTokenizer st = new StringTokenizer(line, getSeparator(), true)
		List<String> result = new ArrayList<String>()
		String prevToken = ''
		while (st.hasMoreTokens()) {
			String token = st.nextToken()
			
 			if (prevToken.equals(getSeparator()) && token.equals(getSeparator()) ) {
 				result.add(''); // 区切りが連続する場合は空文字をつめる。
 			} else if (!getSeparator().equals(token)) {
 				result.add(token)
 			}
 			
 			prevToken = token
		}
		
		return result.toArray(new String[result.size()])
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

	// NOTE
	// 本来は全ファイルの内容をメモリ上に読み込んで処理したほうが簡単だが、
	// オリジナルの実装を極力残すことにした。

	private void openForWrite() throws IOException {
		reader = new BufferedReader(new FileReader(masterFile))
		writer = new BufferedWriter(new FileWriter(workFile))
	}

	private void openForRead() throws IOException {
		reader = new BufferedReader(new FileReader(masterFile))
	}

	private void close() {
		if (reader != null) {
			try {
				reader.close()
			} catch (IOException e) {
				e.printStackTrace()
			}
		}

		if (writer != null) {
			try {
				writer.close()
			} catch (IOException e) {
				e.printStackTrace()
			}
		}
	}

	def test2() {
	}
}
