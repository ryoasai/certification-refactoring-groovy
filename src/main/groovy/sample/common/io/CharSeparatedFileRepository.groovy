package sample.common.io

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
 * ��؂蕶�����g�����e�L�X�g�t�@�C����̃f�[�^��ǂݏ������邽�߂̃��|�W�g���[�����N���X�ł��B
 * ���̃N���X�̓X�e�[�g�������X���b�h�Z�[�t�ł͂Ȃ��_�ɒ��ӂ��邱�ƁB
 */
class CharSeparatedFileRepository<K extends Comparable<K>, E extends EntityBase<K>> implements
		Repository<K, E> {

	// ====================================================
	// �t�B�[���h
	// ====================================================

	File masterFile

	File workFile

	String separator = '\t'

	Class<E> entityClass
	
	private BufferedReader reader

	private BufferedWriter writer

	// ====================================================
	// ���\�b�h
	// ====================================================

	@PostConstruct
	void init() {
		if (entityClass != null) return
		
		// �e�N���X�̑��̃p�����[�^�̌^���擾
		entityClass = (Class<E>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1]
	}
	
	private List<E> doFind(Closure matcher) {
		try {
			List<E> result = new ArrayList<E>()

			openForRead()
			String line

			// �}�X�^����1�s���Ǎ���
			while ((line = reader.readLine()) != null) {
				E entity = toEntity(line)
				if (!entity.isLogicalDeleted() && matcher.call(entity)) {

					result.add(entity)
				}
			}

			return result

		} catch (IOException e) {
			throw new SystemException('�����������s����IO��O���������܂����B', e)
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
			throw new EntityNotFoundException('id = ' + id + '�̃G���e�B�e�B�͑��݂��܂���B')
		}

		// TODO ��Ӑ��`�F�b�N�͂��Ă��Ȃ�
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
				if (exampleValue instanceof Long && (Long)(exampleValue) == 0) continue; // ��{�^��long��0�͖����i���܂����j
	
				Object targetValue = ReflectionUtils.invokeMethod(readMethod, it)
	
				if (targetValue instanceof String && exampleValue instanceof String) { // �����������v
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
			throw new SystemException('�폜�������s����IO��O���������܂����B', e)
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
		if (data == null) throw new IllegalArgumentException('�p�����[�^�[���s���ł��B')

		processUpdate {
			
			@Override
			void handle() throws IOException {
				String line

				List<K> idList = new ArrayList<K>()
				// �}�X�^����1�s���Ǎ���
				while ((line = reader.readLine()) != null) {
					E entity = toEntity(line)
					idList.add(entity.getId())

					writeEntity(entity)
				}

				K maxId = Collections.max(idList)
				data.setId(nextId(maxId))

				data.preCreate(); // �X�V�A�쐬���t�̔��s
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
			throw new IllegalArgumentException('�����̔Ԃł��܂���B')
		}
	}

	
	@Override
	void update(final E data) {
		if (data == null)
			throw new IllegalArgumentException('�p�����[�^�[���s���ł��B')
		if (!data.isPersisted())
			throw new IllegalArgumentException('�p�����[�^�[���i��������Ă��܂���B')

		processUpdate {
			@Override
			void handle() throws IOException {
				String line

				// �}�X�^����1�s���Ǎ���
				while ((line = reader.readLine()) != null) {
					E entity = toEntity(line)
					if (data.getId().equals(entity.getId())) {
						if (entity.isLogicalDeleted()) { // ���ɘ_���폜�ς݂̏ꍇ
							throw new EntityNotFoundException('id = '
									+ entity.getId() + '�̃G���e�B�e�B�͊��ɘ_���폜����Ă��܂��B')
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

				// �}�X�^����1�s���Ǎ���
				while ((line = reader.readLine()) != null) {
					E entity = toEntity(line)

					if (ObjectUtils.equals(id, entity.getId())) {
						if (entity.isLogicalDeleted()) { // ���ɘ_���폜�ς݂̏ꍇ
							throw new EntityNotFoundException('id = ' + id
									+ '�̃G���e�B�e�B�͊��ɘ_���폜����Ă��܂��B')
						}

						entity.logicalDelete()
						deleted = true
					}

					writeEntity(entity)
				}

				if (!deleted) {
					// �p�����[�^�[�Ŏw�肳�ꂽ�G���e�B�e�B�����݂��Ȃ������ꍇ
					throw new EntityNotFoundException('id = ' + id
							+ '�̃G���e�B�e�B�͑��݂��܂���B')
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
			throw new SystemException('�G���e�B�e�B�̕������ɗ�O���������܂����B', e)
		} catch (IllegalAccessException e) {
			throw new SystemException('�G���e�B�e�B�̕������ɗ�O���������܂����B', e)
		}
	}

	protected String[] parseLine(String line) {
		StringTokenizer st = new StringTokenizer(line, getSeparator(), true)
		List<String> result = new ArrayList<String>()
		String prevToken = ''
		while (st.hasMoreTokens()) {
			String token = st.nextToken()
			
 			if (prevToken.equals(getSeparator()) && token.equals(getSeparator()) ) {
 				result.add(''); // ��؂肪�A������ꍇ�͋󕶎����߂�B
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

			// �e���|�����[�t�@�C�����}�X�^�ɒu����
			workFile.renameTo(masterFile)

		} catch (IOException e) {
			throw new SystemException('���[�N�t�@�C���̕ύX���}�X�^�[�t�@�C���ɔ��f�ł��܂���B', e)
		}
	}

	// NOTE
	// �{���͑S�t�@�C���̓��e����������ɓǂݍ���ŏ��������ق����ȒP�����A
	// �I���W�i���̎������ɗ͎c�����Ƃɂ����B

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

}
