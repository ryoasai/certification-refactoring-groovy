package sample.common.io;


import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import sample.common.SampleEntity
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

public class CharSeparatedFilePersisterTest {

	// FIXME 実行時のワークディレクトリーがワークスペースのルートである前提
	private static final String SP = SystemUtils.FILE_SEPARATOR
	private static final String DATA_DIR = SystemUtils.USER_DIR + SP + 'out' + SP + 'test' + SP + 'certification-refactoring-groovy'

	def target = new CharSeparatedFileRepository<Long, SampleEntity>()

	File masterFile
	File workFile
	
	@Before
	void setUp() throws Exception {
		// テスト実行後に復元しやすいようにもともとのデータをコピーしてから操作する。
		File originalFile = new File(DATA_DIR, 'sample.txt')
		
		masterFile = new File(DATA_DIR, 'test.txt')
		workFile = new File(DATA_DIR, 'test.tmp')

		FileUtils.copyFile(originalFile, masterFile)

		target.masterFile = masterFile
		target.workFile = workFile
		target.entityClass = SampleEntity
	}

	@After
	public void tearDown() throws Exception {
//		masterFile.delete()
//		tempFile.delete()
	}

	//====================================================
	// 検索関連
	//====================================================

	@Test
	public void findAll() {
		def sampleList = target.findAll()
		assertEquals(2, sampleList.size())
		
		assertEquals(1L, sampleList[0].id)
		assertEquals('20', sampleList[0].age)
		assertEquals('Name1', sampleList[0].name)

		assertEquals(3L, sampleList[1].id)
		assertEquals('40', sampleList[1].age)
		assertEquals('Name3', sampleList[1].name)
	}


	@Test
	public void findById() {
		def sample = target.findById(1L)
		
		assertEquals(1L, sample.id)
		assertEquals('20', sample.age)
		assertEquals('Name1', sample.name)
	}
	
	@Test
	public void findById_LogicalDeleted() {
		try {
			target.findById(2L)
		} catch (EntityNotFoundException ex) {
			assertEquals('id = 2のエンティティは存在しません。', ex.getMessage())
		}	
	}
	
	@Test
	public void findById_NotFound() {
		try {
			target.findById(100L)
		} catch (EntityNotFoundException ex) {
			assertEquals('id = 100のエンティティは存在しません。', ex.getMessage())
		}	
	}

	@Test
	public void findByExample() {
		SampleEntity example = new SampleEntity()
		example.age = '20'
		
		def sampleList = target.findByExample(example)

		assertEquals(1, sampleList.size())

		assertEquals(1L, sampleList[0].id)
		assertEquals('20', sampleList[0].age)
		assertEquals('Name1', sampleList[0].name)
	}

	//====================================================
	// 作成関連
	//====================================================
	
	@Test
	public void create_Normal() {
		def example = new SampleEntity()
		example.age = '50'
		example.name = 'test'
		
		target.create(example)
		
		def entityList = target.findAll()
		assertEquals(3, entityList.size())
		
		SampleEntity addedEntity = entityList[entityList.size() - 1]
		assertEquals(4L, addedEntity.id)
		assertEquals('50', addedEntity.age)
		assertEquals('test', addedEntity.name)
		
		assertNotNull(addedEntity.createDate)
		assertNotNull(addedEntity.updateDate)
	}
	
	//====================================================
	// 更新関連
	//====================================================
	
	@Test
	public void update_Normal() {
		def sample = new SampleEntity()
		sample.id = 3L
		sample.age = '10'
		sample.name = 'test'
		sample.createDate = new Date()
		
		target.update(sample)
		
		def updatedEntity = target.findById(3L)
		
		assertEquals(3L, updatedEntity.id)
		assertEquals('10', updatedEntity.age)
		assertEquals('test', updatedEntity.name)
		assertNotNull(updatedEntity.createDate)
		assertNotNull(updatedEntity.updateDate)
	}

	@Test
	public void update_AlreadyLogicalDeleted() {
		def sample = new SampleEntity()
		sample.id = 2L
		sample.age = '10'
		sample.name = 'test'
		sample.createDate = new Date()
		
		try {
			target.update(sample)
		} catch (EntityNotFoundException ex) {
			assertEquals('id = 2のエンティティは既に論理削除されています。', ex.getMessage())
		}
	}
	
	//====================================================
	// 削除関連
	//====================================================

	@Test
	public void delete_Normal() {
		target.delete(1L)
	}
	
	@Test
	public void delete_AlreadyLogicalDeleted() {
		try {
			target.delete(2L)
		} catch (EntityNotFoundException ex) {
			assertEquals('id = 2のエンティティは既に論理削除されています。', ex.getMessage())
			
		}
	}
	
	@Test
	public void delete_NotFound() {
		try {
			target.delete(100L)
		} catch (EntityNotFoundException ex) {
			assertEquals('id = 100のエンティティは存在しません。', ex.getMessage())
		}
	}

}
