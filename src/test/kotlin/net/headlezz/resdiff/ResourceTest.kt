package net.headlezz.resdiff

import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.dom.elements
import kotlin.dom.parseXml
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResourceTest {

    @Test
    fun match() {
        val resA = StringResource("nameA", "valueA")
        val resB = StringResource("nameA", "valueA")
        assertTrue(resA.match(resB))
    }

    @Test
    fun match_notMatching() {
        val resA = StringResource("nameA", "valueA")
        val resB = StringResource("nameA", "valueB")
        assertFalse(resA.match(resB))
    }

    @Test
    fun ident() {
        val resA = StringResource("nameA", "valueA")
        val resB = StringResource("nameA", "valueA")
        assertTrue(resA.matchIdent(resB))
    }

    @Test
    fun ident_notMatching() {
        val resA = StringResource("nameA", "valueA")
        val resB = StringResource("nameB", "valueA")
        assertFalse(resA.matchIdent(resB))
    }

    @Test
    fun ident_differentResourceTypes() {
        val resA = object: Resource("nameA", "valueA", Resource.Type.Boolean){}
        val resB = object: Resource("nameA", "valueA", Resource.Type.String){}
        assertFalse(resA.matchIdent(resB))
    }

    @Test
    fun fromElement_String() {
        val res = "<string name=\"a name\">a value</string>"
        val elem = parseXml(buildInputStream(res)).elements.first()

        val stringResource = Resource.fromElement(elem)
        assert(stringResource is StringResource)
        assertEquals("a name", stringResource!!.name)
        assertEquals("a value", stringResource.value)
    }

    @Test
    fun fromElement_Boolean() {
        val res = "<bool name=\"a name\">true</bool>"
        val elem = parseXml(buildInputStream(res)).elements.first()

        val booleanResource = Resource.fromElement(elem)
        assert(booleanResource is BooleanResource)
        assertEquals("a name", booleanResource!!.name)
        assertEquals("true", booleanResource.value)
    }

    @Test
    fun fromElement_Integer() {
        val res = "<integer name=\"a name\">-123</integer>"
        val elem = parseXml(buildInputStream(res)).elements.first()

        val intResource = Resource.fromElement(elem)
        assert(intResource is IntegerResource)
        assertEquals("a name", intResource!!.name)
        assertEquals("-123", intResource.value)
    }

    @Test
    fun fromElement_Color() {
        val res = "<color name=\"a name\">#000001</color>"
        val elem = parseXml(buildInputStream(res)).elements.first()

        val colorResource = Resource.fromElement(elem)
        assert(colorResource is ColorResource)
        assertEquals("a name", colorResource!!.name)
        assertEquals("#000001", colorResource.value)
    }

    @Test
    fun fromElement_Dimension() {
        val res = "<dimen name=\"a name\">12dp</dimen>"
        val elem = parseXml(buildInputStream(res)).elements.first()

        val dimensionResource = Resource.fromElement(elem)
        assert(dimensionResource is DimensionResource)
        assertEquals("a name", dimensionResource!!.name)
        assertEquals("12dp", dimensionResource.value)
    }


    fun buildInputStream(value: String) : InputStream {
        return ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8))
    }

}