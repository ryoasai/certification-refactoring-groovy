package sample.common

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertArrayEquals
import static junit.framework.Assert.assertEquals

public class EntityBaseTest {

    SampleEntity sample = new SampleEntity()

    @Before
    public void setUp() throws Exception {
    }

    @Test
    void logicalDelete() {
        assertFalse(sample.isLogicalDeleted())

        sample.logicalDelete()
        assertTrue(sample.isLogicalDeleted())
    }

    @Test(expected = IllegalStateException.class)
    void logicalDelete_AlreadyDeleted() {
        assertFalse(sample.isLogicalDeleted())

        sample.logicalDelete()

        // 既に論理削除済みの場合例外となる。
        sample.logicalDelete()
    }

    @Test
    void toArray() {
        sample.id = 1L
        sample.name = 'test'
        sample.age = '30'
        sample.preCreate()

        def array = sample.toArray()
        println array

        assertEquals('1', array[0])
        assertEquals('test', array[1])
        assertEquals('30', array[2])
    }


    @Test
    void fromArray() {

        sample.fromArray(['3', 'test', '50'] as String[])
        println sample

        assertEquals(3L, sample.id)
        assertEquals('test', sample.name)
        assertEquals('50', sample.age)
    }

}
